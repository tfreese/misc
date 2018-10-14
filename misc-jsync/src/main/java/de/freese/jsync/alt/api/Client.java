/**
 * Created on 22.10.2016 10:42:26
 */
package de.freese.jsync.alt.api;

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
     * Synchronisiert das Ziel-Verzeichnis mit der Quelle.
     *
     * @param source String
     * @param destination String
     * @throws Exception Falls was schief geht.
     */
    public void sync(String source, String destination) throws Exception;
}
