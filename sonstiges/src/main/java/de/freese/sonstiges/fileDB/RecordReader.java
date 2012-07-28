package de.freese.sonstiges.fileDB;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;

/**
 * @author Thomas Freese
 */
public class RecordReader
{
	/**
	 * 
	 */
	String key;

	/**
   * 
   */
	byte[] data;

	/**
   * 
   */
	ByteArrayInputStream in;

	/**
   * 
   */
	ObjectInputStream objIn;

	/**
	 * Erstellt ein neues {@link RecordReader} Object.
	 * 
	 * @param key String
	 * @param data byte[]
	 */
	public RecordReader(final String key, final byte[] data)
	{
		super();

		this.key = key;
		this.data = data;
		this.in = new ByteArrayInputStream(data);
	}

	/**
	 * @return String
	 */
	public String getKey()
	{
		return this.key;
	}

	/**
	 * @return byte[]
	 */
	public byte[] getData()
	{
		return this.data;
	}

	/**
	 * @return {@link InputStream}
	 * @throws IOException Falls was schief geht.
	 */
	public InputStream getInputStream() throws IOException
	{
		return this.in;
	}

	/**
	 * @return {@link ObjectInputStream}
	 * @throws IOException Falls was schief geht.
	 */
	public ObjectInputStream getObjectInputStream() throws IOException
	{
		if (this.objIn == null)
		{
			this.objIn = new ObjectInputStream(this.in);
		}

		return this.objIn;
	}

	/**
	 * Reads the next object in the record using an ObjectInputStream.
	 * 
	 * @return {@link Object}
	 * @throws IOException Falls was schief geht.
	 * @throws OptionalDataException Falls was schief geht.
	 * @throws ClassNotFoundException Falls was schief geht.
	 */
	public Object readObject() throws IOException, OptionalDataException, ClassNotFoundException
	{
		return getObjectInputStream().readObject();
	}
}
