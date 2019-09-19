/**
 * Created: 18.09.2019
 */

package de.freese.maven.proxy.blobstore.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import de.freese.maven.proxy.blobstore.AbstractBlob;
import de.freese.maven.proxy.blobstore.AbstractBlobStore;
import de.freese.maven.proxy.blobstore.Blob;
import de.freese.maven.proxy.blobstore.BlobId;
import de.freese.maven.proxy.blobstore.BlobStore;

/**
 * {@link BlobStore} Implementierung für eine Datei.
 *
 * @author Thomas Freese
 */
public class FileBlobStore extends AbstractBlobStore
{
    /**
     * {@link Blob} Implementierung für eine Datei.
     *
     * @author Thomas Freese
     */
    public class FileBlob extends AbstractBlob
    {
        /**
         *
         */
        private final Path absolutePath;

        /**
         * Erstellt ein neues {@link FileBlob} Object.
         *
         * @param id {@link BlobId}
         */
        private FileBlob(final BlobId id)
        {
            super(id);

            this.absolutePath = toContentPath(id);
        }

        /**
         * @see de.freese.maven.proxy.blobstore.AbstractBlob#doCreateTempFile()
         */
        @Override
        protected Path doCreateTempFile() throws Exception
        {
            // Einfach den absoluten Pfad liefern.
            return this.absolutePath;
        }

        /**
         * @see de.freese.maven.proxy.blobstore.AbstractBlob#doGetInputStream()
         */
        @Override
        protected InputStream doGetInputStream() throws Exception
        {
            return Files.newInputStream(this.absolutePath);
        }

        /**
         * @see de.freese.maven.proxy.blobstore.AbstractBlob#doGetLength()
         */
        @Override
        protected long doGetLength() throws Exception
        {
            long length = Files.size(this.absolutePath);

            return length;
        }

        /**
         * @see de.freese.maven.proxy.blobstore.AbstractBlob#doGetName()
         */
        @Override
        protected String doGetName() throws Exception
        {
            return this.absolutePath.getFileName().toString();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return this.absolutePath.toString();
        }
    }

    /**
    *
    */
    private final Path basePath;

    /**
     * Erstellt ein neues {@link FileBlobStore} Object.
     *
     * @param basePath {@link Path}
     * @throws IOException Falls was schief geht.
     */
    public FileBlobStore(final Path basePath) throws IOException
    {
        super();

        this.basePath = Objects.requireNonNull(basePath, "basePath required");

        // if (!Files.isWritable(this.basePath))
        // {
        // String msg = "Path not writeable: " + uri;
        //
        // getLogger().error(msg);
        // throw new IllegalArgumentException(msg);
        // }

        if (Files.notExists(this.basePath))
        {
            Files.createDirectories(this.basePath);
        }
    }

    /**
     * @see de.freese.maven.proxy.blobstore.AbstractBlobStore#doCreate(de.freese.maven.proxy.blobstore.BlobId, java.io.InputStream)
     */
    @Override
    protected Blob doCreate(final BlobId id, final InputStream inputStream) throws Exception
    {
        Path path = toContentPath(id);

        Files.createDirectories(path.getParent());

        Files.copy(inputStream, path);

        return new FileBlob(id);
    }

    /**
     * @see de.freese.maven.proxy.blobstore.AbstractBlobStore#doDelete(de.freese.maven.proxy.blobstore.BlobId)
     */
    @Override
    protected void doDelete(final BlobId id) throws Exception
    {
        Path path = toContentPath(id);

        Files.delete(path);
    }

    /**
     * @see de.freese.maven.proxy.blobstore.AbstractBlobStore#doExists(de.freese.maven.proxy.blobstore.BlobId)
     */
    @Override
    protected boolean doExists(final BlobId id) throws Exception
    {
        Path path = toContentPath(id);

        return Files.exists(path);
    }

    /**
     * @see de.freese.maven.proxy.blobstore.AbstractBlobStore#doGet(de.freese.maven.proxy.blobstore.BlobId)
     */
    @Override
    protected Blob doGet(final BlobId id) throws Exception
    {
        return new FileBlob(id);
    }

    /**
     * @param id {@link BlobId}
     * @return {@link Path}
     */
    private Path toContentPath(final BlobId id)
    {
        Path path = null;
        String key = id.asUniqueString();

        if (key.startsWith("/"))
        {
            path = this.basePath.resolve(key.substring(1));
        }
        else
        {
            path = this.basePath.resolve(key);
        }

        return path;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.basePath.toString();
    }
}
