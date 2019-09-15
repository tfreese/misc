// Created: 27.03.2018
package de.freese.maven.proxy.netty.codec;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.maven.proxy.model.MavenRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * {@link ByteToMessageDecoder} für Maven HTTP Requests.
 *
 * @author Thomas Freese
 */
public class NettyMavenProtocolDecoder extends ByteToMessageDecoder
{
    /**
     *
     */
    private final Charset charset;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erzeugt eine neue Instanz von {@link NettyMavenProtocolDecoder}.
     *
     * @param charset {@link Charset}
     */
    public NettyMavenProtocolDecoder(final Charset charset)
    {
        super();

        this.charset = Objects.requireNonNull(charset, "charset required");
    }

    /**
     * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     */
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug(in.toString());
        }

        MavenRequest mavenRequest = null;

        try (BufferedReader reader = new BufferedReader(new StringReader(in.toString(this.charset))))
        {
            mavenRequest = MavenRequest.create(reader);
        }

        // Sonst kommt die Exception "did not read anything but decoded a message".
        in.resetWriterIndex();

        // ctx.write(mavenRequest);
        out.add(mavenRequest);
    }

    /**
     * @return {@link Logger}
     */
    private Logger getLogger()
    {
        return this.logger;
    }
}
