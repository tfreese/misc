// Created: 27.03.2018
package de.freese.maven.proxy.netty.initializer;

import java.util.List;
import java.util.Objects;
import de.freese.maven.proxy.MavenProxy;
import de.freese.maven.proxy.netty.handler.NettyMavenRequestHandler;
import de.freese.maven.proxy.repository.file.FileRepository;
import de.freese.maven.proxy.repository.http.HttpRepository;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * {@link ChannelInitializer} für den {@link MavenProxy}.
 *
 * @author Thomas Freese
 */
public class NettyMavenInitializer extends ChannelInitializer<SocketChannel>
{
    /**
    *
    */
    private final FileRepository fileRepository;

    /**
    *
    */
    private final List<HttpRepository> httpRepositories;

    /**
    *
    */
    private final SslContext sslContext;

    /**
     * Erzeugt eine neue Instanz von {@link NettyMavenInitializer}.
     *
     * @param fileRepository {@link FileRepository}
     * @param httpRepositories {@link List}
     */
    public NettyMavenInitializer(final FileRepository fileRepository, final List<HttpRepository> httpRepositories)
    {
        super();

        this.fileRepository = Objects.requireNonNull(fileRepository, "fileRepository required");
        this.httpRepositories = Objects.requireNonNull(httpRepositories, "httpRepositories required");
        this.sslContext = null;
    }

    /**
     * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
     */
    @Override
    protected void initChannel(final SocketChannel ch) throws Exception
    {
        ChannelPipeline pipeline = ch.pipeline();

        if (this.sslContext != null)
        {
            pipeline.addLast(this.sslContext.newHandler(ch.alloc()));
        }

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536)); // Keine Chunks
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new NettyMavenRequestHandler(this.fileRepository, this.httpRepositories));
    }
}
