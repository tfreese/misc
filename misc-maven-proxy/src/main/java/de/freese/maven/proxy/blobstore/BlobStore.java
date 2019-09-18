/**
 * Created: 18.09.2019
 */

package de.freese.maven.proxy.blobstore;

import java.io.InputStream;

/**
 * Interface eines BlobStores.<br>
 * {@link "https://github.com/sonatype/nexus-public/blob/master/components/nexus-blobstore-api"}
 *
 * @author Thomas Freese
 */
public interface BlobStore
{
    /**
     * @param id {@link BlobId}
     * @param inputStream {@link InputStream}
     * @return {@link Blob}
     */
    public Blob create(BlobId id, InputStream inputStream);

    /**
     * @param id {@link BlobId}
     */
    public void delete(BlobId id);

    /**
     * @param id {@link BlobId}
     * @return boolean
     */
    public boolean exists(BlobId id);

    /**
     * @param id {@link BlobId}
     * @return {@link Blob}
     */
    public Blob get(BlobId id);
}
