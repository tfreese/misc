package de.freese.sonstiges.fileDB;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Enumeration;

/**
 * @author Thomas Freese
 */
public abstract class BaseRecordsFile
{

	/**
	 * File pointer to the data start pointer header.
	 */
	protected static final long DATA_START_HEADER_LOCATION = 4;

	/**
	 * Total length in bytes of the global database headers.
	 */
	protected static final int FILE_HEADERS_REGION_LENGTH = 16;

	/**
	 * The length of a key in the index.
	 */
	protected static final int MAX_KEY_LENGTH = 64;

	/**
	 * File pointer to the num records header.
	 */
	protected static final long NUM_RECORDS_HEADER_LOCATION = 0;

	/**
	 * Number of bytes in the record header.
	 */
	protected static final int RECORD_HEADER_LENGTH = 16;

	/**
	 * The total length of one index entry - the key length plus the record header length.
	 */
	protected static final int INDEX_ENTRY_LENGTH = MAX_KEY_LENGTH + RECORD_HEADER_LENGTH;

	/**
	 * Current file pointer to the start of the record data.
	 */
	protected long dataStartPtr;

	/**
	 * The database file.
	 */
	private RandomAccessFile file;

	/**
	 * Creates a new database file, initializing the appropriate headers. Enough space is allocated
	 * in the index for the specified initial size.
	 * 
	 * @param dbPath String
	 * @param initialSize int
	 * @throws IOException Falls was schief geht.
	 * @throws RecordsFileException Falls was schief geht.
	 */
	protected BaseRecordsFile(final String dbPath, final int initialSize)
		throws IOException, RecordsFileException
	{
		super();

		File f = new File(dbPath);

		if (f.exists())
		{
			throw new RecordsFileException("Database already exits: " + dbPath);
		}

		this.file = new RandomAccessFile(f, "rw");
		this.dataStartPtr = indexPositionToKeyFp(initialSize); // Record Data Region starts were the
		setFileLength(this.dataStartPtr); // (i+1)th index entry would start.
		writeNumRecordsHeader(0);
		writeDataStartPtrHeader(this.dataStartPtr);
	}

	/**
	 * Opens an existing database file and initializes the dataStartPtr. The accessFlags parameter
	 * can be "r" or "rw" -- as defined in RandomAccessFile.
	 * 
	 * @param dbPath String
	 * @param accessFlags String
	 * @throws IOException Falls was schief geht.
	 * @throws RecordsFileException Falls was schief geht.
	 */
	protected BaseRecordsFile(final String dbPath, final String accessFlags)
		throws IOException, RecordsFileException
	{
		super();

		File f = new File(dbPath);

		if (!f.exists())
		{
			throw new RecordsFileException("Database not found: " + dbPath);
		}

		this.file = new RandomAccessFile(f, accessFlags);
		this.dataStartPtr = readDataStartHeader();
	}

	/**
	 * Appends an entry to end of index. Assumes that insureIndexSpace() has already been called.
	 * 
	 * @param key String
	 * @param newRecord {@link RecordHeader}
	 * @param currentNumRecords int
	 * @throws IOException Falls was schief geht.
	 * @throws RecordsFileException Falls was schief geht.
	 */
	protected void addEntryToIndex(final String key, final RecordHeader newRecord,
									final int currentNumRecords)
		throws IOException, RecordsFileException
	{
		DbByteArrayOutputStream temp = new DbByteArrayOutputStream(MAX_KEY_LENGTH);
		(new DataOutputStream(temp)).writeUTF(key);

		if (temp.size() > MAX_KEY_LENGTH)
		{
			throw new RecordsFileException("Key is larger than permitted size of " + MAX_KEY_LENGTH
					+ " bytes");
		}

		this.file.seek(indexPositionToKeyFp(currentNumRecords));
		temp.writeTo(this.file);
		this.file.seek(indexPositionToRecordHeaderFp(currentNumRecords));
		newRecord.write(this.file);
		newRecord.setIndexPosition(currentNumRecords);
		writeNumRecordsHeader(currentNumRecords + 1);
	}

	/**
	 * Locates space for a new record of dataLength size and initializes a RecordHeader.
	 * 
	 * @param key String
	 * @param dataLength int
	 * @return {@link RecordHeader}
	 * @throws RecordsFileException Falls was schief geht.
	 * @throws IOException Falls was schief geht.
	 */
	protected abstract RecordHeader allocateRecord(String key, int dataLength)
		throws RecordsFileException, IOException;

