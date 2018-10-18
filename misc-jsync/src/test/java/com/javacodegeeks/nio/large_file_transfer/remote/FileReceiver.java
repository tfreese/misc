package com.javacodegeeks.nio.large_file_transfer.remote;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class FileReceiver
{
    /**
     *
     */
    private final FileWriter fileWriter;

    /**
     *
     */
    private final int port;

    /**
     *
     */
    private final long size;

    /**
     * Erstellt ein neues {@link FileReceiver} Object.
     *
     * @param port int
     * @param fileWriter {@link FileWriter}
     * @param size long
     */
    public FileReceiver(final int port, final FileWriter fileWriter, final long size)
    {
        super();

        this.port = port;
        this.fileWriter = fileWriter;
        this.size = size;
    }

    /**
     * @param channel {@link SocketChannel}
     * @throws IOException Falls was schief geht.
     */
    private void doTransfer(final SocketChannel channel) throws IOException
    {
        assert !Objects.isNull(channel);

        this.fileWriter.transfer(channel, this.size);
    }

    /**
     * @param serverSocketChannel {@link ServerSocketChannel}
     * @throws IOException Falls was schief geht.
     */
    private void init(final ServerSocketChannel serverSocketChannel) throws IOException
    {
        assert !Objects.isNull(serverSocketChannel);

        serverSocketChannel.bind(new InetSocketAddress(this.port));
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    public void receive() throws IOException
    {
        SocketChannel channel = null;

        try (final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open())
        {
            init(serverSocketChannel);

            channel = serverSocketChannel.accept();

            doTransfer(channel);
        }
        finally
        {
            if (!Objects.isNull(channel))
            {
                channel.close();
            }

            this.fileWriter.close();
        }
    }
}
