// Created: 27.03.2018
package de.freese.maven.proxy.netty;

import java.util.concurrent.Executor;
import de.freese.maven.proxy.AbstractMavenProxy;
import de.freese.maven.proxy.MavenProxy;
import de.freese.maven.proxy.netty.initializer.NettyMavenInitializer;
import de.freese.maven.proxy.repository.Repository;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * {@link MavenProxy} mit dem netty-Framework.<br>
 * Maven Konfiguration:
 *
 * <pre>
 * &lt;mirror&gt;
 *  &lt;mirrorOf&gt;*&lt;/mirrorOf&gt;
 *  &lt;id&gt;myProxy&lt;/id>&gt;
 *  &lt;name&gt;myProxy&lt;/name&gt;
 *  &lt;url&gt;http://localhost:8080&lt;/url&gt;
 * &lt;/mirror&gt;
 * </pre>
 *
 * @author Thomas Freese
 */
public class NettyMavenProxy extends AbstractMavenProxy
{
    /**
     *
     */
    private final EventLoopGroup acceptorGroup;

    /**
     *
     */
    private final EventLoopGroup workerGroup;

    /**
     * Erzeugt eine neue Instanz von {@link NettyMavenProxy}.
     *
     * @param repository {@link Repository}
     * @param executor {@link Executor}
     */
    public NettyMavenProxy(final Repository repository, final Executor executor)
    {
        super(repository, executor);

        this.acceptorGroup = new NioEventLoopGroup(2, executor);
        this.workerGroup = new NioEventLoopGroup(2, executor);
    }

    /**
     * @see de.freese.maven.proxy.MavenProxy#shutdown()
     */
    @Override
    public void shutdown()
    {
        getLogger().info(null);

        this.acceptorGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }

    /**
     * @see de.freese.maven.proxy.MavenProxy#start()
     */
    @Override
    public void start()
    {
        try
        {
            ServerBootstrap bootstrap = new ServerBootstrap();

            // @formatter:off
            bootstrap.group(this.acceptorGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new NettyMavenInitializer(getRepository(), getCharset()));
            //  @formatter:off

            ChannelFuture ch = bootstrap.bind(getPort());

            ch.channel().closeFuture().sync();
        }
        catch (Exception ex)
        {
            if (ex instanceof RuntimeException)
            {
                throw (RuntimeException) ex;
            }

            throw new RuntimeException(ex);
        }
        // finally
        // {
        // this.bossGroup.shutdownGracefully();
        // this.workerGroup.shutdownGracefully();
        // }
    }
}