	/**
	 * Closes the file.
	 * 
	 * @throws IOException Falls was schief geht.
	 * @throws RecordsFileException Falls was schief geht.
	 */
	public synchronized void close() throws IOException, RecordsFileException
	{
		try
		{
			this.file.close();
		}
		finally
		{
			this.file = null;
		}
	}

	/**
	 * Removes the record from the index. Replaces the target with the entry at the end of the
	 * index.
	 * 
	 * @param key String
	 * @param header {@link RecordHeader}
	 * @param currentNumRecords int
	 * @throws IOException Falls was schief geht.
	 * @throws RecordsFileException Falls was schief geht.
	 */
	protected void deleteEntryFromIndex(final String key, final RecordHeader header,
										final int currentNumRecords)
		throws IOException, RecordsFileException
	{
		if (header.indexPosition != currentNumRecords - 1)
		{
			String lastKey = readKeyFromIndex(currentNumRecords - 1);
			RecordHeader last = keyToRecordHeader(lastKey);
			last.setIndexPosition(header.indexPosition);
			this.file.seek(indexPositionToKeyFp(last.indexPosition));
			this.file.writeUTF(lastKey);
			this.file.seek(indexPositionToRecordHeaderFp(last.indexPosition));
			last.write(this.file);
		}

		writeNumRecordsHeader(currentNumRecords - 1);
	}

	/**
	 * Deletes a record.
	 * 
	 * @param key String
	 * @throws RecordsFileException Falls was schief geht.
	 * @throws IOException Falls was schief geht.
	 */
	public synchronized void deleteRecord(final String key)
		throws RecordsFileException, IOException
	{
		RecordHeader delRec = keyToRecordHeader(key);
		int currentNumRecords = getNumRecords();

		if (getFileLength() == delRec.dataPointer + delRec.dataCapacity)
		{
			// shrink file since this is the last record in the file
			setFileLength(delRec.dataPointer);
		}
		else
		{
			RecordHeader previous = getRecordAt(delRec.dataPointer - 1);

			if (previous != null)
			{
				// append space of deleted record onto previous record
				previous.dataCapacity += delRec.dataCapacity;
				writeRecordHeaderToIndex(previous);
			}
			else
			{
				// target record is first in the file and is deleted by adding its space to
				// the second record.
				RecordHeader secondRecord = getRecordAt(delRec.dataPointer + delRec.dataCapacity);
				byte[] data = readRecordData(secondRecord);
				secondRecord.dataPointer = delRec.dataPointer;
				secondRecord.dataCapacity += delRec.dataCapacity;
				writeRecordData(secondRecord, data);
				writeRecordHeaderToIndex(secondRecord);
			}
		}

		deleteEntryFromIndex(key, delRec, currentNumRecords);
	}

	/**
	 * Returns an Enumeration of the keys of all records in the database.
	 * 
	 * @return {@link Enumeration}
	 */
	public abstract Enumeration<String> enumerateKeys();

	/**
	 * @return int
	 * @throws IOException Falls was schief geht.
	 */
	protected long getFileLength() throws IOException
	{
		return this.file.length();
	}

	/**
	 * Returns the number or records in the database.
	 * 
	 * @return int
	 */
	public abstract int getNumRecords();

	/**
	 * Returns the record to which the target file pointer belongs - meaning the specified location
	 * in the file is part of the record data of the RecordHeader which is returned. Returns null if
	 * the location is not part of a record. (O(n) mem accesses)
	 * 
	 * @param targetFp long
	 * @return {@link RecordHeader}
	 * @throws RecordsFileException Falls was schief geht.
	 */
	protected abstract RecordHeader getRecordAt(long targetFp) throws RecordsFileException;

	/**
	 * Returns a file pointer in the index pointing to the first byte in the key located at the
	 * given index position.
	 * 
	 * @param pos int
	 * @return long
	 */
	protected long indexPositionToKeyFp(final int pos)
	{
		return FILE_HEADERS_REGION_LENGTH + (INDEX_ENTRY_LENGTH * pos);
	}

