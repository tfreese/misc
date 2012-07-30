/**
 * Created: 29.12.2011
 */

package de.freese.maven.proxy.codec;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.maven.proxy.model.HTTPHeader;
import de.freese.maven.proxy.model.MavenRequest;

/**
 * {@link ProtocolDecoder} für Maven HTTP Requests.
 * 
 * @author Thomas Freese
 */
public class MavenProtocolDecoder implements ProtocolDecoder
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
	 * Erstellt ein neues {@link MavenProtocolDecoder} Object.
	 * 
	 * @param charsetDecoder {@link CharsetDecoder}
	 */
	public MavenProtocolDecoder(final CharsetDecoder charsetDecoder)
	{
		super();

		this.charsetDecoder = charsetDecoder;
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolDecoder#decode(org.apache.mina.core.session.IoSession,
	 *      org.apache.mina.core.buffer.IoBuffer,
	 *      org.apache.mina.filter.codec.ProtocolDecoderOutput)
	 */
	@Override
	public void decode(final IoSession session, final IoBuffer in, final ProtocolDecoderOutput out)
		throws Exception
	{
		if (getLogger().isDebugEnabled())
		{
			getLogger().debug(in.toString());
		}

		StringReader stringReader = new StringReader(in.getString(this.charsetDecoder));
		BufferedReader reader = new BufferedReader(stringReader);

		HTTPHeader httpHeader = HTTPHeader.parseHeader(reader);

		reader.close();

		MavenRequest mavenRequest = new MavenRequest();
		mavenRequest.setHttpHeader(httpHeader);

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
	public void finishDecode(final IoSession session, final ProtocolDecoderOutput out)
		throws Exception
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
