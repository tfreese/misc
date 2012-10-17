// Created: 28.12.2009
/**
 * 28.12.2009
 */
package de.freese.sonstiges.server.mina.http.codec;

import java.nio.charset.CharsetEncoder;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import de.freese.sonstiges.server.mina.http.HttpResponseMessage;

/**
 * @author Thomas Freese
 */
public class HttpProtocolEncoder implements ProtocolEncoder
{
	// /**
	// *
	// */
	// private static final Logger LOGGER = LoggerFactory.getLogger(HttpProtocolEncoder.class);

	/**
	 * 
	 */
	private final CharsetEncoder encoder;

	/**
	 * Erstellt ein neues {@link HttpProtocolEncoder} Object.
	 * 
	 * @param encoder {@link CharsetEncoder}
	 */
	public HttpProtocolEncoder(final CharsetEncoder encoder)
	{
		super();

		this.encoder = encoder;
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#dispose(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void dispose(final IoSession iosession) throws Exception
	{
		// LOGGER.info(iosession.toString());
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#encode(org.apache.mina.core.session.IoSession,
	 *      java.lang.Object, org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	@Override
	public void encode(final IoSession iosession, final Object obj,
						final ProtocolEncoderOutput protocolencoderoutput) throws Exception
	{
		HttpResponseMessage message = (HttpResponseMessage) obj;
		IoBuffer buf = IoBuffer.allocate(256);
		buf.setAutoExpand(true);

		// output all headers except the content length
		buf.putString("HTTP/1.1 ", this.encoder);
		buf.putString(String.valueOf(message.getResponseCode()), this.encoder);

		switch (message.getResponseCode())
		{
			case HttpResponseMessage.HTTP_STATUS_SUCCESS:
				buf.putString(" OK", this.encoder);
				break;
			case HttpResponseMessage.HTTP_STATUS_NOT_FOUND:
				buf.putString(" Not Found", this.encoder);
				break;
			default:
				break;
		}

		buf.put(HttpResponseMessage.CRLF_BYTES);

		for (Entry<String, String> entry : message.getHeaders().entrySet())
		{
			buf.putString(entry.getKey(), this.encoder);
			buf.putString(": ", this.encoder);
			buf.putString(entry.getValue(), this.encoder);
			buf.put(HttpResponseMessage.CRLF_BYTES);
		}

		// now the content length is the body length
		buf.putString("Content-Length: ", this.encoder);
		buf.putString(String.valueOf(message.getBodyLength()), this.encoder);
		buf.put(HttpResponseMessage.CRLF_BYTES);
		buf.put(HttpResponseMessage.CRLF_BYTES);
		buf.put(message.getBody());

		buf.flip();
		protocolencoderoutput.write(buf);
	}
}
