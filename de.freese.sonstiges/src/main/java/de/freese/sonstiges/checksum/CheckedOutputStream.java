package de.freese.sonstiges.checksum;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Checksum;

/**
 * @author Thomas Freese
 */
public class CheckedOutputStream extends FilterOutputStream
{
	/**
     * 
     */
	private Checksum cksum = null;

	/**
	 * Creates a new {@link CheckedOutputStream} object.
	 * 
	 * @param out {@link CheckedOutputStream}
	 * @param cksum {@link Checksum}
	 */
	public CheckedOutputStream(final OutputStream out, final Checksum cksum)
	{
		super(out);

		this.cksum = cksum;
	}

	/**
	 * @return {@link Checksum}
	 */
	public Checksum getChecksum()
	{
		return this.cksum;
	}

	/**
	 * @see java.io.FilterOutputStream#write(byte[])
	 */
	@Override
	public void write(final byte[] b) throws IOException
	{
		this.out.write(b, 0, b.length);
		this.cksum.update(b, 0, b.length);
	}

	/**
	 * @see java.io.FilterOutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException
	{
		this.out.write(b, off, len);
		this.cksum.update(b, off, len);
	}

	/**
	 * @see java.io.FilterOutputStream#write(int)
	 */
	@Override
	public void write(final int b) throws IOException
	{
		this.out.write(b);
		this.cksum.update(b);
	}
}
