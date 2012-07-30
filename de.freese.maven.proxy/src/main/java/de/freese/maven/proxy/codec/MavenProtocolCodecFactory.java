/**
 * Created: 29.12.2011
 */

package de.freese.maven.proxy.codec;

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
public class MavenProtocolCodecFactory implements ProtocolCodecFactory
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
	 * Erstellt ein neues {@link MavenProtocolCodecFactory} Object.
	 * 
	 * @param charset {@link Charset}
	 */
	public MavenProtocolCodecFactory(final Charset charset)
	{
		super();

		this.protocolDecoder = new MavenProtocolDecoder(charset.newDecoder());
		this.protocolEncoder = new MavenProtocolEncoder(charset.newEncoder());
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
