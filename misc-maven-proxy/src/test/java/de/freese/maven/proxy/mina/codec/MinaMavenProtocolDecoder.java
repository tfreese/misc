/**
 * Created: 29.12.2011
 */

package de.freese.maven.proxy.mina.codec;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.CharsetDecoder;
import java.util.Objects;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.maven.proxy.model.MavenRequest;

/**
 * {@link ProtocolDecoder} für Maven HTTP Requests.
 *
 * @author Thomas Freese
 */
public class MinaMavenProtocolDecoder implements ProtocolDecoder
{
    /**
     *
     */
    private final CharsetDecoder charsetDecoder;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link MinaMavenProtocolDecoder} Object.
     *
     * @param charsetDecoder {@link CharsetDecoder}
     */
    public MinaMavenProtocolDecoder(final CharsetDecoder charsetDecoder)
    {
        super();

        this.charsetDecoder = Objects.requireNonNull(charsetDecoder, "charsetDecoder required");
    }

    /**
     * @see org.apache.mina.filter.codec.ProtocolDecoder#decode(org.apache.mina.core.session.IoSession,
     *      org.apache.mina.core.buffer.IoBuffer, org.apache.mina.filter.codec.ProtocolDecoderOutput)
     */
    @Override
    public void decode(final IoSession session, final IoBuffer in, final ProtocolDecoderOutput out) throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug(in.toString());
        }

        MavenRequest mavenRequest = null;

        try (BufferedReader reader = new BufferedReader(new StringReader(in.getString(this.charsetDecoder))))
        {
            mavenRequest = MavenRequest.create(reader);
        }

        out.write(mavenRequest);
    }

    /**
     * @see org.apache.mina.filter.codec.ProtocolDecoder#dispose(org.apache.mina.core.session.IoSession)
     */
    @Override
    public void dispose(final IoSession session) throws Exception
    {
        // Empty
    }

    /**
     * @see org.apache.mina.filter.codec.ProtocolDecoder#finishDecode(org.apache.mina.core.session.IoSession,
     *      org.apache.mina.filter.codec.ProtocolDecoderOutput)
     */
    @Override
    public void finishDecode(final IoSession session, final ProtocolDecoderOutput out) throws Exception
    {
        // Empty
    }

    /**
     * @return {@link Logger}
     */
    private Logger getLogger()
    {
        return this.logger;
    }
}
