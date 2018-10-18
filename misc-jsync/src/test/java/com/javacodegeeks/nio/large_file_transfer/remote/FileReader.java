package com.javacodegeeks.nio.large_file_transfer.remote;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Thomas Freese
 */
public final class FileReader
{

    /**
     *
     */
    private final FileChannel channel;

    /**
     *
     */
    private final FileSender sender;

    /**
     * Erstellt ein neues {@link FileReader} Object.
     *
     * @param sender {@link FileSender}
     * @param path String
     * @throws IOException Falls was schief geht.
     */
    public FileReader(final FileSender sender, final String path) throws IOException
    {
        if (Objects.isNull(sender) || StringUtils.isEmpty(path))
        {
            throw new IllegalArgumentException("sender and path required");
        }

        this.sender = sender;
        this.channel = FileChannel.open(Paths.get(path), StandardOpenOption.READ);
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    public void close() throws IOException
    {
        this.sender.close();
        this.channel.close();
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    public void read() throws IOException
    {
        try
        {
            transfer();
        }
        finally
        {
            close();
        }
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    private void transfer() throws IOException
    {
        this.sender.transfer(this.channel, 0l, this.channel.size());
    }
}
