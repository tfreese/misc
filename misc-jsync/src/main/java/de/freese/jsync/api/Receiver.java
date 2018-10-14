/**
 * Created on 22.10.2016 10:42:26
 */
package de.freese.jsync.api;

import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.concurrent.Future;
import de.freese.jsync.impl.generator.DirectorySyncItem;
import de.freese.jsync.impl.generator.FileSyncItem;

/**
 * As a role the receiver is the destination system.<br>
 * As a process the receiver is the process that receives update data and writes it to disk.<br>
 *
 * @see <a href="https://rsync.samba.org/how-rsync-works.html">How rsync works</a>
 * @author Thomas Freese
 */
public interface Receiver
{
    /**
     * Erzeugt die Map aller SyncItems (Verzeichnisse, Dateien) des Basis-Verzeichnisses.<br>
     * Der Task wird jedoch noch nicht ausgeführt.
     *
     * @return {@link Future}
     */
    public Future<Map<String, SyncItem>> createTargetSyncItems();

    /**
     * Löscht eine Datei oder Verzeichnis.
     *
     * @param fileDir String
     * @throws Exception Falls was schief geht.
     */
    public void delete(String fileDir) throws Exception;

    /**
     * Liefert den Channel zur Ziel-Datei.
     *
     * @param file String
     * @return {@link WritableByteChannel}
     * @throws Exception Falls was schief geht.
     */
    public WritableByteChannel getChannel(final String file) throws Exception;

    /**
     * Aktualisiert ein Verzeichnis.
     *
     * @param syncItem {@link DirectorySyncItem}
     * @throws Exception Falls was schief geht.
     */
    public void updateDirectory(DirectorySyncItem syncItem) throws Exception;

    /**
     * Aktualisiert ein Verzeichnis.
     *
     * @param syncItem {@link FileSyncItem}
     * @throws Exception Falls was schief geht.
     */
    public void updateFile(FileSyncItem syncItem) throws Exception;
}
