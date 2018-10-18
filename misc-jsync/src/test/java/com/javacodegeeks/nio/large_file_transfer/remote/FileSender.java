package com.javacodegeeks.nio.large_file_transfer.remote;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import com.javacodegeeks.nio.large_file_transfer.Constants;

/**
 * @author Thomas Freese
 */
public final class FileSender
{
    /**
     *
     */
    private SocketChannel client = null;

    /**
     *
     */
    private final InetSocketAddress hostAddress;

    /**
     * Erstellt ein neues {@link FileSender} Object.
     *
     * @param port int
     * @throws IOException Falls was schief geht.
     */
    public FileSender(final int port) throws IOException
    {
        super();

        this.hostAddress = new InetSocketAddress(port);
        this.client = SocketChannel.open(this.hostAddress);
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    public void close() throws IOException
    {
        this.client.close();
    }

    /**
     * @return {@link SocketChannel}
     */
    public SocketChannel getChannel()
    {
        return this.client;
    }

    /**
     * @param channel {@link FileChannel}
     * @param position long
     * @param size long
     * @throws IOException Falls was schief geht.
     */
    public void transfer(final FileChannel channel, long position, final long size) throws IOException
    {
        assert !Objects.isNull(channel);

        while (position < size)
        {
            position += channel.transferTo(position, Constants.TRANSFER_MAX_SIZE, this.client);
        }
    }
}
