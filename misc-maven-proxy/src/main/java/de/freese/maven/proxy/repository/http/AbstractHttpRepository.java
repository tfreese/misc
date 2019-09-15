/**
 * Created: 15.09.2019
 */

package de.freese.maven.proxy.repository.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Objects;
import de.freese.maven.proxy.model.MavenRequest;
import de.freese.maven.proxy.model.MavenResponse;
import de.freese.maven.proxy.repository.AbstractRepository;

/**
 * Basisimplementierung eines HTTP-Repositories.
 *
 * @author Thomas Freese
 */
public abstract class AbstractHttpRepository extends AbstractRepository
{
    /**
     * (0x0D, 0x0A), (13,10), (\r\n)
     */
    protected static final String CRLF = "\r\n";

    /**
     *
     */
    private final Charset charset;

    /**
    *
    */
    private final CharsetDecoder charsetDecoder;

    /**
    *
    */
    private final CharsetEncoder charsetEncoder;

    /**
     * Erstellt ein neues {@link AbstractHttpRepository} Object.<br>
     * Default-Charset: ISO-8859-1
     *
     * @param uri String; Quelle des Repositories
     * @throws URISyntaxException Falls was schief geht.
     */
    public AbstractHttpRepository(final String uri) throws URISyntaxException
    {
        this(new URI(uri));
    }

    /**
     * Erstellt ein neues {@link AbstractHttpRepository} Object.<br>
     * Default-Charset: ISO-8859-1
     *
     * @param uri {@link URI}; Quelle des Repositories
     */
    public AbstractHttpRepository(final URI uri)
    {
        this(uri, Charset.forName("ISO-8859-1"));
    }

    /**
     * Erstellt ein neues {@link AbstractHttpRepository} Object.
     *
     * @param uri {@link URI}; Quelle des Repositories
     * @param charset {@link Charset}; Kodierung
     */
    public AbstractHttpRepository(final URI uri, final Charset charset)
    {
        super(uri);

        String scheme = uri.getScheme();

        if ((scheme == null) || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")))
        {
            String msg = "HTTP or HTTPS protocol required: " + scheme;

            getLogger().error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.charset = Objects.requireNonNull(charset, "charset required");
        this.charsetEncoder = charset.newEncoder();
        this.charsetDecoder = charset.newDecoder();
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#dispose()
     */
    @Override
    public void dispose()
    {
        // Empty
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#exist(de.freese.maven.proxy.model.MavenRequest)
     */
    @Override
    public MavenResponse exist(final MavenRequest mavenRequest) throws Exception
    {
        // HTTP Request bauen.
        // mavenRequest.setConnectionValue("close");
        mavenRequest.setHostValue(getUri().getHost());

        MavenResponse mavenResponse = existImpl(mavenRequest);

        if (mavenResponse != null)
        {
            mavenResponse.setHttpUri(mavenRequest.getHttpUri());
        }

        return mavenResponse;
    }

    /**
     * @param mavenRequest MavenRequest
     * @return {@link MavenResponse}
     * @throws Exception Falls was schief geht.
     */
    protected abstract MavenResponse existImpl(MavenRequest mavenRequest) throws Exception;

    /**
     * @return {@link Charset}
     */
    protected Charset getCharset()
    {
        return this.charset;
    }

    /**
     * @return {@link CharsetDecoder}
     */
    protected CharsetDecoder getCharsetDecoder()
    {
        return this.charsetDecoder;
    }

    /**
     * @return {@link CharsetEncoder}
     */
    protected CharsetEncoder getCharsetEncoder()
    {
        return this.charsetEncoder;
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#getResource(de.freese.maven.proxy.model.MavenRequest)
     */
    @Override
    public MavenResponse getResource(final MavenRequest mavenRequest) throws Exception
    {
        // HTTP Request bauen.
        mavenRequest.setConnectionValue("close");
        mavenRequest.setHostValue(getUri().getHost());

        MavenResponse mavenResponse = getResourceImpl(mavenRequest);

        if (mavenResponse != null)
        {
            mavenResponse.setHttpUri(mavenRequest.getHttpUri());
        }

        return mavenResponse;
    }

    /**
     * @param mavenRequest MavenRequest
     * @return {@link MavenResponse}
     * @throws Exception Falls was schief geht.
     */
    protected abstract MavenResponse getResourceImpl(MavenRequest mavenRequest) throws Exception;
}