	/**
	 * Returns a file pointer in the index pointing to the first byte in the record pointer located
	 * at the given index position.
	 * 
	 * @param pos int
	 * @return long
	 */
	long indexPositionToRecordHeaderFp(final int pos)
	{
		return indexPositionToKeyFp(pos) + MAX_KEY_LENGTH;
	}

	/**
	 * Adds the given record to the database.
	 * 
	 * @param rw {@link RecordWriter}
	 * @throws RecordsFileException Falls was schief geht.
	 * @throws IOException Falls was schief geht.
	 */
	public synchronized void insertRecord(final RecordWriter rw)
		throws RecordsFileException, IOException
	{
		String key = rw.getKey();

		if (recordExists(key))
		{
			throw new RecordsFileException("Key exists: " + key);
		}

		insureIndexSpace(getNumRecords() + 1);
		RecordHeader newRecord = allocateRecord(key, rw.getDataLength());
		writeRecordData(newRecord, rw);
		addEntryToIndex(key, newRecord, getNumRecords());
	}

	/**
	 * Checks to see if there is space for and additional index entry. If not, space is created by
	 * moving records to the end of the file.
	 * 
	 * @param requiredNumRecords int
	 * @throws RecordsFileException Falls was schief geht.
	 * @throws IOException ex
	 */
	protected void insureIndexSpace(final int requiredNumRecords)
		throws RecordsFileException, IOException
	{
		int currentNumRecords = getNumRecords();
		long endIndexPtr = indexPositionToKeyFp(requiredNumRecords);

		if ((endIndexPtr > getFileLength()) && (currentNumRecords == 0))
		{
			setFileLength(endIndexPtr);
			this.dataStartPtr = endIndexPtr;
			writeDataStartPtrHeader(this.dataStartPtr);
			return;
		}

		while (endIndexPtr > this.dataStartPtr)
		{
			RecordHeader first = getRecordAt(this.dataStartPtr);
			byte[] data = readRecordData(first);
			first.dataPointer = getFileLength();
			first.dataCapacity = data.length;
			setFileLength(first.dataPointer + data.length);
			writeRecordData(first, data);
			writeRecordHeaderToIndex(first);
			this.dataStartPtr += first.dataCapacity;
			writeDataStartPtrHeader(this.dataStartPtr);
		}
	}

	/**
	 * Maps a key to a record header.
	 * 
	 * @param key String
	 * @return record
	 * @throws RecordsFileException Falls was schief geht.
	 */
	protected abstract RecordHeader keyToRecordHeader(String key) throws RecordsFileException;

	/**
	 * Reads the data start pointer header from the file.
	 * 
	 * @return long
	 * @throws IOException Falls was schief geht.
	 */
	protected long readDataStartHeader() throws IOException
	{
		this.file.seek(DATA_START_HEADER_LOCATION);

		return this.file.readLong();
	}

	/**
	 * Reads the ith key from the index.
	 * 
	 * @param position int
	 * @return String
	 * @throws IOException Falls was schief geht.
	 */
	String readKeyFromIndex(final int position) throws IOException
	{
		this.file.seek(indexPositionToKeyFp(position));

		return this.file.readUTF();
	}

	/**
	 * Reads the number of records header from the file.
	 * 
	 * @return int
	 * @throws IOException Falls was schief geht.
	 */
	protected int readNumRecordsHeader() throws IOException
	{
		this.file.seek(NUM_RECORDS_HEADER_LOCATION);

		return this.file.readInt();
	}

	/**
	 * Reads a record.
	 * 
	 * @param key String
	 * @return {@link RecordHeader}
	 * @throws RecordsFileException Falls was schief geht.
	 * @throws IOException Falls was schief geht.
	 */
	public synchronized RecordReader readRecord(final String key)
		throws RecordsFileException, IOException
	{
		byte[] data = readRecordData(key);

		return new RecordReader(key, data);
	}

	/**
	 * Reads the record data for the given record header.
	 * 
	 * @param header {@link RecordHeader}
	 * @return byte[]
	 * @throws IOException Falls was schief geht.
	 */
	protected byte[] readRecordData(final RecordHeader header) throws IOException
	{
		byte[] buf = new byte[header.dataCount];
		this.file.seek(header.dataPointer);
		this.file.readFully(buf);

		return buf;
	}

