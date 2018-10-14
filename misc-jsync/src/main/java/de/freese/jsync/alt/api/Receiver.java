/**
 * Created on 22.10.2016 10:42:26
 */
package de.freese.jsync.alt.api;

import java.nio.channels.WritableByteChannel;
import java.util.Map;
import java.util.concurrent.Future;

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
     * Erzeugt ein Verzeichnis.
     *
     * @param directory String
     * @param permissions String; In der Form "rwxr-xr-x"
     * @param lastModifiedTime long; TimeUnit = SECONDS
     * @param group String
     * @param user String
     * @throws Exception Falls was schief geht.
     */
    public void createDirectory(String directory, String permissions, long lastModifiedTime, String group, String user) throws Exception;

    /**
     * Erzeugt eine Datei.
     *
     * @param file String
     * @param permissions String; In der Form "rwxr-xr-x"
     * @param lastModifiedTime long; TimeUnit = SECONDS
     * @param group String
     * @param user String
     * @throws Exception Falls was schief geht.
     */
    public void createFile(String file, String permissions, long lastModifiedTime, String group, String user) throws Exception;

    /**
     * Erzeugt die File-Map des Basis-Verzeichnisses.
     *
     * @return {@link Future}
     * @throws Exception Falls was schief geht.
     */
    public Future<Map<String, SyncItem>> createFileMap() throws Exception;

    /**
     * Löscht eine Datei oder Verzeichnis.
     *
     * @param fileDir String
     * @throws Exception Falls was schief geht.
     */
    public void delete(String fileDir) throws Exception;

    // /**
    // * Löscht ein Verzeichnis.
    // *
    // * @param directory String
    // * @throws Exception Falls was schief geht.
    // */
    // public void deleteDirectory(String directory) throws Exception;
    //
    // /**
    // * Löscht eine Datei.
    // *
    // * @param file String
    // * @throws Exception Falls was schief geht.
    // */
    // public void deleteFile(String file) throws Exception;

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
     * @param directory String
     * @param permissions String; In der Form "rwxr-xr-x"
     * @param lastModifiedTime long; TimeUnit = SECONDS
     * @param group String
     * @param user String
     * @throws Exception Falls was schief geht.
     */
    public void updateDirectory(String directory, String permissions, long lastModifiedTime, String group, String user) throws Exception;

    /**
     * Aktualisiert ein Verzeichnis.
     *
     * @param file String
     * @param permissions String; In der Form "rwxr-xr-x"
     * @param lastModifiedTime long; TimeUnit = SECONDS
     * @param group String
     * @param user String
     * @throws Exception Falls was schief geht.
     */
    public void updateFile(String file, String permissions, long lastModifiedTime, String group, String user) throws Exception;
}
