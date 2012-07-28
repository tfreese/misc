/**
 * Created: 27.12.2011
 */

package de.freese.sonstiges.mina.client;

import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class MavenRequestEncoder implements ProtocolEncoder
{
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 
	 */
	private final CharsetEncoder encoder;

	/**
	 * 
	 */
	public static final byte[] CRLF = new byte[]
	{
			0x0D, 0x0A
	};

	/**
	 * Erstellt ein neues {@link MavenRequestEncoder} Object.
	 * 
	 * @param encoder {@link CharsetEncoder}
	 */
	public MavenRequestEncoder(final CharsetEncoder encoder)
	{
		super();

		this.encoder = encoder;
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#dispose(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void dispose(final IoSession session) throws Exception
	{
		// Empty
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#encode(org.apache.mina.core.session.IoSession,
	 *      java.lang.Object, org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	@Override
	public void encode(final IoSession session, final Object message,
						final ProtocolEncoderOutput out) throws Exception
	{
		this.logger.info(message.toString());

		String context = (String) message;

		IoBuffer buf = IoBuffer.allocate(256);
		buf.setAutoExpand(true);

		// output all headers except the content length
		buf.putString("GET " + context + " HTTP/1.1", this.encoder).put(CRLF);
		// buf.putString("Accept-Encoding: gzip", this.encoder).put(CRLF);
		buf.putString("Pragma: no-cache", this.encoder).put(CRLF);
		// buf.putString("User-Agent: " + getClass().getName(), this.encoder).put(CRLF);
		buf.putString("Host: localhost:8088", this.encoder).put(CRLF);
		// buf.putString("Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2",
		// this.encoder)
		// .put(CRLF);
		buf.putString("Connection: keep-alive", this.encoder).put(CRLF);
		buf.put(CRLF);

		buf.flip();
		out.write(buf);
		out.flush();
	}
}
