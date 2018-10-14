/**
 * Created: 22.10.2016
 */
package de.freese.jsync.alt.impl;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.BiConsumer;

import de.freese.jsync.alt.MonitoringWritableByteChannel;
import de.freese.jsync.alt.api.Generator;
import de.freese.jsync.alt.api.Options;
import de.freese.jsync.alt.api.Receiver;
import de.freese.jsync.alt.api.Sender;
import de.freese.jsync.alt.api.SyncItem;

/**
 * Basis-Implementierung des {@link Sender}.
 *
 * @author Thomas Freese
 */
public class SenderImpl implements Sender
{
    /**
     *
     */
    private final Path base;

    /**
     *
     */
    private final Options options;

    /**
     * Erstellt ein neues {@link SenderImpl} Object.
     *
     * @param options {@link Options}
     * @param base {@link Path}
     */
    public SenderImpl(final Options options, final Path base)
    {
        super();

        Objects.requireNonNull(options, "options required");
        Objects.requireNonNull(base, "base required");

        this.options = options;
        this.base = base;
    }

    /**
     * @see de.freese.jsync.alt.api.Sender#copy(de.freese.jsync.alt.api.Receiver, java.lang.String)
     */
    @Override
    public void copy(final Receiver receiver, final String file) throws Exception
    {
        boolean useChannels = true;

        if (!useChannels)
        {
            Files.copy(getBase().resolve(file), ((ReceiverImpl) receiver).getBase().resolve(file), StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES);
        }
        else
        {
            BiConsumer<Long, Long> monitor = (gesamt, written) -> getOptions().getPrintWriter().printf("\r%d / %d%n", written, gesamt);
            long size = Files.size(getBase().resolve(file));

            try (ReadableByteChannel srcChannel = getChannel(file);
                 WritableByteChannel dstChannel = new MonitoringWritableByteChannel(receiver.getChannel(file), size, monitor))
            {
                ByteBuffer buffer = ByteBuffer.allocateDirect(Options.BUFFER_SIZE);

                while (srcChannel.read(buffer) != -1)
                {
                    // prepare the buffer to be drained
                    buffer.flip();

                    // write to the channel, may block
                    dstChannel.write(buffer);

                    // If partial transfer, shift remainder down
                    // If buffer is empty, same as doing clear()
                    buffer.compact();
                }

                // EOF will leave buffer in fill state
                buffer.flip();

                // make sure the buffer is fully drained.
                while (buffer.hasRemaining())
                {
                    dstChannel.write(buffer);
                }

                buffer.clear();
            }
        }

        // try (FileInputStream inStream = new FileInputStream(aSourceFile);
        // FileChannel inChannel inChannel = inStream.getChannel();
        // FileOutputStream outStream = new FileOutputStream(aTargetFile);
        // FileChannel outChannel = outStream.getChannel())
        // {
        // long bytesTransferred = 0;
        //
        // while (bytesTransferred < inChannel.size())
        // {
        // bytesTransferred += inChannel.transferTo(bytesTransferred, inChannel.size(), outChannel);
        // }
        // }

        // try (FileChannel inputChannel = new FileInputStream(sourceFile).getChannel();
        // FileChannel outputChannel = new FileOutputStream(targetFile).getChannel())
        // {
        // ByteBuffer buffer = ByteBuffer.allocateDirect(Options.BUFFER_SIZE);
        //
        // while (inputChannel.read(buffer) != -1)
        // {
        // buffer.flip();
        //
        // while (buffer.hasRemaining())
        // {
        // outputChannel.write(buffer);
        // }
        //
        // buffer.clear();
        // }
        // }
    }

    /**
     * @see de.freese.jsync.alt.api.Sender#createFileList()
     */
    @Override
    public Future<List<SyncItem>> createFileList() throws Exception
    {
        Generator generator = new GeneratorImpl(getOptions(), getBase());
        FutureTask<List<SyncItem>> futureTask = generator.createFileList();

        futureTask.run(); // Synchron laufen Lassen.
        // getOptions().getExecutor().execute(futureTask);

        return futureTask;
    }

    /**
     * @see de.freese.jsync.alt.api.Sender#getChannel(java.lang.String)
     */
    @Override
    public ReadableByteChannel getChannel(final String file) throws Exception
    {
        return Files.newByteChannel(getBase().resolve(file), StandardOpenOption.READ);
    }

    /**
     * Liefert das Basis-Verzeichnis.
     *
     * @return base {@link Path}
     */
    protected Path getBase()
    {
        return this.base;
    }

    /**
     * @return {@link Options}
     */
    protected Options getOptions()
    {
        return this.options;
    }
}
