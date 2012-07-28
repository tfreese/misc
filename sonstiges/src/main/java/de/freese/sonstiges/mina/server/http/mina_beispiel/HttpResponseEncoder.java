// Created: 28.12.2009
/**
 * 28.12.2009
 */
package de.freese.sonstiges.mina.server.http.mina_beispiel;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import de.freese.sonstiges.mina.server.http.HttpResponseMessage;

/**
 * A {@link MessageEncoder} that encodes {@link HttpResponseMessage}.
 * 
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 590006 $, $Date: 2007-10-30 18:44:02 +0900 (?, 30 10? 2007) $
 */
public class HttpResponseEncoder implements MessageEncoder<HttpResponseMessage>
{
	/**
 * 
 */
	private static final byte[] CRLF = new byte[]
	{
			0x0D, 0x0A
	};

	/**
	 * Erstellt ein neues {@link HttpResponseEncoder} Object.
	 */
	public HttpResponseEncoder()
	{
		super();
	}

	/**
	 * @see org.apache.mina.filter.codec.demux.MessageEncoder#encode(org.apache.mina.core.session.IoSession,
	 *      java.lang.Object, org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	@Override
	public void encode(final IoSession session, final HttpResponseMessage message,
						final ProtocolEncoderOutput out) throws Exception
	{
		IoBuffer buf = IoBuffer.allocate(256);
		// Enable auto-expand for easier encoding
		buf.setAutoExpand(true);

		try
		{
			// output all headers except the content length
			CharsetEncoder encoder = Charset.defaultCharset().newEncoder();
			buf.putString("HTTP/1.1 ", encoder);
			buf.putString(String.valueOf(message.getResponseCode()), encoder);
			switch (message.getResponseCode())
			{
				case HttpResponseMessage.HTTP_STATUS_SUCCESS:
					buf.putString(" OK", encoder);
					break;
				case HttpResponseMessage.HTTP_STATUS_NOT_FOUND:
					buf.putString(" Not Found", encoder);
					break;
			}
			buf.put(CRLF);
			for (Entry<String, String> entry : message.getHeaders().entrySet())
			{
				buf.putString(entry.getKey(), encoder);
				buf.putString(": ", encoder);
				buf.putString(entry.getValue(), encoder);
				buf.put(CRLF);
			}
			// now the content length is the body length
			buf.putString("Content-Length: ", encoder);
			buf.putString(String.valueOf(message.getBodyLength()), encoder);
			buf.put(CRLF);
			buf.put(CRLF);
			// add body
			buf.put(message.getBody());
			// System.out.println("\n+++++++");
			// for (int i=0; i<buf.position();i++)System.out.print(new String(new
			// byte[]{buf.get(i)}));
			// System.out.println("\n+++++++");
		}
		catch (CharacterCodingException ex)
		{
			ex.printStackTrace();
		}

		buf.flip();
		out.write(buf);
	}
}
