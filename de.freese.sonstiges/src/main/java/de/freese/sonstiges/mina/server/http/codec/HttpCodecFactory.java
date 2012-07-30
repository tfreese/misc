// Created: 28.12.2009
/**
 * 28.12.2009
 */
package de.freese.sonstiges.mina.server.http.codec;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * @author Thomas Freese
 */
public class HttpCodecFactory implements ProtocolCodecFactory
{
	/**
	 * 
	 */
	private final Charset charset;

	/**
	 * 
	 */
	private final HttpProtocolDecoder protocolDecoder;

	/**
	 * 
	 */
	private final HttpProtocolEncoder protocolEncoder;

	/**
	 * Erstellt ein neues {@link HttpCodecFactory} Object.
	 * 
	 * @param charset {@link Charset}
	 */
	public HttpCodecFactory(final Charset charset)
	{
		super();

		this.charset = charset;
		this.protocolDecoder = new HttpProtocolDecoder(this.charset.newDecoder());
		this.protocolEncoder = new HttpProtocolEncoder(this.charset.newEncoder());
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
