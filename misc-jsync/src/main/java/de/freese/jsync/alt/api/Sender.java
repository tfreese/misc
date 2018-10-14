/**
 * Created on 22.10.2016 10:42:26
 */
package de.freese.jsync.alt.api;

import java.nio.channels.ReadableByteChannel;
import java.util.List;
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
     * Kopiert die Datei (relativer Pfad) von der Quelle (Source) in das Ziel (Destination).
     *
     * @param receiver {@link Receiver}
     * @param file String
     * @throws Exception Falls was schief geht.
     */
    public void copy(final Receiver receiver, final String file) throws Exception;

    /**
     * Erzeugt die File-List des Basis-Verzeichnisses.
     *
     * @return {@link Future}
     * @throws Exception Falls was schief geht.
     */
    public Future<List<SyncItem>> createFileList() throws Exception;

    /**
     * Liefert den Channel von der Quell-Datei.
     *
     * @param file String
     * @return {@link ReadableByteChannel}
     * @throws Exception Falls was schief geht.
     */
    public ReadableByteChannel getChannel(final String file) throws Exception;
}
