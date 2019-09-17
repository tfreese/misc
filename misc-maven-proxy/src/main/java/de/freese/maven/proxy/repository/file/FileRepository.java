/**
 * Created: 15.09.2019
 */

package de.freese.maven.proxy.repository.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import de.freese.maven.proxy.repository.AbstractRepository;

/**
 * Implementierung eines File-Repositories.
 *
 * @author Thomas Freese
 */
public class FileRepository extends AbstractRepository
{
    /**
     *
     */
    private final Path pathRepository;

    /**
     * Erstellt ein neues {@link FileRepository} Object.
     *
     * @param uri {@link URI}
     * @throws IOException Falls was schief geht.
     */
    public FileRepository(final URI uri) throws IOException
    {
        super(uri);

        String scheme = uri.getScheme();

        if ((scheme == null) || !scheme.equalsIgnoreCase("file"))
        {
            String msg = "Only file protocol supported: " + scheme;

            getLogger().error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.pathRepository = Paths.get(uri);

        // if (!Files.isWritable(this.pathRepository))
        // {
        // String msg = "Path not writeable: " + uri;
        //
        // getLogger().error(msg);
        // throw new IllegalArgumentException(msg);
        // }

        if (Files.notExists(this.pathRepository))
        {
            Files.createDirectories(this.pathRepository);
        }
    }

    /**
     * Liefert das absoluten Verzeichnis der Resource.
     *
     * @param repository {@link Path}
     * @param resource String
     * @return {@link Path}
     */
    protected Path createResourcePath(final Path repository, final String resource)
    {
        Path path = null;

        if (resource.startsWith("/"))
        {
            path = repository.resolve(resource.substring(1));
        }
        else
        {
            path = repository.resolve(resource);
        }

        return path;
    }

    /**
     * Liefert das absoluten Verzeichnis der Resource.
     *
     * @param resource String
     * @return {@link Path}
     * @throws Exception Falls was schief geht.
     */
    public Path createResourcePath(final String resource) throws Exception
    {
        return createResourcePath(getPathRepository(), resource);
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
     * @see de.freese.maven.proxy.repository.Repository#exist(java.lang.String)
     */
    @Override
    public boolean exist(final String resource) throws Exception
    {
        Path pathFile = createResourcePath(getPathRepository(), resource);

        return Files.isReadable(pathFile);
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#getInputStream(java.lang.String)
     */
    @Override
    public InputStream getInputStream(final String resource) throws Exception
    {
        Path pathFile = createResourcePath(getPathRepository(), resource);

        if (!Files.isReadable(pathFile))
        {
            return null;
        }

        return Files.newInputStream(pathFile);

        // // BasicFileAttributes basicFileAttributes = Files.readAttributes(pathFile, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        // // long size = basicFileAttributes.size();
        // long size = Files.size(pathFile);
        //
        // mavenResponse.setContentLengthValue(Long.toString(size));
        // mavenResponse.setResource(Files.readAllBytes(pathFile));
        //
        // // Content-Type: application/xml
    }

    /**
     * Schreibt die Resource in den {@link OutputStream}.
     *
     * @param resource String
     * @return {@link OutputStream}
     * @throws IOException Falls was schief geht.
     */
    public OutputStream getOutputStream(final String resource) throws IOException
    {
        Path pathFile = createResourcePath(getPathRepository(), resource);

        Files.createDirectories(pathFile.getParent());

        return Files.newOutputStream(pathFile);
    }

    /**
     * @return {@link Path}
     */
    protected Path getPathRepository()
    {
        return this.pathRepository;
    }
}
