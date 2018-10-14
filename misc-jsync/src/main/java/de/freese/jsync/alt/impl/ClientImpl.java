/**
 * Created: 22.10.2016
 */

package de.freese.jsync.alt.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.freese.jsync.alt.SyncStatus;
import de.freese.jsync.alt.api.Client;
import de.freese.jsync.alt.api.Options;
import de.freese.jsync.alt.api.Receiver;
import de.freese.jsync.alt.api.Sender;
import de.freese.jsync.alt.api.SyncItem;

/**
 * Basis-Implementierung des {@link Client}.
 *
 * @author Thomas Freese
 */
public class ClientImpl implements Client
{
    /**
     *
     */
    private final Options options;

    /**
     * Erstellt ein neues {@link ClientImpl} Object.
     *
     * @param options {@link Options}
     */
    public ClientImpl(final Options options)
    {
        super();

        Objects.requireNonNull(options, "options required");

        this.options = options;
    }

    /**
     * @see de.freese.jsync.alt.api.Client#sync(java.lang.String, java.lang.String)
     */
    @Override
    public void sync(final String source, final String destination) throws Exception
    {
        Path src = Paths.get(source);
        Path dst = Paths.get(destination);

        Sender sender = new SenderImpl(getOptions(), src);
        Receiver receiver = new ReceiverImpl(getOptions(), dst);

        if (getOptions().isDryRun())
        {
            getOptions().getPrintWriter().println();
            getOptions().getPrintWriter().println("Dry-Run: No file operations will be executed");
            getOptions().getPrintWriter().println();
        }

        getOptions().getPrintWriter().println("Generating FileList");

        // Receiver zuerst, da er asynchron ausgeführt wird.
        Future<Map<String, SyncItem>> futureReceiver = receiver.createFileMap();

        // Sender synchron ausführen.
        Future<List<SyncItem>> futureSender = sender.createFileList();

        List<SyncItem> fileListSender = futureSender.get();
        Map<String, SyncItem> fileMapReceiver = futureReceiver.get();

        // Listen mergen.
        List<SyncPair> fileList = createFileList(fileListSender, fileMapReceiver);

        getOptions().getPrintWriter().println();
        getOptions().getPrintWriter().println();

        // Löschen
        if (getOptions().isDelete())
        {
            delete(receiver, fileList);
        }

        getOptions().getPrintWriter().println();

        // Verzeichnisse anlegen
        createDirectories(sender, receiver, fileList);

        // Verzeichnisse aktualisieren, erst nach Kopie der Dateien, sonst Änderung im Timestamp

        // Dateien anlegen
        createFiles(sender, receiver, fileList);

        // Dateien aktualisieren
        updateFiles(sender, receiver, fileList);

        // Dateien kopieren
        copyFiles(sender, receiver, fileList);

        // Verzeichnisse aktualisieren
        updateDirectories(sender, receiver, fileList);

        getOptions().getPrintWriter().println();
        getOptions().getPrintWriter().println("ready");
    }

    /**
     * Vereinigt die Ergebnisse vom {@link Sender} und vom {@link Receiver}.<br>
     * Die Einträge des Senders sind die Referenz.<br>
     * Ist ein Eintrag im Receiver nicht enthalten, muss er kopiert werden.<br>
     * Ist ein Eintrag nur Receiver enthalten, muss er dort gelöscht werden.<br>
     *
     * @param fileListSender {@link List}
     * @param fileMapReceiver {@link Map}
     * @return {@link List}
     */
    private List<SyncPair> createFileList(final List<SyncItem> fileListSender, final Map<String, SyncItem> fileMapReceiver)
    {
        // Stream.concat(futureSender.get().stream(), futureReceiver.get().stream())

        // @formatter:off
        List<SyncPair> fileList = fileListSender.parallelStream()
                .map(item -> new SyncPair(item, fileMapReceiver.remove(item.getPath())))
                .collect(Collectors.toList());

         // Was jetzt noch in der Receiver-Map drin ist, muss gelöscht werden (source = null).
         fileMapReceiver.forEach((path, item) -> fileList.add(new SyncPair(null, item)));

        // SyncStatus ermitteln.
        AtomicInteger i = new AtomicInteger(1);

        fileList.stream()
                .peek(pair -> getOptions().getPrintWriter().printf("\r%d Files             ", i.getAndIncrement()))
                .forEach(SyncPair::validateStatus);
        // @formatter:on

        return fileList;
    }

