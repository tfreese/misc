/**
 * Created: 18.09.2019
 */

package de.freese.maven.proxy.blobstore;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basis-Implementierung eines {@link BlobStore}s.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBlobStore implements BlobStore
{
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractBlobStore} Object.
     */
    public AbstractBlobStore()
    {
        super();
    }

    /**
     * @see de.freese.maven.proxy.blobstore.BlobStore#create(de.freese.maven.proxy.blobstore.BlobId, java.io.InputStream)
     */
    @Override
    public Blob create(final BlobId id, final InputStream inputStream)
    {
        try
        {
            return doCreate(id, inputStream);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        return null;
    }

    /**
     * @see de.freese.maven.proxy.blobstore.BlobStore#delete(de.freese.maven.proxy.blobstore.BlobId)
     */
    @Override
    public void delete(final BlobId id)
    {
        try
        {
            doDelete(id);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * @param id {@link BlobId}
     * @param inputStream {@link InputStream}
     * @return {@link Blob}
     * @throws Exception Falls was schief geht.
     */
    protected abstract Blob doCreate(final BlobId id, final InputStream inputStream) throws Exception;

    /**
     * @param id {@link BlobId}
     * @throws Exception Falls was schief geht.
     */
    protected abstract void doDelete(final BlobId id) throws Exception;

    /**
     * @param id {@link BlobId}
     * @return boolean
     * @throws Exception Falls was schief geht.
     */
    protected abstract boolean doExists(final BlobId id) throws Exception;

    /**
     * @param id {@link BlobId}
     * @return {@link Blob}
     * @throws Exception Falls was schief geht.
     */
    protected abstract Blob doGet(final BlobId id) throws Exception;

    /**
     * @see de.freese.maven.proxy.blobstore.BlobStore#exists(de.freese.maven.proxy.blobstore.BlobId)
     */
    @Override
    public boolean exists(final BlobId id)
    {
        try
        {
            return doExists(id);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        return false;
    }

    /**
     * @see de.freese.maven.proxy.blobstore.BlobStore#get(de.freese.maven.proxy.blobstore.BlobId)
     */
    @Override
    public Blob get(final BlobId id)
    {
        try
        {
            return doGet(id);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        return null;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
