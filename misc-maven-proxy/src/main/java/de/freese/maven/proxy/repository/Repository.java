/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.repository;

import java.net.URI;
import de.freese.maven.proxy.model.MavenRequest;
import de.freese.maven.proxy.model.MavenResponse;

/**
 * Interface eines Repositories.
 *
 * @author Thomas Freese
 */
public interface Repository
{
    /**
     * Beenden und aufräumen.
     */
    public void dispose();

    /**
     * Prüft, ob die Datei vorhanden ist.<br>
     *
     * @param mavenRequest {@link MavenRequest}
     * @return {@link MavenResponse}
     * @throws Exception Falls was schief geht.
     */
    public MavenResponse exist(MavenRequest mavenRequest) throws Exception;

    /**
     * Liefert den eindeitigen Namen des Repositories (URL, Path).
     *
     * @return String
     */
    public default String getName()
    {
        return getUri().toString();
    }

    /**
     * Laden der betreffenden Datei.<br>
     *
     * @param mavenRequest {@link MavenRequest}
     * @return {@link MavenResponse}
     * @throws Exception Falls was schief geht.
     */
    public MavenResponse getResource(MavenRequest mavenRequest) throws Exception;

    /**
     * Liefert den Ort des Repositories.
     *
     * @return {@link URI}
     */
    public URI getUri();
}
