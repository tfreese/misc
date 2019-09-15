// Created: 27.03.2018
package de.freese.maven.proxy.netty.initializer;

import java.nio.charset.Charset;
import java.util.Objects;
import de.freese.maven.proxy.MavenProxy;
import de.freese.maven.proxy.netty.codec.NettyMavenProtocolDecoder;
import de.freese.maven.proxy.netty.codec.NettyMavenProtocolEncoder;
import de.freese.maven.proxy.netty.handler.NettyMavenRequestHandler;
import de.freese.maven.proxy.repository.Repository;
import de.freese.maven.proxy.repository.file.FileRepository;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

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
    private final Charset charset;

    /**
    *
    */
    private final FileRepository fileRepository;

    /**
    *
    */
    private final Repository repository;

    /**
     * Erzeugt eine neue Instanz von {@link NettyMavenInitializer}.
     *
     * @param repository {@link Repository}
     * @param charset {@link Charset}
     * @param fileRepository {@link FileRepository}
     */
    public NettyMavenInitializer(final Repository repository, final Charset charset, final FileRepository fileRepository)
    {
        super();

        this.repository = Objects.requireNonNull(repository, "repository required");
        this.charset = Objects.requireNonNull(charset, "charset required");
        this.fileRepository = Objects.requireNonNull(fileRepository, "fileRepository required");
    }

    /**
     * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
     */
    @Override
    protected void initChannel(final SocketChannel ch) throws Exception
    {
        ChannelPipeline p = ch.pipeline();

        // p.addLast(new HttpRequestDecoder());
        p.addLast(new NettyMavenProtocolDecoder(this.charset));

        // Uncomment the following line if you don't want to handle HttpChunks.
        // p.addLast(new HttpObjectAggregator(1048576));

        // p.addLast(new HttpResponseEncoder());
        p.addLast(new NettyMavenProtocolEncoder(this.charset));

        // Remove the following line if you don't want automatic content compression.
        // p.addLast(new HttpContentCompressor());
        p.addLast(new NettyMavenRequestHandler(this.repository, this.fileRepository));
    }
}
