package com.javacodegeeks.nio.large_file_transfer.remote;

import org.apache.commons.lang3.StringUtils;
import com.javacodegeeks.nio.large_file_transfer.Constants;

/**
 * @author Thomas Freese
 */
final class FileMetaData
{

    /**
     * @param request String
     * @return {@link FileMetaData}
     */
    static FileMetaData from(final String request)
    {
        assert StringUtils.isNotEmpty(request);

        final String[] contents = request.replace(Constants.END_MESSAGE_MARKER, StringUtils.EMPTY).split(Constants.MESSAGE_DELIMITTER);

        return new FileMetaData(contents[0], Long.valueOf(contents[1]));
    }

    /**
     *
     */
    private final String fileName;

    /**
     *
     */
    private final long size;

    /**
     * Erstellt ein neues {@link FileMetaData} Object.
     *
     * @param fileName String
     * @param size long
     */
    private FileMetaData(final String fileName, final long size)
    {
        assert StringUtils.isNotEmpty(fileName);

        this.fileName = fileName;
        this.size = size;
    }

    /**
     * @return String
     */
    String getFileName()
    {
        return this.fileName;
    }

    /**
     * @return long
     */
    long getSize()
    {
        return this.size;
    }
}
