/**
 * Created: 29.12.2011
 */

package de.freese.maven.proxy.codec;

import java.nio.charset.CharsetEncoder;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.maven.proxy.model.HTTPHeader;
import de.freese.maven.proxy.model.MavenResponse;

/**
 * {@link ProtocolEncoder} für Maven HTTP Response.
 * 
 * @author Thomas Freese
 */
public class MavenProtocolEncoder implements ProtocolEncoder
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
	private final CharsetEncoder charsetEncoder;

	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Erstellt ein neues {@link MavenProtocolEncoder} Object.
	 * 
	 * @param charsetEncoder {@link CharsetEncoder}
	 */
	public MavenProtocolEncoder(final CharsetEncoder charsetEncoder)
	{
		super();

		this.charsetEncoder = charsetEncoder;
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
		MavenResponse mavenResponse = (MavenResponse) message;
		HTTPHeader httpHeader = mavenResponse.getHttpHeader();

		int contentLengthResource = mavenResponse.getResourceLength();
		int contentLengthHeader = mavenResponse.getHttpHeader().getContentLength();

		if (contentLengthResource != contentLengthHeader)
		{
			httpHeader.setContentLengthValue(Integer.toString(Math.max(contentLengthResource,
					contentLengthHeader)));

			if (getLogger().isDebugEnabled())
			{
				getLogger().debug("ContentLength: Resource={}, Header={}",
						Integer.valueOf(contentLengthResource),
						Integer.valueOf(contentLengthHeader));
			}
		}

		IoBuffer response = IoBuffer.allocate(1024);
		response.setAutoExpand(true);

		response.putString(httpHeader.getFirstLine(), this.charsetEncoder);
		response.put(CRLF);

		for (Entry<String, String> entry : httpHeader.getHeaders().entrySet())
		{
			String key = entry.getKey();
			String value = entry.getValue();

			response.putString(key, this.charsetEncoder);
			response.putString(": ", this.charsetEncoder);
			response.putString(value, this.charsetEncoder);
			response.put(CRLF);
		}

		response.put(CRLF);

		if (mavenResponse.hasResource())
		{
			response.put(IoBuffer.wrap(mavenResponse.getResource()));
		}

		response.flip();
		out.write(response);

		// CharBuffer charBuffer = CharBuffer.allocate(response.capacity());
		// this.charsetDecoder.decode(response.buf(), charBuffer, false);
		// charBuffer.flip();
		// getLogger().debug(charBuffer.toString());
		// charBuffer.clear();
	}

	/**
	 * @return {@link Logger}
	 */
	private Logger getLogger()
	{
		return this.logger;
	}
}
