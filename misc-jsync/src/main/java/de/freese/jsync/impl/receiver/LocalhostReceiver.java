// Created: 05.04.2018
package de.freese.jsync.impl.receiver;

import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;

import de.freese.jsync.api.Generator;
import de.freese.jsync.api.Options;
import de.freese.jsync.api.Receiver;
import de.freese.jsync.api.SyncItem;
import de.freese.jsync.impl.generator.DefaultGenerator;

/**
 * {@link Receiver} für Localhost-Ziele.
 *
 * @author Thomas Freese
 */
public class LocalhostReceiver extends AbstractReceiver
{
    /**
     * Erzeugt eine neue Instanz von {@link LocalhostReceiver}.
     *
     * @param options {@link Options}
     * @param base {@link Path}
     */
    public LocalhostReceiver(final Options options, final Path base)
    {
        super(options, base);
    }

    /**
     * @see de.freese.jsync.api.Receiver#createTargetSyncItems()
     */
    @Override
    public Future<Map<String, SyncItem>> createTargetSyncItems()
    {
        Generator generator = new DefaultGenerator(getOptions(), getBase());
        RunnableFuture<Map<String, SyncItem>> futureTask = generator.createSyncItems();

        futureTask.run(); // Synchron laufen Lassen.
        // getOptions().getExecutor().execute(futureTask);

        return futureTask;
    }

    /**
     * @see de.freese.jsync.api.Receiver#getChannel(java.lang.String)
     */
    @Override
    public WritableByteChannel getChannel(final String file) throws Exception
    {
        Path path = getBase().resolve(file);

        if (!Files.exists(path.getParent()))
        {
            Files.createDirectories(path.getParent());
        }

        return Files.newByteChannel(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
