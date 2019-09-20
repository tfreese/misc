// Created: 27.03.2018
package de.freese.maven.proxy.netty.initializer;

import java.util.Objects;
import de.freese.maven.proxy.blobstore.BlobStore;
import de.freese.maven.proxy.netty.handler.NettyMavenRequestHandler;
import de.freese.maven.proxy.repository.RemoteRepository;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * {@link ChannelInitializer} für den Maven Proxy.<br>
 * curl -v localhost:8085/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.pom<br>
 * curl -v -X GET localhost:8085/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.pom<br>
 * curl -v -X PUT localhost:8085 -d "..."<br>
 *
 * @author Thomas Freese
 */
public class NettyMavenInitializer extends ChannelInitializer<SocketChannel>
{
    /**
    *
    */
    private final BlobStore blobStore;

    /**
    *
    */
    private final RemoteRepository remoteRepository;

    /**
    *
    */
    private final SslContext sslContext;

    /**
     * Erzeugt eine neue Instanz von {@link NettyMavenInitializer}.
     *
     * @param blobStore {@link BlobStore}
     * @param remoteRepository {@link RemoteRepository}
     */
    public NettyMavenInitializer(final BlobStore blobStore, final RemoteRepository remoteRepository)
    {
        super();

        this.blobStore = Objects.requireNonNull(blobStore, "blobStore required");
        this.remoteRepository = Objects.requireNonNull(remoteRepository, "repository required");
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
        pipeline.addLast(new NettyMavenRequestHandler(this.blobStore, this.remoteRepository));
    }
}
