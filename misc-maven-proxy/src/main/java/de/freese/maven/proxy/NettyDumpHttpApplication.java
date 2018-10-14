/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Ausgabe aller HTTP-Requests.<br>
 *
 * @author Thomas Freese
 */
public class NettyDumpHttpApplication
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        int port = 8080;

        // Charset charset = Charset.forName("ISO-8859-1");

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        final EventLoopGroup acceptorGroup = new NioEventLoopGroup(2, executorService);
        final EventLoopGroup workerGroup = new NioEventLoopGroup(2, executorService);

        ServerBootstrap bootstrap = new ServerBootstrap();

        // @formatter:off
        bootstrap.group(acceptorGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO)) // Dump Socket-Request
            .childHandler(new ChannelInitializer<SocketChannel>()
                    {
                        /**
                         *
                         * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
                         */
                        @Override
                        protected void initChannel(final SocketChannel ch) throws Exception
                        {
                            ChannelPipeline p = ch.pipeline();

                             p.addLast(new HttpRequestDecoder());
                             p.addLast(new LoggingHandler(LogLevel.INFO)); // Dump HTTP-Request
                             p.addLast(new HttpResponseEncoder());
                             p.addLast(new ChannelInboundHandlerAdapter()
                                     {

                                        /**
                                         * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
                                         */
                                        @Override
                                        public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
                                        {
                                            DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SERVICE_UNAVAILABLE);

                                            ctx.writeAndFlush(response);
                                        }
                                     });
                        }
                    })
            ;
        //  @formatter:off

        ChannelFuture ch = bootstrap.bind(port);

        ch.channel().closeFuture().sync();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            acceptorGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            executorService.shutdown();

            while (!executorService.isTerminated())
            {
                try
                {
                    executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                }
                catch (InterruptedException ex)
                {
                    // Ignore
                }
            }

            System.exit(0);
        }));
    }
}
