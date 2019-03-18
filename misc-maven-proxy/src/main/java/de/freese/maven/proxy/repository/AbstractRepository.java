/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Objects;

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
     *
     */
    private boolean active = true;
    /**
     *
     */
    private CharsetDecoder charsetDecoder = null;
    /**
     *
     */
    private CharsetEncoder charsetEncoder = null;

    /**
     * Erstellt ein neues {@link AbstractRepository} Object.<br>
     * Default-Charset: ISO-8859-1
     *
     * @param uri {@link URI}; Ressourenquelle des Repositories
     */
    public AbstractRepository(final URI uri)
    {
        this(uri, Charset.forName("ISO-8859-1"));
    }

    /**
     * Erstellt ein neues {@link AbstractRepository} Object.
     *
     * @param uri     {@link URI}; Ressourenquelle des Repositories
     * @param charset {@link Charset}; Kodierung
     */
    public AbstractRepository(final URI uri, final Charset charset)
    {
        super();

        this.uri = Objects.requireNonNull(uri, "repository required");

        Objects.requireNonNull(charset, "charset required");
        this.charsetEncoder = charset.newEncoder();
        this.charsetDecoder = charset.newDecoder();
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#getName()
     */
    @Override
    public String getName()
    {
        return getUri().toString();
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#isActive()
     */
    @Override
    public boolean isActive()
    {
        return this.active;
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#setActive(boolean)
     */
    @Override
    public void setActive(final boolean value)
    {
        this.active = value;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getName();
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
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link URI}
     */
    protected URI getUri()
    {
        return this.uri;
    }
}
