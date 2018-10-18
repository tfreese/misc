package com.javacodegeeks.nio.large_file_transfer.remote;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import com.javacodegeeks.nio.large_file_transfer.Constants;

/**
 * @author Thomas Freese
 */
public final class FileWriter
{
    /**
     *
     */
    private final FileChannel channel;

    /**
     * Erstellt ein neues {@link FileWriter} Object.
     *
     * @param path String
     * @throws IOException Falls was schief geht.
     */
    public FileWriter(final String path) throws IOException
    {
        if (StringUtils.isEmpty(path))
        {
            throw new IllegalArgumentException("path required");
        }

        this.channel = FileChannel.open(Paths.get(path), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    public void close() throws IOException
    {
        this.channel.close();
    }

    /**
     * @param channel {@link SocketChannel}
     * @param bytes long
     * @throws IOException Falls was schief geht.
     */
    public void transfer(final SocketChannel channel, final long bytes) throws IOException
    {
        assert !Objects.isNull(channel);

        long position = 0l;

        while (position < bytes)
        {
            position += this.channel.transferFrom(channel, position, Constants.TRANSFER_MAX_SIZE);
        }
    }

    /**
     * @param buffer {@link ByteBuffer}
     * @param position long
     * @return int
     * @throws IOException Falls was schief geht.
     */
    public int write(final ByteBuffer buffer, final long position) throws IOException
    {
        assert !Objects.isNull(buffer);

        int bytesWritten = 0;

        while (buffer.hasRemaining())
        {
            bytesWritten += this.channel.write(buffer, position + bytesWritten);
        }

        return bytesWritten;
    }
}
