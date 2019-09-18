/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.repository;

import java.io.InputStream;

/**
 * Interface eines Remote-Repositories.
 *
 * @author Thomas Freese
 */
public interface RemoteRepository
{
    /**
     * Prüft, ob die Resource vorhanden ist.<br>
     *
     * @param resource String
     * @return boolean
     * @throws Exception Falls was schief geht.
     */
    public boolean exist(String resource) throws Exception;

    /**
     * Laden der betreffenden Resource.<br>
     * Der Stream ist null, wenn die Resource nicht existiert.
     *
     * @param resource String
     * @return {@link InputStream}
     * @throws Exception Falls was schief geht.
     */
    public InputStream getInputStream(String resource) throws Exception;
}
