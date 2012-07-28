package de.freese.sonstiges.fileDB;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Thomas Freese
 */
public class RecordHeader
{

	/**
	 * File pointer to the first byte of record data (8 bytes).
	 */
	protected long dataPointer;

	/**
	 * Actual number of bytes of data held in this record (4 bytes).
	 */
	protected int dataCount;

	/**
	 * Number of bytes of data that this record can hold (4 bytes).
	 */
	protected int dataCapacity;

	/**
	 * Indicates this header's position in the file index.
	 */
	protected int indexPosition;

	/**
	 * Erstellt ein neues {@link RecordHeader} Object.
	 */
	protected RecordHeader()
	{
		super();
	}

	/**
	 * Erstellt ein neues {@link RecordHeader} Object.
	 * 
	 * @param dataPointer long
	 * @param dataCapacity int
	 */
	protected RecordHeader(final long dataPointer, final int dataCapacity)
	{
		super();

		if (dataCapacity < 1)
		{
			throw new IllegalArgumentException("Bad record size: " + dataCapacity);
		}

		this.dataPointer = dataPointer;
		this.dataCapacity = dataCapacity;
		this.dataCount = 0;
	}

	/**
	 * @return int
	 */
	protected int getIndexPosition()
	{
		return this.indexPosition;
	}

	/**
	 * @param indexPosition int
	 */
	protected void setIndexPosition(final int indexPosition)
	{
		this.indexPosition = indexPosition;
	}

	/**
	 * @return int
	 */
	protected int getDataCapacity()
	{
		return this.dataCapacity;
	}

	/**
	 * @return int
	 */
	protected int getFreeSpace()
	{
		return this.dataCapacity - this.dataCount;
	}

	/**
	 * @param in {@link DataInput}
	 * @throws IOException Falls was schief geht.
	 */
	protected void read(final DataInput in) throws IOException
	{
		this.dataPointer = in.readLong();
		this.dataCapacity = in.readInt();
		this.dataCount = in.readInt();
	}

	/**
	 * @param out {@link DataOutput}
	 * @throws IOException Falls was schief geht.
	 */
	protected void write(final DataOutput out) throws IOException
	{
		out.writeLong(this.dataPointer);
		out.writeInt(this.dataCapacity);
		out.writeInt(this.dataCount);
	}

	/**
	 * @param in {@link DataInput}
	 * @return {@link RecordHeader}
	 * @throws IOException Falls was schief geht.
	 */
	protected static RecordHeader readHeader(final DataInput in) throws IOException
	{
		RecordHeader r = new RecordHeader();
		r.read(in);

		return r;
	}

	/**
	 * Returns a new record header which occupies the free space of this record. Shrinks this record
	 * size by the size of its free space.
	 * 
	 * @return {@link RecordHeader}
	 * @throws RecordsFileException Falls was schief geht.
	 */
	protected RecordHeader split() throws RecordsFileException
	{
		long newFp = this.dataPointer + this.dataCount;
		RecordHeader newRecord = new RecordHeader(newFp, getFreeSpace());
		this.dataCapacity = this.dataCount;

		return newRecord;
	}

}
