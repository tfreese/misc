/**
 * Created: 18.09.2019
 */

package de.freese.maven.proxy.blobstore;

import java.io.InputStream;

/**
 * Referenz für binäre Daten aus einem {@link BlobStore}.<br>
 * {@link "https://github.com/sonatype/nexus-public/blob/master/components/nexus-blobstore-api"}
 *
 * @author Thomas Freese
 */
public interface Blob
{
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

    /**
     * Liefert die Größe/Lönge des Blobs in Byte.
     *
     * @return long
     */
    public long getLength();

    /**
     * Liefert den Namen des Blobs ohne führende Pfadangaben.
     *
     * @return long
     */
    public String getSimpleName();
}
