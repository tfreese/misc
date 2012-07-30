/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.repository;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * Basisimplementierung eines RemoteRepositories.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractRemoteRepository extends AbstractRepository implements
		IRemoteRepository
{

	// /**
	// *
	// */
	// private Charset charset = null;

	/**
	 * 
	 */
	private CharsetDecoder charsetDecoder = null;

	/**
	 * 
	 */
	private CharsetEncoder charsetEncoder = null;

	/**
	 * 
	 */
	private URI uri = null;

	/**
	 * Erstellt ein neues {@link AbstractRemoteRepository} Object.
	 */
	public AbstractRemoteRepository()
	{
		super();
	}

	/**
	 * @return {@link CharsetDecoder}
	 */
	protected CharsetDecoder getCharsetDecoder()
	{
		return this.charsetDecoder;
	}

	/**
	 * @return {@link CharsetEncoder}
	 */
	protected CharsetEncoder getCharsetEncoder()
	{
		return this.charsetEncoder;
	}

	/**
	 * @see de.freese.maven.proxy.repository.IRepository#getName()
	 */
	@Override
	public String getName()
	{
		return getUri().toString();
	}

	/**
	 * @return {@link URI}
	 */
	protected URI getUri()
	{
		return this.uri;
	}

	/**
	 * @see de.freese.maven.proxy.repository.IRemoteRepository#setCharset(java.nio.charset.Charset)
	 */
	@Override
	public void setCharset(final Charset charset)
	{
		this.charsetEncoder = charset.newEncoder();
		this.charsetDecoder = charset.newDecoder();
	}

	/**
	 * @see de.freese.maven.proxy.repository.IRemoteRepository#setUri(java.net.URI)
	 */
	@Override
	public void setUri(final URI value)
	{
		this.uri = value;
	}
}
