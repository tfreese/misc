/**
 * Created on 22.10.2016 10:42:26
 */
package de.freese.jsync.alt.api;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * The generator process identifies changed files and manages the file level logic.
 * <p>
 * The generator process compares the file list with its local directory tree.<br>
 * Prior to beginning its primary function, if --delete has been specified,<br>
 * it will first identify local files not on the sender and delete them on the receiver.
 * <p>
 * The generator will then start walking the file list.<br>
 * Each file will be checked to see if it can be skipped.<br>
 * In the most common mode of operation files are not skipped if the modification time or size differs.<br>
 * If --checksum was specified a file-level checksum will be created and compared.<br>
 * Directories, device nodes and symlinks are not skipped.<br>
 * Missing directories will be created.
 *
 * @see <a href="https://rsync.samba.org/how-rsync-works.html">How rsync works</a>
 * @author Thomas Freese
 */
public interface Generator
{
    /**
     * Erzeugt die File-List des Basis-Verzeichnisses.<br>
     * Wird vom {@link Sender} verwendet.
     *
     * @return {@link FutureTask}
     * @throws Exception Falls was schief geht.
     */
    public FutureTask<List<SyncItem>> createFileList() throws Exception;

    /**
     * Erzeugt die File-Map des Basis-Verzeichnisses.<br>
     * Wird vom {@link Receiver} verwendet.
     *
     * @return {@link FutureTask}
     * @throws Exception Falls was schief geht.
     */
    public FutureTask<Map<String, SyncItem>> createFileMap() throws Exception;

    /**
     * Liefert das Basis-Verzeichnis.
     *
     * @return base {@link Path}
     */
    public Path getBase();
}
