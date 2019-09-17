/**
 * Created: 15.09.2019
 */

package de.freese.maven.proxy.repository.http;

import java.net.URI;
import de.freese.maven.proxy.repository.AbstractRepository;

/**
 * Basisimplementierung eines HTTP-Repositories.
 *
 * @author Thomas Freese
 */
public abstract class AbstractHttpRepository extends AbstractRepository implements HttpRepository
{
    /**
     * Erstellt ein neues {@link AbstractHttpRepository} Object.
     *
     * @param uri {@link URI}; Quelle des Repositories
     */
    public AbstractHttpRepository(final URI uri)
    {
        super(uri);

        String scheme = uri.getScheme();

        if ((scheme == null) || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")))
        {
            String msg = "HTTP or HTTPS protocol required: " + scheme;

            getLogger().error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#dispose()
     */
    @Override
    public void dispose()
    {
        // Empty
    }
}
