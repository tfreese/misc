// Created: 05.04.2018
package de.freese.jsync.impl.client;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import de.freese.jsync.SyncStatus;
import de.freese.jsync.api.Client;
import de.freese.jsync.api.Options;
import de.freese.jsync.api.Receiver;
import de.freese.jsync.api.Sender;
import de.freese.jsync.api.SyncItem;
import de.freese.jsync.impl.MonitoringWritableByteChannel;
import de.freese.jsync.impl.SyncPair;
import de.freese.jsync.impl.generator.DirectorySyncItem;
import de.freese.jsync.impl.generator.FileSyncItem;

/**
 * Basis-Implementierung des {@link Client}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractClient implements Client
{
    /**
    *
    */
    private final Options options;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractClient}.
     *
     * @param options {@link Options}
     */
    public AbstractClient(final Options options)
    {
        super();

        this.options = Objects.requireNonNull(options, "options required");
    }

    /**
     * Kopieren der Dateien auf den {@link Receiver}<br>
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @param item {@link SyncItem}
     */
    protected void copyFile(final Sender sender, final Receiver receiver, final FileSyncItem item)
    {
        getOptions().getPrintWriter().printf("copy %s%n", item.getRelativePath());

        if (getOptions().isDryRun())
        {
            return;
        }

        try
        {
            BiConsumer<Long, Long> monitor =
                    (written, gesamt) -> getOptions().getPrintWriter().printf("\r%d / %d = %.2f %%", written, gesamt, Options.getPercent(written, gesamt));
            // long size = Files.size(getBase().resolve(file));
            long size = item.getSize();

            try (ReadableByteChannel readableByteChannel = sender.getChannel(item.getRelativePath());
                 WritableByteChannel writableByteChannel = new MonitoringWritableByteChannel(receiver.getChannel(item.getRelativePath()), monitor, size))
            {
                ByteBuffer buffer = ByteBuffer.allocateDirect(Options.BUFFER_SIZE);

                while (readableByteChannel.read(buffer) != -1)
                {
                    // prepare the buffer to be drained
                    buffer.flip();

                    while (buffer.hasRemaining())
                    {
                        writableByteChannel.write(buffer);
                    }

                    buffer.clear();
                }
            }

            // Attribute aktuelisieren.
            receiver.updateFile(item);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        // Files.copy(getBase().resolve(file), ((ReceiverImpl) receiver).getBase().resolve(file), StandardCopyOption.REPLACE_EXISTING,
        // StandardCopyOption.COPY_ATTRIBUTES);
        //
        // try (ReadableByteChannel srcChannel = getChannel(file);
        // WritableByteChannel dstChannel = new MonitoringWritableByteChannel(receiver.getChannel(file), size, monitor))
        // {
        // ByteBuffer buffer = ByteBuffer.allocateDirect(Options.BUFFER_SIZE);
        //
        // while (srcChannel.read(buffer) != -1)
        // {
        // // prepare the buffer to be drained
        // buffer.flip();
        //
        // // write to the channel, may block
        // dstChannel.write(buffer);
        //
        // // If partial transfer, shift remainder down
        // // If buffer is empty, same as doing clear()
        // buffer.compact();
        // }
        //
        // // EOF will leave buffer in fill state
        // buffer.flip();
        //
        // // make sure the buffer is fully drained.
        // while (buffer.hasRemaining())
        // {
        // dstChannel.write(buffer);
        // }
        //
        // buffer.clear();
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
    }

    /**
     * Kopieren der Dateien auf den {@link Receiver}<br>
     * {@link SyncStatus#ONLY_IN_SOURCE}<br>
     * {@link SyncStatus#DIFFERENT_LAST_MODIFIEDTIME}<br>
     * {@link SyncStatus#DIFFERENT_SIZE}<br>
     * {@link SyncStatus#DIFFERENT_CHECKSUM}<br>
     *
     * @param sender Sender
     * @param receiver {@link Receiver}
     * @param syncList {@link List}
     */
    protected void copyFiles(final Sender sender, final Receiver receiver, final List<SyncPair> syncList)
    {
        Predicate<SyncPair> isFile = p -> p.getSource() instanceof FileSyncItem;
        Predicate<SyncPair> isOnlyInSource = p -> SyncStatus.ONLY_IN_SOURCE.equals(p.getStatus());
        Predicate<SyncPair> isDifferentTimestamp = p -> SyncStatus.DIFFERENT_LAST_MODIFIEDTIME.equals(p.getStatus());
        Predicate<SyncPair> isDifferentSize = p -> SyncStatus.DIFFERENT_SIZE.equals(p.getStatus());
        Predicate<SyncPair> isDifferentChecksum = p -> SyncStatus.DIFFERENT_CHECKSUM.equals(p.getStatus());

        // @formatter:off
        syncList.stream()
                .filter(isFile.and(isOnlyInSource.or(isDifferentTimestamp).or(isDifferentSize).or(isDifferentChecksum)))
                .forEach(pair -> copyFile(sender,receiver, (FileSyncItem) pair.getSource()));
        //@formatter:on
    }

    /**
     * Löschen der Verzeichnisse und Dateien mit relativem Pfad zum Basis-Verzeichnis.<br>
     * Die Reihenfolge wird umgekehrt, damit Verzeichnisse zuletzt gelöscht werden.
     *
     * @param receiver {@link Receiver}
     * @param syncList {@link List}
     */
    protected void delete(final Receiver receiver, final List<SyncPair> syncList)
    {
        Predicate<SyncPair> isOnlyInTarget = p -> SyncStatus.ONLY_IN_TARGET.equals(p.getStatus());

        // @formatter:off
        syncList.stream()
                .filter(isOnlyInTarget)
                .sorted(Comparator.comparing(SyncPair::getRelativePath).reversed())
                .forEach(pair -> delete(receiver, pair.getTarget()));
        // @formatter:on
    }

    /**
     * Löscht ein Verzeichnis/Datei mit relativem Pfad zum Basis-Verzeichnis.
     *
     * @param receiver {@link Receiver}
     * @param item {@link SyncItem}
     */
    protected void delete(final Receiver receiver, final SyncItem item)
    {
        getOptions().getPrintWriter().printf("delete %s%n", item.getRelativePath());

        if (getOptions().isDryRun())
        {
            return;
        }

        try
        {
            receiver.delete(item.getRelativePath());
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return {@link de.freese.jsync.api.Options}
     */
    protected Options getOptions()
    {
        return this.options;
    }

    /**
     * Vereinigt die Ergebnisse vom {@link Sender} und vom {@link Receiver}.<br>
     * Die Einträge des Senders sind die Referenz.<br>
     * Ist ein Eintrag im Receiver nicht enthalten, muss er kopiert werden.<br>
     * Ist ein Eintrag nur Receiver enthalten, muss er dort gelöscht werden.<br>
     *
     * @param fileMapSender {@link Map}
     * @param fileMapReceiver {@link Map}
     * @return {@link List}
     */
    protected List<SyncPair> mergeSyncItems(final Map<String, SyncItem> fileMapSender, final Map<String, SyncItem> fileMapReceiver)
    {
        //
        // @formatter:off
        List<SyncPair> fileList = fileMapSender.entrySet()
                .parallelStream()
                .map(entry -> new SyncPair(entry.getValue(), fileMapReceiver.remove(entry.getKey())))
                .collect(Collectors.toList());

         // Was jetzt noch in der Receiver-Map drin ist, muss gelöscht werden (source = null).
         fileMapReceiver.forEach((key, value) -> fileList.add(new SyncPair(null, value)));

        // SyncStatus ermitteln.
        AtomicInteger i = new AtomicInteger(1);

        fileList.stream()
                .parallel()
                .peek(SyncPair::validateStatus)
                .sequential()
                .forEach(pair -> getOptions().getPrintWriter().printf("\r%3d Files", i.getAndIncrement()));
        // @formatter:on

        return fileList;
    }

    /**
     * Aktualisieren von Verzeichniss-Attributen auf dem {@link Receiver}.<br>
     *
     * @param receiver {@link Receiver}
     * @param syncList {@link List}
     */
    protected void updateDirectories(final Receiver receiver, final List<SyncPair> syncList)
    {
        Predicate<SyncPair> isDirectory = p -> p.getSource() instanceof DirectorySyncItem;

        // @formatter:off
        syncList.stream()
                .filter(isDirectory)
                .forEach(pair -> updateDirectory(receiver, (DirectorySyncItem) pair.getSource()));
        //@formatter:on
    }

    /**
     * Aktualisieren von Verzeichniss-Attributen auf dem {@link Receiver}.<br>
     *
     * @param receiver {@link Receiver}
     * @param item {@link DirectorySyncItem}
     */
    protected void updateDirectory(final Receiver receiver, final DirectorySyncItem item)
    {
        if (getOptions().isDryRun())
        {
            return;
        }

        try
        {
            receiver.updateDirectory(item);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Aktualisieren von Datei-Attributen auf dem {@link Receiver}.<br>
     *
     * @param receiver {@link Receiver}
     * @param item {@link FileSyncItem}
     */
    protected void updateFile(final Receiver receiver, final FileSyncItem item)
    {
        getOptions().getPrintWriter().printf("update attributes %s%n", item.getRelativePath());

        if (getOptions().isDryRun())
        {
            return;
        }

        try
        {
            receiver.updateFile(item);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Aktualisieren von Datei-Attributen auf dem {@link Receiver}.<br>
     * {@link SyncStatus#DIFFERENT_PERMISSIONS}<br>
     * {@link SyncStatus#DIFFERENT_USER}<br>
     * {@link SyncStatus#DIFFERENT_GROUP}<br>
     *
     * @param receiver {@link Receiver}
     * @param syncList {@link List}
     */
    protected void updateFiles(final Receiver receiver, final List<SyncPair> syncList)
    {
        Predicate<SyncPair> isFile = p -> p.getSource() instanceof FileSyncItem;
        Predicate<SyncPair> isDifferentPermission = p -> SyncStatus.DIFFERENT_PERMISSIONS.equals(p.getStatus());
        Predicate<SyncPair> isDifferentUser = p -> SyncStatus.DIFFERENT_USER.equals(p.getStatus());
        Predicate<SyncPair> isDifferentGroup = p -> SyncStatus.DIFFERENT_GROUP.equals(p.getStatus());

        // @formatter:off
        syncList.stream()
                .filter(isFile.and(isDifferentPermission.or(isDifferentUser).or(isDifferentGroup)))
                .forEach(pair -> updateFile(receiver, (FileSyncItem) pair.getSource()));
        //@formatter:on
    }
}
