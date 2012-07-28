package de.freese.sonstiges.fileDB;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Extends ByteArrayOutputStream to provide a way of writing the buffer to a DataOutput without
 * re-allocating it.
 */
public class DbByteArrayOutputStream extends ByteArrayOutputStream
{
	/**
	 * Erstellt ein neues {@link DbByteArrayOutputStream} Object.
	 */
	public DbByteArrayOutputStream()
	{
		super();
	}

	/**
	 * Erstellt ein neues {@link DbByteArrayOutputStream} Object.
	 * 
	 * @param size int
	 */
	public DbByteArrayOutputStream(final int size)
	{
		super(size);
	}

	/**
	 * Writes the full contents of the buffer a DataOutput stream.
	 * 
	 * @param dstr {@link DataOutput}
	 * @throws IOException Falls was schief geht.
	 */
	public synchronized void writeTo(final DataOutput dstr) throws IOException
	{
		byte[] data = super.buf;
		int l = super.size();
		dstr.write(data, 0, l);
	}
}
