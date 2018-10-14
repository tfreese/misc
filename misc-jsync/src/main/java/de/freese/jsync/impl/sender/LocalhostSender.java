// Created: 05.04.2018
package de.freese.jsync.impl.sender;

import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;

import de.freese.jsync.api.Generator;
import de.freese.jsync.api.Options;
import de.freese.jsync.api.Sender;
import de.freese.jsync.api.SyncItem;
import de.freese.jsync.impl.generator.DefaultGenerator;

/**
 * {@link Sender} für Localhost-Quellen.
 *
 * @author Thomas Freese
 */
public class LocalhostSender extends AbstractSender
{
    /**
     * Erzeugt eine neue Instanz von {@link LocalhostSender}.
     *
     * @param options {@link Options}
     * @param base {@link Path}
     */
    public LocalhostSender(final Options options, final Path base)
    {
        super(options, base);
    }

    /**
     * @see de.freese.jsync.api.Sender#createSourceSyncItems()
     */
    @Override
    public Future<Map<String, SyncItem>> createSourceSyncItems()
    {
        Generator generator = new DefaultGenerator(getOptions(), getBase());
        RunnableFuture<Map<String, SyncItem>> futureTask = generator.createSyncItems();

        futureTask.run(); // Synchron laufen Lassen.
        // getOptions().getExecutor().execute(futureTask);

        return futureTask;
    }

    /**
     * @see de.freese.jsync.api.Sender#getChannel(java.lang.String)
     */
    @Override
    public ReadableByteChannel getChannel(final String file) throws Exception
    {
        return Files.newByteChannel(getBase().resolve(file), StandardOpenOption.READ);
    }
}
