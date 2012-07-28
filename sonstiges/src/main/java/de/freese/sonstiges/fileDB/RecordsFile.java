package de.freese.sonstiges.fileDB;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author Thomas Freese
 */
public class RecordsFile extends BaseRecordsFile
{

	/**
	 * Hashtable which holds the in-memory index. For efficiency, the entire index is cached in
	 * memory. The hashtable maps a key of type String to a RecordHeader.
	 */
	protected Hashtable<String, RecordHeader> memIndex;

	/**
	 * Creates a new database file. The initialSize parameter determines the amount of space which
	 * is allocated for the index. The index can grow dynamically, but the parameter is provide to
	 * increase efficiency.
	 * 
	 * @param dbPath String
	 * @param initialSize int
	 * @throws IOException Falls was schief geht.
	 * @throws RecordsFileException Falls was schief geht.
	 */
	public RecordsFile(final String dbPath, final int initialSize)
		throws IOException, RecordsFileException
	{
		super(dbPath, initialSize);

		this.memIndex = new Hashtable<>(initialSize);
	}

	/**
	 * Opens an existing database and initializes the in-memory index.
	 * 
	 * @param dbPath String
	 * @param accessFlags String
	 * @throws IOException Falls was schief geht.
	 * @throws RecordsFileException Falls was schief geht.
	 */
	public RecordsFile(final String dbPath, final String accessFlags)
		throws IOException, RecordsFileException
	{

		super(dbPath, accessFlags);

		int numRecords = readNumRecordsHeader();
		this.memIndex = new Hashtable<>(numRecords);

		for (int i = 0; i < numRecords; i++)
		{
			String key = readKeyFromIndex(i);
			RecordHeader header = readRecordHeaderFromIndex(i);
			header.setIndexPosition(i);
			this.memIndex.put(key, header);
		}
	}

	/**
	 * @see de.freese.sonstiges.fileDB.BaseRecordsFile#addEntryToIndex(java.lang.String,
	 *      de.freese.sonstiges.fileDB.RecordHeader, int)
	 */
	@Override
	protected void addEntryToIndex(final String key, final RecordHeader newRecord,
									final int currentNumRecords)
		throws IOException, RecordsFileException
	{
		super.addEntryToIndex(key, newRecord, currentNumRecords);
		this.memIndex.put(key, newRecord);
	}

	/**
	 * @see de.freese.sonstiges.fileDB.BaseRecordsFile#allocateRecord(java.lang.String, int)
	 */
	@Override
	protected RecordHeader allocateRecord(final String key, final int dataLength)
		throws RecordsFileException, IOException
	{
		// search for empty space
		RecordHeader newRecord = null;
		Enumeration<RecordHeader> e = this.memIndex.elements();

		while (e.hasMoreElements())
		{
			RecordHeader next = e.nextElement();
			// int free = next.getFreeSpace();

			if (dataLength <= next.getFreeSpace())
			{
				newRecord = next.split();
				writeRecordHeaderToIndex(next);
				break;
			}
		}

		if (newRecord == null)
		{
			// append record to end of file - grows file to allocate space
			long fp = getFileLength();
			setFileLength(fp + dataLength);
			newRecord = new RecordHeader(fp, dataLength);
		}

		return newRecord;
	}

	/**
	 * @see de.freese.sonstiges.fileDB.BaseRecordsFile#close()
	 */
	@Override
	public synchronized void close() throws IOException, RecordsFileException
	{
		try
		{
			super.close();
		}
		finally
		{
			this.memIndex.clear();
			this.memIndex = null;
		}
	}

	/**
	 * @see de.freese.sonstiges.fileDB.BaseRecordsFile#deleteEntryFromIndex(java.lang.String,
	 *      de.freese.sonstiges.fileDB.RecordHeader, int)
	 */
	@Override
	protected void deleteEntryFromIndex(final String key, final RecordHeader header,
										final int currentNumRecords)
		throws IOException, RecordsFileException
	{
		super.deleteEntryFromIndex(key, header, currentNumRecords);
		this.memIndex.remove(key);
		// RecordHeader deleted = this.memIndex.remove(key);
	}

	/**
	 * @see de.freese.sonstiges.fileDB.BaseRecordsFile#enumerateKeys()
	 */
	@Override
	public synchronized Enumeration<String> enumerateKeys()
	{
		return this.memIndex.keys();
	}

	/**
	 * @see de.freese.sonstiges.fileDB.BaseRecordsFile#getNumRecords()
	 */
	@Override
	public synchronized int getNumRecords()
	{
		return this.memIndex.size();
	}

	/**
	 * @see de.freese.sonstiges.fileDB.BaseRecordsFile#getRecordAt(long)
	 */
	@Override
	protected RecordHeader getRecordAt(final long targetFp) throws RecordsFileException
	{
		Enumeration<RecordHeader> e = this.memIndex.elements();

		while (e.hasMoreElements())
		{
			RecordHeader next = e.nextElement();

			if ((targetFp >= next.dataPointer)
					&& (targetFp < (next.dataPointer + next.dataCapacity)))
			{
				return next;
			}
		}

		return null;
	}

	/**
	 * @see de.freese.sonstiges.fileDB.BaseRecordsFile#keyToRecordHeader(java.lang.String)
	 */
	@Override
	protected RecordHeader keyToRecordHeader(final String key) throws RecordsFileException
	{
		RecordHeader h = this.memIndex.get(key);

		if (h == null)
		{
			throw new RecordsFileException("Key not found: " + key);
		}

		return h;
	}

	/**
	 * @see de.freese.sonstiges.fileDB.BaseRecordsFile#recordExists(java.lang.String)
	 */
	@Override
	public synchronized boolean recordExists(final String key)
	{
		return this.memIndex.containsKey(key);
	}
}
