package de.freese.sonstiges.fileDB;

import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author Thomas Freese
 */
public class RecordWriter
{

	/**
	 * 
	 */
	String key;

	/**
   * 
   */
	ObjectOutputStream objOut;

	/**
   * 
   */
	DbByteArrayOutputStream out;

	/**
	 * Erstellt ein neues {@link RecordWriter} Object.
	 * 
	 * @param key String
	 */
	public RecordWriter(final String key)
	{
		super();

		this.key = key;
		this.out = new DbByteArrayOutputStream();
	}

	/**
	 * Returns the number of bytes in the data.
	 * 
	 * @return int
	 */
	public int getDataLength()
	{
		return this.out.size();
	}

	/**
	 * @return String
	 */
	public String getKey()
	{
		return this.key;
	}

	/**
	 * @return {@link ObjectOutputStream}
	 * @throws IOException Falls was schief geht.
	 */
	public ObjectOutputStream getObjectOutputStream() throws IOException
	{
		if (this.objOut == null)
		{
			this.objOut = new ObjectOutputStream(this.out);
		}
		return this.objOut;
	}

	/**
	 * @return {@link OutputStream}
	 */
	public OutputStream getOutputStream()
	{
		return this.out;
	}

	/**
	 * @param o {@link Object}
	 * @throws IOException Falls was schief geht.
	 */
	public void writeObject(final Object o) throws IOException
	{
		getObjectOutputStream().writeObject(o);
		getObjectOutputStream().flush();
	}

	/**
	 * Writes the data out to the stream without re-allocating the buffer.
	 * 
	 * @param str {@link DataOutput}
	 * @throws IOException Falls was schief geht.
	 */
	public void writeTo(final DataOutput str) throws IOException
	{
		this.out.writeTo(str);
	}
}
