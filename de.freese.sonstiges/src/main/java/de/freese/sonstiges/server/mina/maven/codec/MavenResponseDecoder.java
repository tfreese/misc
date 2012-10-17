/**
 * Created: 27.12.2011
 */

package de.freese.sonstiges.server.mina.maven.codec;

import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.CharsetDecoder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class MavenResponseDecoder implements ProtocolDecoder
{
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 
	 */
	private final CharsetDecoder decoder;

	/**
	 * Erstellt ein neues {@link MavenResponseDecoder} Object.
	 * 
	 * @param decoder {@link CharsetDecoder}
	 */
	public MavenResponseDecoder(final CharsetDecoder decoder)
	{
		super();

		this.decoder = decoder;
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
		Reader reader = new StringReader(in.getString(this.decoder));

		LineIterator lineIterator = IOUtils.lineIterator(reader);

		while (lineIterator.hasNext())
		{
			String line = lineIterator.next();
			this.logger.info(line);
		}

		IOUtils.closeQuietly(reader);
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
}
