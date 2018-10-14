/**
 * Created: 29.12.2011
 */

package de.freese.maven.proxy.mina.codec;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Factory für die Encoder/Decoder für das Maven HTTP Protokoll.
 * 
 * @author Thomas Freese
 */
public class MinaMavenProtocolCodecFactory implements ProtocolCodecFactory
{
	/**
	 * 
	 */
	private final ProtocolDecoder protocolDecoder;

	/**
	 * 
	 */
	private final ProtocolEncoder protocolEncoder;

	/**
	 * Erstellt ein neues {@link MinaMavenProtocolCodecFactory} Object.
	 * 
	 * @param charset {@link Charset}
	 */
	public MinaMavenProtocolCodecFactory(final Charset charset)
	{
		super();

		this.protocolDecoder = new MinaMavenProtocolDecoder(charset.newDecoder());
		this.protocolEncoder = new MinaMavenProtocolEncoder(charset.newEncoder());
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getDecoder(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ProtocolDecoder getDecoder(final IoSession session) throws Exception
	{
		return this.protocolDecoder;
	}

	/**
	 * @see org.apache.mina.filter.codec.ProtocolCodecFactory#getEncoder(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public ProtocolEncoder getEncoder(final IoSession session) throws Exception
	{
		return this.protocolEncoder;
	}
}
