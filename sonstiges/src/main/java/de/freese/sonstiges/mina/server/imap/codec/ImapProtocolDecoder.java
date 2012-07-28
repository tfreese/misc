// Created: 02.01.2010
/**
 * 02.01.2010
 */
package de.freese.sonstiges.mina.server.imap.codec;

import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class ImapProtocolDecoder implements ProtocolDecoder
{
	/**
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ImapProtocolDecoder.class);

	/**
	 * 
	 */
	private final CharsetDecoder decoder;

	/**
	 * Erstellt ein neues {@link ImapProtocolDecoder} Object.
	 * 
	 * @param decoder {@link CharsetDecoder}
	 */
	public ImapProtocolDecoder(final CharsetDecoder decoder)
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
	public void decode(final IoSession iosession, final IoBuffer iobuffer,
						final ProtocolDecoderOutput protocoldecoderoutput) throws Exception
	{
		String request = iobuffer.getString(this.decoder);

		protocoldecoderoutput.write(request);
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolDecoder#dispose(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void dispose(final IoSession iosession) throws Exception
	{
		// Empty
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolDecoder#finishDecode(org.apache.mina.core.session.IoSession,
	 *      org.apache.mina.filter.codec.ProtocolDecoderOutput)
	 */
	@Override
	public void finishDecode(final IoSession iosession,
								final ProtocolDecoderOutput protocoldecoderoutput) throws Exception
	{
		// Empty
	}
}
