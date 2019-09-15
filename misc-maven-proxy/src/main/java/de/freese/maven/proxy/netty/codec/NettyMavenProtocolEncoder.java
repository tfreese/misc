// Created: 27.03.2018
package de.freese.maven.proxy.netty.codec;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.maven.proxy.model.MavenResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * {@link MessageToMessageEncoder} für Maven HTTP Response.
 *
 * @author Thomas Freese
 */
public class NettyMavenProtocolEncoder extends MessageToMessageEncoder<MavenResponse>
{
    /**
     * (0x0D, 0x0A), (13,10), (\r\n)
     */
    private static final byte[] CRLF = new byte[]
    {
            0x0D, 0x0A
    };

    /**
    *
    */
    private final Charset charset;

    // /**
    // *
    // */
    // private final CharsetEncoder charsetEncoder;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erzeugt eine neue Instanz von {@link NettyMavenProtocolEncoder}.
     *
     * @param charset {@link Charset}
     */
    public NettyMavenProtocolEncoder(final Charset charset)
    {
        super();

        this.charset = Objects.requireNonNull(charset, "charset required");
        // this.charsetEncoder = charset.newEncoder();
    }

    /**
     * @see io.netty.handler.codec.MessageToMessageEncoder#encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, java.util.List)
     */
    @Override
    protected void encode(final ChannelHandlerContext ctx, final MavenResponse msg, final List<Object> out) throws Exception
    {
        MavenResponse mavenResponse = msg;

        int contentLengthResource = mavenResponse.getResourceLength();
        int contentLengthHeader = mavenResponse.getContentLength();

        if (contentLengthResource != contentLengthHeader)
        {
            mavenResponse.setContentLengthValue(Integer.toString(Math.max(contentLengthResource, contentLengthHeader)));

            if (getLogger().isDebugEnabled())
            {
                getLogger().debug("ContentLength: Resource={}, Header={}", Integer.valueOf(contentLengthResource), Integer.valueOf(contentLengthHeader));
            }
        }

        ByteBuf buf = ctx.alloc().buffer(1024);

        // HTTP/1.1 200 OK
        buf.writeCharSequence(mavenResponse.getHttpProtocol() + " " + mavenResponse.getHttpCode() + " " + mavenResponse.getHttpMessage(), this.charset);
        buf.writeBytes(CRLF);

        for (Entry<String, String> entry : mavenResponse.getHeaders().entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();

            buf.writeCharSequence(key, this.charset);
            buf.writeCharSequence(": ", this.charset);
            buf.writeCharSequence(value, this.charset);
            buf.writeBytes(CRLF);
        }

        buf.writeBytes(CRLF);

        if (mavenResponse.hasResource())
        {
            buf.ensureWritable(mavenResponse.getResourceLength(), true);

            buf.writeBytes(mavenResponse.getResource());
        }

        // out.add(buf);
        ctx.write(buf);
    }

    /**
     * @return {@link Logger}
     */
    private Logger getLogger()
    {
        return this.logger;
    }
}
