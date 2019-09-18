/**
 * Created: 18.09.2019
 */

package de.freese.maven.proxy.blobstore;

import java.io.Serializable;
import java.util.Objects;

/**
 * Eindeutige ID für einen Blob in einem {@link BlobStore}.<br>
 * {@link "https://github.com/sonatype/nexus-public/blob/master/components/nexus-blobstore-api"}
 *
 * @author Thomas Freese
 */
public class BlobId implements Serializable, Comparable<BlobId>
{
    /**
     *
     */
    private static final long serialVersionUID = -5581749917166864024L;

    /**
     *
     */
    private final String id;

    /**
     * Erstellt ein neues {@link BlobId} Object.
     *
     * @param id String
     */
    public BlobId(final String id)
    {
        super();

        this.id = Objects.requireNonNull(id, "id required");
    }

    /**
     * @return String
     */
    public String asUniqueString()
    {
        return this.id;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final BlobId o)
    {
        return this.id.compareTo(o.id);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }

        BlobId blobId = (BlobId) o;

        return this.id.equals(blobId.id);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return this.id.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.id;
    }
}
