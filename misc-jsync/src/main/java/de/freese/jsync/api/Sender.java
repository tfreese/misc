/**
 * Created on 22.10.2016 10:42:26
 */
package de.freese.jsync.api;

import java.nio.channels.ReadableByteChannel;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * As a role the sender is the source system.<br>
 * As a process the receiver is the process that has access to the source files being synchronised.<br>
 *
 * @see <a href="https://rsync.samba.org/how-rsync-works.html">How rsync works</a>
 * @author Thomas Freese
 */
public interface Sender
{
    /**
     * Erzeugt die Map aller SyncItems (Verzeichnisse, Dateien) des Basis-Verzeichnisses.<br>
     * Der Task wird jedoch noch nicht ausgeführt.
     *
     * @return {@link Future}
     */
    public Future<Map<String, SyncItem>> createSourceSyncItems();

    /**
     * Liefert den Channel der Quell-Datei.
     *
     * @param file String
     * @return {@link ReadableByteChannel}
     * @throws Exception Falls was schief geht.
     */
    public ReadableByteChannel getChannel(final String file) throws Exception;
}
