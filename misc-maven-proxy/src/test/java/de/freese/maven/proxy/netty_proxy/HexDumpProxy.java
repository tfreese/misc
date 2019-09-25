/**
 * Created: 25.09.2019
 */

package de.freese.maven.proxy.netty_proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Thomas Freese
 */
public final class HexDumpProxy
{

    /**
     *
     */
    static final int LOCAL_PORT = Integer.parseInt(System.getProperty("localPort", "8443"));

    /**
     *
     */
    static final String REMOTE_HOST = System.getProperty("remoteHost", "www.google.com");

    /**
     *
     */
    static final int REMOTE_PORT = Integer.parseInt(System.getProperty("remotePort", "443"));

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        System.err.println("Proxying *:" + LOCAL_PORT + " to " + REMOTE_HOST + ':' + REMOTE_PORT + " ...");

        // Configure the bootstrap.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try
        {
            ServerBootstrap b = new ServerBootstrap();

            // @formatter:off
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new HexDumpProxyInitializer(REMOTE_HOST, REMOTE_PORT))
                .childOption(ChannelOption.AUTO_READ, false)
                .bind(LOCAL_PORT).sync()
                .channel().closeFuture().sync();
            // @formatter:on
        }
        finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
