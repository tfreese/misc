// Created: 05.04.2018
package de.freese.jsync.impl.client;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import de.freese.jsync.SyncStatus;
import de.freese.jsync.api.Client;
import de.freese.jsync.api.Options;
import de.freese.jsync.api.Receiver;
import de.freese.jsync.api.Sender;
import de.freese.jsync.api.SyncItem;
import de.freese.jsync.impl.SyncPair;

/**
 * Default-Implementierung des {@link Client}.
 *
 * @author Thomas Freese
 */
public class DefaultClient extends AbstractClient
{
    /**
     * Erzeugt eine neue Instanz von {@link DefaultClient}.
     *
     * @param options {@link Options}
     */
    public DefaultClient(final Options options)
    {
        super(options);
    }

    /**
     * @see de.freese.jsync.api.Client#createSyncList(de.freese.jsync.api.Sender, de.freese.jsync.api.Receiver)
     */
    @Override
    public List<SyncPair> createSyncList(final Sender sender, final Receiver receiver) throws Exception
    {
        if (getOptions().isDryRun())
        {
            getOptions().getPrintWriter().println();
            getOptions().getPrintWriter().println("Dry-Run: No file operations will be executed");
            getOptions().getPrintWriter().println();
        }

        getOptions().getPrintWriter().println("Generating FileList");

        // Sender
        Future<Map<String, SyncItem>> futureSender = sender.createSourceSyncItems();
        Map<String, SyncItem> fileMapSender = futureSender.get();

        // Receiver
        Future<Map<String, SyncItem>> futureReceiver = receiver.createTargetSyncItems();
        Map<String, SyncItem> fileMapReceiver = futureReceiver.get();

        // Listen mergen.
        List<SyncPair> syncList = mergeSyncItems(fileMapSender, fileMapReceiver);

        return syncList;
    }

    /**
     * @see de.freese.jsync.api.Client#syncReceiver(de.freese.jsync.api.Sender, de.freese.jsync.api.Receiver, java.util.List)
     */
    @Override
    public void syncReceiver(final Sender sender, final Receiver receiver, final List<SyncPair> syncList) throws Exception
    {
        getOptions().getPrintWriter().println();

        // Alles rausfiltern was synchronized ist.
        Predicate<SyncPair> isSynchronised = p -> SyncStatus.SYNCHRONIZED.equals(p.getStatus());
        List<SyncPair> list = syncList.stream().filter(isSynchronised.negate()).collect(Collectors.toList());

        // Löschen
        if (getOptions().isDelete())
        {
            delete(receiver, list);
        }

        // Verzeichnisse erst aktualisieren nach Kopie der Dateien, sonst Änderung im Timestamp.

        // Dateien kopieren
        copyFiles(sender, receiver, list);

        // Aktualisieren von Datei-Attributen.
        updateFiles(receiver, list);

        // Verzeichnisse aktualisieren
        updateDirectories(receiver, list);

        getOptions().getPrintWriter().println();
        getOptions().getPrintWriter().println();
        getOptions().getPrintWriter().println("ready");
    }
}
