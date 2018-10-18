package com.javacodegeeks.nio.large_file_transfer.remote;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Thomas Freese
 */
public final class FileWriterProxy
{
    /**
     *
     */
    private final String fileName;

    /**
     *
     */
    private final FileWriter fileWriter;

    /**
     *
     */
    private final AtomicLong position;

    /**
     *
     */
    private final long size;

    /**
     * Erstellt ein neues {@link FileWriterProxy} Object.
     *
     * @param path String
     * @param metaData {@link FileMetaData}
     * @throws IOException Falls was schief geht.
     */
    public FileWriterProxy(final String path, final FileMetaData metaData) throws IOException
    {
        assert !Objects.isNull(metaData) && StringUtils.isNotEmpty(path);

        this.fileWriter = new FileWriter(path + "/" + metaData.getFileName());
        this.position = new AtomicLong(0l);
        this.size = metaData.getSize();
        this.fileName = metaData.getFileName();
    }

    /**
     * @return boolean
     */
    public boolean done()
    {
        return this.position.get() == this.size;
    }

    /**
     * @return String
     */
    public String getFileName()
    {
        return this.fileName;
    }

    /**
     * @return {@link FileWriter}
     */
    public FileWriter getFileWriter()
    {
        return this.fileWriter;
    }

    /**
     * @return {@link AtomicLong}
     */
    public AtomicLong getPosition()
    {
        return this.position;
    }
}
