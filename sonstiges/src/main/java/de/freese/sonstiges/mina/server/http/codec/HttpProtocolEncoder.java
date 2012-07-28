// Created: 28.12.2009
/**
 * 28.12.2009
 */
package de.freese.sonstiges.mina.server.http.codec;

import java.nio.charset.CharsetEncoder;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.mina.server.http.HttpResponseMessage;

/**
 * @author Thomas Freese
 */
public class HttpProtocolEncoder implements ProtocolEncoder
{
	/**
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpProtocolEncoder.class);

	/**
	 * 
	 */
	private final CharsetEncoder encoder;

	/**
	 * The end of line character sequence used by most IETF protocols. That is a carriage return
	 * followed by a newline: "\r\n" (NETASCII_EOL)
	 */
	private static final String CRLF_STRING = "\r\n";

	/**
	 * "\r\n"
	 */
	private static final byte[] CRLF = new byte[]
	{
			0x0D, 0x0A
	};

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
		}

		buf.put(CRLF);

		for (Entry<String, String> entry : message.getHeaders().entrySet())
		{
			buf.putString(entry.getKey(), this.encoder);
			buf.putString(": ", this.encoder);
			buf.putString(entry.getValue(), this.encoder);
			buf.put(CRLF);
		}

		// now the content length is the body length
		buf.putString("Content-Length: ", this.encoder);
		buf.putString(String.valueOf(message.getBodyLength()), this.encoder);
		buf.put(CRLF);
		buf.put(CRLF);
		buf.put(message.getBody());

		// LOGGER.info(iosession.toString());
		//
		// String response = obj.toString();
		// int responseLength = response == null ? 0 : response.length();
		//
		// StringBuilder builder = new StringBuilder();
		// builder.append("HTTP/1.0 ").append("200 OK").append(CRLF);
		// builder.append("Server: MEINER !").append(CRLF);
		// builder.append("Content-type: ").append("text/html").append(CRLF);
		// builder.append("Content-length: ").append(responseLength).append(CRLF);
		// builder.append(response);
		//
		// IoBuffer buf = IoBuffer.allocate(responseLength * 2);
		// buf.setAutoExpand(true);
		// buf.putString(response, this.encoder);

		// for (;;)
		// {
		// try
		// {
		// cb.put(response);
		//
		// break;
		// }
		// catch (BufferOverflowException x)
		// {
		// // assert (cb.capacity() < (1 << 16));
		// cb = CharBuffer.allocate(cb.capacity() * 2);
		//
		// continue;
		// }
		// }

		buf.flip();
		protocolencoderoutput.write(buf);
	}
}