	/**
	 * Reads the data for the record with the given key.
	 * 
	 * @param key String
	 * @return byte[]
	 * @throws IOException Falls was schief geht.
	 * @throws RecordsFileException Falls was schief geht.
	 */
	protected byte[] readRecordData(final String key) throws IOException, RecordsFileException
	{
		return readRecordData(keyToRecordHeader(key));
	}

	/**
	 * Reads the ith record header from the index.
	 * 
	 * @param position int
	 * @return {@link RecordHeader}
	 * @throws IOException Falls was schief geht.
	 */
	RecordHeader readRecordHeaderFromIndex(final int position) throws IOException
	{
		this.file.seek(indexPositionToRecordHeaderFp(position));
		return RecordHeader.readHeader(this.file);
	}

	/**
	 * Checks there is a record with the given key.
	 * 
	 * @param key String
	 * @return boolean
	 */
	public abstract boolean recordExists(String key);

	/**
	 * @param l long
	 * @throws IOException Falls was schief geht.
	 */
	protected void setFileLength(final long l) throws IOException
	{
		this.file.setLength(l);
	}

	/**
	 * Updates an existing record. If the new contents do not fit in the original record, then the
	 * update is handled by deleting the old record and adding the new.
	 * 
	 * @param rw {@link RecordWriter}
	 * @throws RecordsFileException Falls was schief geht.
	 * @throws IOException Falls was schief geht.
	 */
	public synchronized void updateRecord(final RecordWriter rw)
		throws RecordsFileException, IOException
	{
		RecordHeader header = keyToRecordHeader(rw.getKey());

		if (rw.getDataLength() > header.dataCapacity)
		{
			deleteRecord(rw.getKey());
			insertRecord(rw);
		}
		else
		{
			writeRecordData(header, rw);
			writeRecordHeaderToIndex(header);
		}
	}

	/**
	 * Writes the data start pointer header to the file.
	 * 
	 * @param dataStartPtr long
	 * @throws IOException Falls was schief geht.
	 */
	protected void writeDataStartPtrHeader(final long dataStartPtr) throws IOException
	{
		this.file.seek(DATA_START_HEADER_LOCATION);
		this.file.writeLong(dataStartPtr);
	}

	/**
	 * Writes the number of records header to the file.
	 * 
	 * @param numRecords int
	 * @throws IOException Falls was schief geht.
	 */
	protected void writeNumRecordsHeader(final int numRecords) throws IOException
	{
		this.file.seek(NUM_RECORDS_HEADER_LOCATION);
		this.file.writeInt(numRecords);
	}

	/**
	 * Updates the contents of the given record. A RecordsFileException is thrown if the new data
	 * does not fit in the space allocated to the record. The header's data count is updated, but
	 * not written to the file.
	 * 
	 * @param header {@link RecordHeader}
	 * @param data byte[]
	 * @throws IOException Falls was schief geht.
	 * @throws RecordsFileException Falls was schief geht.
	 */
	protected void writeRecordData(final RecordHeader header, final byte[] data)
		throws IOException, RecordsFileException
	{
		if (data.length > header.dataCapacity)
		{
			throw new RecordsFileException("Record data does not fit");
		}

		header.dataCount = data.length;
		this.file.seek(header.dataPointer);
		this.file.write(data, 0, data.length);
	}

	/**
	 * Updates the contents of the given record. A RecordsFileException is thrown if the new data
	 * does not fit in the space allocated to the record. The header's data count is updated, but
	 * not written to the file.
	 * 
	 * @param header {@link RecordHeader}
	 * @param rw {@link RecordWriter}
	 * @throws IOException Falls was schief geht.
	 * @throws RecordsFileException Falls was schief geht.
	 */
	protected void writeRecordData(final RecordHeader header, final RecordWriter rw)
		throws IOException, RecordsFileException
	{
		if (rw.getDataLength() > header.dataCapacity)
		{
			throw new RecordsFileException("Record data does not fit");
		}

		header.dataCount = rw.getDataLength();
		this.file.seek(header.dataPointer);
		rw.writeTo(this.file);
	}

	/**
	 * Writes the ith record header to the index.
	 * 
	 * @param header {@link RecordHeader}
	 * @throws IOException Falls was schief geht.
	 */
	protected void writeRecordHeaderToIndex(final RecordHeader header) throws IOException
	{
		this.file.seek(indexPositionToRecordHeaderFp(header.indexPosition));
		header.write(this.file);
	}
}
