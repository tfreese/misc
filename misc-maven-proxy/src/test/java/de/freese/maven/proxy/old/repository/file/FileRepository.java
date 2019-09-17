/**
 * Created: 15.09.2019
 */

package de.freese.maven.proxy.old.repository.file;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import de.freese.maven.proxy.old.model.AbstractMavenHttpObject;
import de.freese.maven.proxy.old.model.MavenRequest;
import de.freese.maven.proxy.old.model.MavenResponse;
import de.freese.maven.proxy.old.repository.AbstractRepository;

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

    @Override
    public void dispose()
    {
        // Empty
    }

    /**
     * @see de.freese.maven.proxy.old.repository.Repository#exist(de.freese.maven.proxy.old.model.MavenRequest)
     */
    @Override
    public MavenResponse exist(final MavenRequest mavenRequest) throws Exception
    {
        Path pathFile = getFilePath(getPathRepository(), mavenRequest.getHttpUri());

        if (!Files.isReadable(pathFile))
        {
            return null;
        }

        MavenResponse mavenResponse = new MavenResponse(mavenRequest.getHttpProtocol(), AbstractMavenHttpObject.HTTP_OK, "OK");
        mavenResponse.setHttpUri(mavenRequest.getHttpUri());

        return mavenResponse;
    }

    /**
     * Liefert das absoluten Verzeichnis der Datei.
     *
     * @param repository {@link Path}
     * @param file String
     * @return {@link Path}
     */
    protected Path getFilePath(final Path repository, final String file)
    {
        Path path = null;

        if (file.startsWith("/"))
        {
            path = repository.resolve(file.substring(1));
        }
        else
        {
            path = repository.resolve(file);
        }

        return path;
    }

    /**
     * @return {@link Path}
     */
    protected Path getPathRepository()
    {
        return this.pathRepository;
    }

    /**
     * @see de.freese.maven.proxy.old.repository.Repository#getResource(de.freese.maven.proxy.old.model.MavenRequest)
     */
    @Override
    public MavenResponse getResource(final MavenRequest mavenRequest) throws Exception
    {
        Path pathFile = getFilePath(getPathRepository(), mavenRequest.getHttpUri());

        if (!Files.isReadable(pathFile))
        {
            return null;
        }

        MavenResponse mavenResponse = new MavenResponse(mavenRequest.getHttpProtocol(), AbstractMavenHttpObject.HTTP_OK, "OK");
        mavenResponse.setHttpUri(mavenRequest.getHttpUri());

        // BasicFileAttributes basicFileAttributes = Files.readAttributes(pathFile, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        // long size = basicFileAttributes.size();
        long size = Files.size(pathFile);

        mavenResponse.setContentLengthValue(Long.toString(size));
        mavenResponse.setResource(Files.readAllBytes(pathFile));

        // Content-Type: application/xml

        return mavenResponse;
    }

    /**
     * @param mavenResponse {@link MavenResponse}
     * @throws IOException Falls was schief geht.
     */
    public void writeFile(final MavenResponse mavenResponse) throws IOException
    {
        if (!mavenResponse.hasResource())
        {
            return;
        }

        Path pathFile = getFilePath(getPathRepository(), mavenResponse.getHttpUri());

        Files.createDirectories(pathFile.getParent());

        Files.write(pathFile, mavenResponse.getResource());
    }
}
