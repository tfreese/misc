/**
 * Created: 15.09.2019
 */

package de.freese.maven.proxy.repository.http;

import java.net.URI;
import java.util.Objects;
import de.freese.maven.proxy.repository.AbstractRemoteRepository;

/**
 * Basisimplementierung eines HTTP-Repositories.
 *
 * @author Thomas Freese
 */
public abstract class AbstractHttpRepository extends AbstractRemoteRepository implements HttpRepository
{
    /**
    *
    */
    private final URI uri;

    /**
     * Erstellt ein neues {@link AbstractHttpRepository} Object.
     *
     * @param uri {@link URI}; Quelle des Repositories
     */
    public AbstractHttpRepository(final URI uri)
    {
        super();

        this.uri = Objects.requireNonNull(uri, "repository required");

        String scheme = uri.getScheme();

        if ((scheme == null) || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")))
        {
            String msg = "HTTP or HTTPS protocol required: " + scheme;

            getLogger().error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Liefert den Ort des Repositories.
     *
     * @return {@link URI}
     */
    protected URI getUri()
    {
        return this.uri;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getUri().toString();
    }
}
