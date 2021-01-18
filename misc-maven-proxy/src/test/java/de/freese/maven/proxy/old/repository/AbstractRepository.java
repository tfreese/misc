/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.old.repository;

import java.net.URI;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisimplementierung eines Repositories.
 *
 * @author Thomas Freese
 */
public abstract class AbstractRepository implements Repository
{
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final URI uri;

    /**
     * Erstellt ein neues {@link AbstractRepository} Object.
     *
     * @param uri {@link URI}; Ressourenquelle des Repositories
     */
    protected AbstractRepository(final URI uri)
    {
        super();

        this.uri = Objects.requireNonNull(uri, "repository required");
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.maven.proxy.old.repository.Repository#getUri()
     */
    @Override
    public URI getUri()
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
