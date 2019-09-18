/**
 * Created: 18.09.2019
 */

package de.freese.maven.proxy.blobstore;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Referenz für binäre Daten aus einem {@link BlobStore}.<br>
 * {@link "https://github.com/sonatype/nexus-public/blob/master/components/nexus-blobstore-api"}
 *
 * @author Thomas Freese
 */
public interface Blob
{
    /**
     * @return {@link Path}
     */
    public Path createTempFile();

    /**
     * @return {@link BlobId}
     */
    public BlobId getId();

    /**
     * @return {@link BlobId}
     */
    public default String getIdAsUniqueString()
    {
        return getId().asUniqueString();
    }

    /**
     * @return {@link InputStream}
     */
    public InputStream getInputStream();
}
