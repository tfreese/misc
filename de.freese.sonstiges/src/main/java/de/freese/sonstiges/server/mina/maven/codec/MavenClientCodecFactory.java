/**
 * Created: 27.12.2011
 */

package de.freese.sonstiges.server.mina.maven.codec;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * @author Thomas Freese
 */
public class MavenClientCodecFactory implements ProtocolCodecFactory
{
	// /**
	// *
	// */
	// private final Charset charset;

	/**
	 * 
	 */
	private final ProtocolDecoder decoder;

	/**
	 * 
	 */
	private final ProtocolEncoder encoder;

	/**
	 * Erstellt ein neues {@link MavenClientCodecFactory} Object.
	 * 
	 * @param charset {@link Charset}
	 */
	public MavenClientCodecFactory(final Charset charset)
	{
		super();

		this.encoder = new MavenRequestEncoder(charset.newEncoder());
		this.decoder = new MavenResponseDecoder(charset.newDecoder());
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getDecoder(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ProtocolDecoder getDecoder(final IoSession session) throws Exception
	{
		return this.decoder;
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getEncoder(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ProtocolEncoder getEncoder(final IoSession session) throws Exception
	{
		return this.encoder;
	}
}
