/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.old.repository;

import java.net.URI;
import de.freese.maven.proxy.old.model.MavenRequest;
import de.freese.maven.proxy.old.model.MavenResponse;

/**
 * Interface eines Repositories.
 *
 * @author Thomas Freese
 */
public interface Repository
{
    /**
     * Prüft, ob die Datei vorhanden ist.<br>
     *
     * @param mavenRequest {@link MavenRequest}
     * @return {@link MavenResponse}
     * @throws Exception Falls was schief geht.
     */
    public MavenResponse exist(MavenRequest mavenRequest) throws Exception;

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