    /**
     * Kopiert die Datei von der Quelle (Source) in das Ziel (Destination).
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @param item {@link SyncItem}
     */
    protected void copyFile(final Sender sender, final Receiver receiver, final FileSyncItem item)
    {
        getOptions().getPrintWriter().printf("copy %s%n", item.getPath());

        if (getOptions().isDryRun())
        {
            return;
        }

        try
        {
            sender.copy(receiver, item.getPath());
            receiver.updateFile(item.getPath(), item.getPermissionsToString(), item.getLastModifiedTime(), item.getGroup().getName(),
                    item.getUser().getName());
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Kopieren der Dateien auf den {@link Receiver}<br>
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @param fileList {@link List}
     */
    protected void copyFiles(final Sender sender, final Receiver receiver, final List<SyncPair> fileList)
    {
        Predicate<SyncPair> isFile = p -> p.getSource() instanceof FileSyncItem;
        Predicate<SyncPair> isOnlyInSource = p -> SyncStatus.ONLY_IN_SOURCE.equals(p.getStatus());
        Predicate<SyncPair> isDifferentContent = p -> SyncStatus.DIFFERENT_CONTENT.equals(p.getStatus());

        // @formatter:off
        fileList.stream()
                .filter(isFile.and(isOnlyInSource.or(isDifferentContent)))
                .forEach(pair -> copyFile(sender, receiver, (FileSyncItem) pair.getSource()));
        //@formatter:on
    }

    /**
     * Anlegen der Verzeichnisse auf dem {@link Receiver}.<br>
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @param fileList {@link List}
     */
    protected void createDirectories(final Sender sender, final Receiver receiver, final List<SyncPair> fileList)
    {
        Predicate<SyncPair> isDirectory = p -> p.getSource() instanceof DirectorySyncItem;
        Predicate<SyncPair> isOnlyInSource = p -> SyncStatus.ONLY_IN_SOURCE.equals(p.getStatus());

        // @formatter:off
        fileList.stream()
                .filter(isDirectory.and(isOnlyInSource))
                .forEach(pair -> createDirectory(sender, receiver, (DirectorySyncItem) pair.getSource()));
        //@formatter:on
    }

    /**
     * Anlegen eines Verzeichnisses auf dem {@link Receiver}.<br>
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @param item {@link DirectorySyncItem}
     */
    protected void createDirectory(final Sender sender, final Receiver receiver, final DirectorySyncItem item)
    {
        if (getOptions().isDryRun())
        {
            return;
        }

        try
        {
            receiver.createDirectory(item.getPath(), item.getPermissionsToString(), item.getLastModifiedTime(), item.getGroup().getName(),
                    item.getUser().getName());
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Anlegen einer Datei auf dem {@link Receiver}.<br>
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @param item {@link FileSyncItem}
     */
    protected void createFile(final Sender sender, final Receiver receiver, final FileSyncItem item)
    {
        if (getOptions().isDryRun())
        {
            return;
        }

        try
        {
            receiver.createFile(item.getPath(), item.getPermissionsToString(), item.getLastModifiedTime(), item.getGroup().getName(),
                    item.getUser().getName());
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Anlegen der Datien auf dem {@link Receiver}.<br>
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @param fileList {@link List}
     */
    protected void createFiles(final Sender sender, final Receiver receiver, final List<SyncPair> fileList)
    {
        Predicate<SyncPair> isFile = p -> p.getSource() instanceof FileSyncItem;
        Predicate<SyncPair> isOnlyInSource = p -> SyncStatus.ONLY_IN_SOURCE.equals(p.getStatus());

        // @formatter:off
        fileList.stream()
                .filter(isFile.and(isOnlyInSource))
                .forEach(pair -> createFile(sender, receiver, (FileSyncItem) pair.getSource()));
        //@formatter:on
    }

    /**
     * Löschen der Verzeichnisse und Dateien mit relativem Pfad zum Basis-Verzeichnis.<br>
     * Die Reihenfolge wird umgekehrt, damit Verzeichnisse zuletzt gelöscht werden.
     *
     * @param receiver {@link Receiver}
     * @param fileList {@link List}
     */
    protected void delete(final Receiver receiver, final List<SyncPair> fileList)
    {
        Predicate<SyncPair> isOnlyInDest = p -> SyncStatus.ONLY_IN_DEST.equals(p.getStatus());

        // @formatter:off
        fileList.stream()
                .filter(isOnlyInDest)
                .sorted(Comparator.comparing(SyncPair::getPath).reversed())
                .forEach(pair -> delete(receiver, pair.getDestination()));
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
        getOptions().getPrintWriter().printf("delete %s%n", item.getPath());

        if (getOptions().isDryRun())
        {
            return;
        }

        try
        {
            receiver.delete(item.getPath());
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return {@link Options}
     */
    protected Options getOptions()
    {
        return this.options;
    }

    /**
     * Aktualisieren der Verzeichnisse auf dem {@link Receiver}.<br>
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @param fileList {@link List}
     */
    protected void updateDirectories(final Sender sender, final Receiver receiver, final List<SyncPair> fileList)
    {
        Predicate<SyncPair> isDirectory = p -> p.getSource() instanceof DirectorySyncItem;
        Predicate<SyncPair> isDifferentContent = p -> SyncStatus.DIFFERENT_CONTENT.equals(p.getStatus());
        Predicate<SyncPair> isDifferentPermission = p -> SyncStatus.DIFFERENT_PERMISSIONS.equals(p.getStatus());

        // @formatter:off
        fileList.stream()
                .filter(isDirectory.and(isDifferentContent.or(isDifferentPermission)))
                .forEach(pair -> updateDirectory(sender, receiver, (DirectorySyncItem) pair.getSource()));
        //@formatter:on
    }

    /**
     * Aktualisieren eines Verzeichnisses auf dem {@link Receiver}.<br>
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @param item {@link DirectorySyncItem}
     */
    protected void updateDirectory(final Sender sender, final Receiver receiver, final DirectorySyncItem item)
    {
        if (getOptions().isDryRun())
        {
            return;
        }

        try
        {
            receiver.updateDirectory(item.getPath(), item.getPermissionsToString(), item.getLastModifiedTime(), item.getGroup().getName(),
                    item.getUser().getName());
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Aktualisieren einer Datei auf dem {@link Receiver}.<br>
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @param item {@link DirectorySyncItem}
     */
    protected void updateFile(final Sender sender, final Receiver receiver, final FileSyncItem item)
    {
        if (getOptions().isDryRun())
        {
            return;
        }

        try
        {
            receiver.updateFile(item.getPath(), item.getPermissionsToString(), item.getLastModifiedTime(), item.getGroup().getName(),
                    item.getUser().getName());
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Aktualisieren der Dateien auf dem {@link Receiver}.<br>
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @param fileList {@link List}
     */
    protected void updateFiles(final Sender sender, final Receiver receiver, final List<SyncPair> fileList)
    {
        Predicate<SyncPair> isFile = p -> p.getSource() instanceof FileSyncItem;
        Predicate<SyncPair> isDifferentPermission = p -> SyncStatus.DIFFERENT_PERMISSIONS.equals(p.getStatus());

        // @formatter:off
        fileList.stream()
                .filter(isFile.and(isDifferentPermission))
                .forEach(pair -> updateDirectory(sender, receiver, (DirectorySyncItem) pair.getSource()));
        //@formatter:on
    }
}
