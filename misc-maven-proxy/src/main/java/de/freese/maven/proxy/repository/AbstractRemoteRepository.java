/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisimplementierung eines {@link RemoteRepository}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractRemoteRepository implements RemoteRepository
{
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractRemoteRepository} Object.
     */
    public AbstractRemoteRepository()
    {
        super();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

}
