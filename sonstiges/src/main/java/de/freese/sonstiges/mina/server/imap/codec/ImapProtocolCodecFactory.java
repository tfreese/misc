package de.freese.sonstiges.mina.server.imap.codec;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * @author Thomas Freese
 */
public class ImapProtocolCodecFactory implements ProtocolCodecFactory
{
	/**
	 * 
	 */
	private final Charset charset;

	/**
	 * 
	 */
	private final ImapProtocolDecoder protocolDecoder;

	/**
	 * 
	 */
	private final ImapProtocolEncoder protocolEncoder;

	/**
	 * Erstellt ein neues {@link ImapProtocolCodecFactory} Object.
	 * 
	 * @param charset {@link Charset}
	 */
	public ImapProtocolCodecFactory(final Charset charset)
	{
		super();

		this.charset = charset;
		this.protocolDecoder = new ImapProtocolDecoder(this.charset.newDecoder());
		this.protocolEncoder = new ImapProtocolEncoder(this.charset.newEncoder());
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getDecoder(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ProtocolDecoder getDecoder(final IoSession iosession) throws Exception
	{
		return this.protocolDecoder;
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getEncoder(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ProtocolEncoder getEncoder(final IoSession iosession) throws Exception
	{
		return this.protocolEncoder;
	}
}
