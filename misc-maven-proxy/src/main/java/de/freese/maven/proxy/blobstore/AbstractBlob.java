/**
 * Created: 18.09.2019
 */

package de.freese.maven.proxy.blobstore;

import java.io.InputStream;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basis-Implementierung eines {@link Blob}s.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBlob implements Blob
{
    /**
     *
     */
    private final BlobId id;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractBlob} Object.
     *
     * @param id {@link BlobId}
     */
    public AbstractBlob(final BlobId id)
    {
        super();

        this.id = Objects.requireNonNull(id, "id required");
    }

    /**
     * @return {@link InputStream}
     * @throws Exception Falls was schief geht.
     */
    protected abstract InputStream doGetInputStream() throws Exception;

    /**
     * @return long
     * @throws Exception Falls was schief geht.
     */
    protected abstract long doGetLength() throws Exception;

    /**
     * @return String
     * @throws Exception Falls was schief geht.
     */
    protected abstract String doGetSimpleName() throws Exception;

    /**
     * @see de.freese.maven.proxy.blobstore.Blob#getId()
     */
    @Override
    public final BlobId getId()
    {
        return this.id;
    }

    /**
     * @see de.freese.maven.proxy.blobstore.Blob#getInputStream()
     */
    @Override
    public InputStream getInputStream()
    {
        try
        {
            return doGetInputStream();
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        return null;
    }

    /**
     * @see de.freese.maven.proxy.blobstore.Blob#getLength()
     */
    @Override
    public long getLength()
    {
        try
        {
            return doGetLength();
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        return -1L;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.maven.proxy.blobstore.Blob#getSimpleName()
     */
    @Override
    public String getSimpleName()
    {
        try
        {
            return doGetSimpleName();
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        return null;
    }
}
