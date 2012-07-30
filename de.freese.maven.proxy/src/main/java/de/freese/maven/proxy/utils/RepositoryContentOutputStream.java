/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.utils;

import java.io.ByteArrayOutputStream;

/**
 * Liefert den Buffer des Streams ohne diesen vorher zu kopieren.
 * 
 * @author Thomas Freese
 */
public class RepositoryContentOutputStream extends ByteArrayOutputStream
{
	/**
	 * Erstellt ein neues {@link RepositoryContentOutputStream} Object.
	 */
	public RepositoryContentOutputStream()
	{
		super();
	}

	/**
	 * Erstellt ein neues {@link RepositoryContentOutputStream} Object.
	 * 
	 * @param size int
	 */
	public RepositoryContentOutputStream(final int size)
	{
		super(size);
	}

	/**
	 * @see java.io.ByteArrayOutputStream#toByteArray()
	 */
	@Override
	public synchronized byte[] toByteArray()
	{
		return this.buf;
	}
}
