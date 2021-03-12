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
    protected AbstractHttpRepository(final URI uri)
    {
        super();

        this.uri = Objects.requireNonNull(uri, "repository required");

        String scheme = uri.getScheme();

        if ((scheme == null) || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)))
        {
            String msg = "HTTP or HTTPS protocol required: " + scheme;

            getLogger().error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Liefert das absoluten Verzeichnis der Datei.
     *
     * @param baseUri {@link URI}
     * @param resource String
     * @return {@link URI}
     */
    protected URI createResourceUri(final URI baseUri, final String resource)
    {
        // StringBuilder url = new StringBuilder(getUri().getScheme());
        // url.append("://");
        // url.append(getUri().getHost());
        //
        // if (getUri().getPort() > 0)
        // {
        // url.append(":").append(getUri().getPort());
        // }
        //
        // url.append(getUri().getPath());
        // url.append(mavenRequest.getHttpUri());
        //
        // URI uri = new URI(url.toString());

        String path = baseUri.getPath();

        if (path.endsWith("/") && resource.startsWith("/"))
        {
            path += resource.substring(1);
        }
        else if (path.endsWith("/") && !resource.startsWith("/"))
        {
            path += resource;
        }
        else if (!path.endsWith("/") && resource.startsWith("/"))
        {
            path += resource;
        }

        return baseUri.resolve(path);
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
