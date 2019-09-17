// Created: 27.03.2018
package de.freese.maven.proxy.netty;

import java.util.List;
import java.util.concurrent.Executor;
import de.freese.maven.proxy.AbstractMavenProxy;
import de.freese.maven.proxy.MavenProxy;
import de.freese.maven.proxy.netty.initializer.NettyMavenInitializer;
import de.freese.maven.proxy.repository.file.FileRepository;
import de.freese.maven.proxy.repository.http.HttpRepository;
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
    private EventLoopGroup acceptorGroup = null;

    /**
     *
     */
    private EventLoopGroup workerGroup = null;

    /**
     * Erzeugt eine neue Instanz von {@link NettyMavenProxy}.
     *
     * @param executor {@link Executor}
     * @param fileRepository {@link FileRepository}
     * @param httpRepositories {@link List}
     */
    public NettyMavenProxy(final Executor executor, final FileRepository fileRepository, final List<HttpRepository> httpRepositories)
    {
        super(executor, fileRepository, httpRepositories);
    }

    /**
     * @see de.freese.maven.proxy.MavenProxy#shutdown()
     */
    @Override
    public void shutdown()
    {
        getLogger().info("shutdown");

        if (this.acceptorGroup != null)
        {
            this.acceptorGroup.shutdownGracefully();
        }

        if (this.workerGroup != null)
        {
            this.workerGroup.shutdownGracefully();
        }
    }

    /**
     * @see de.freese.maven.proxy.MavenProxy#start()
     */
    @Override
    public void start()
    {
        getLogger().info("start");

        try
        {
            ServerBootstrap bootstrap = new ServerBootstrap();

            this.acceptorGroup = new NioEventLoopGroup(8, getExecutor());
            this.workerGroup = new NioEventLoopGroup(8, getExecutor());

            // @formatter:off
            bootstrap.group(this.acceptorGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new NettyMavenInitializer(getFileRepository(), getHttpRepositories()));
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
        // shutdown()
        // }
    }
}
