/**
 * Created on 22.10.2016 10:42:26
 */
package de.freese.jsync.api;

import java.util.List;

import de.freese.jsync.impl.SyncPair;

/**
 * The client initiates the synchronisation.<br>
 * When an Rsync client is started it will first establish a connection with a server process.<br>
 *
 * @see <a href="https://rsync.samba.org/how-rsync-works.html">How rsync works</a>
 * @author Thomas Freese
 */
public interface Client
{
    /**
     * Ermittelt die Differenzen von Quelle und Ziel.
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     */
    public List<SyncPair> createSyncList(Sender sender, Receiver receiver) throws Exception;

    /**
     * Synchronisiert das Ziel-Verzeichnis mit der Quelle.
     *
     * @param sender {@link Sender}
     * @param receiver {@link Receiver}
     * @param syncList {@link List}
     * @throws Exception Falls was schief geht.
     */
    public void syncReceiver(Sender sender, Receiver receiver, List<SyncPair> syncList) throws Exception;
}
