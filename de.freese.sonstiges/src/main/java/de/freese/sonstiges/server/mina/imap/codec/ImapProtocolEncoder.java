// Created: 02.01.2010
/**
 * 02.01.2010
 */
package de.freese.sonstiges.server.mina.imap.codec;

import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * @author Thomas Freese
 */
public class ImapProtocolEncoder implements ProtocolEncoder
{
	// /**
	// *
	// */
	// private static final Logger LOGGER = LoggerFactory.getLogger(ImapProtocolEncoder.class);

	/**
	 * 
	 */
	private final CharsetEncoder encoder;

	// /**
	// * The end of line character sequence used by most IETF protocols. That is a carriage return
	// * followed by a newline: "\r\n" (NETASCII_EOL)
	// */
	// private static final String CRLF_STRING = "\r\n";
	//
	// /**
	// *
	// */
	// private static final byte[] CRLF = new byte[]
	// {
	// 0x0D, 0x0A
	// };

	/**
	 * Erstellt ein neues {@link ImapProtocolEncoder} Object.
	 * 
	 * @param encoder {@link CharsetEncoder}
	 */
	public ImapProtocolEncoder(final CharsetEncoder encoder)
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
		// Empty
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolEncoder#encode(org.apache.mina.core.session.IoSession,
	 *      java.lang.Object, org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	@Override
	public void encode(final IoSession iosession, final Object obj,
						final ProtocolEncoderOutput protocolencoderoutput) throws Exception
	{
		String response = (String) obj;

		IoBuffer buf = IoBuffer.allocate(256);
		buf.setAutoExpand(true);

		buf.putString(response, this.encoder);

		buf.flip();
		protocolencoderoutput.write(buf);
	}
}
