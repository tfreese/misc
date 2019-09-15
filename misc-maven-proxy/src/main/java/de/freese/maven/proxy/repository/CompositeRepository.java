/**
 * Created: 01.01.2012
 */

package de.freese.maven.proxy.repository;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import de.freese.maven.proxy.model.AbstractMavenHttpObject;
import de.freese.maven.proxy.model.MavenRequest;
import de.freese.maven.proxy.model.MavenResponse;

/**
 * Virtuelles Repository, welches die {@link Repository} zusammenfasst.
 *
 * @author Thomas Freese
 */
public class CompositeRepository extends AbstractRepository
{
    /**
     *
     */
    private Map<String, Repository> index = new TreeMap<>();

    /**
     *
     */
    private Map<String, Repository> repositories = new HashMap<>();

    /**
     * Erstellt ein neues {@link CompositeRepository} Object.
     */
    public CompositeRepository()
    {
        super(URI.create(""));
    }

    /**
     * Hinzufügen eines {@link Repository}.
     *
     * @param repository {@link Repository}
     * @throws IllegalArgumentException falls Name des Repositories schon existiert.
     */
    public void addRepository(final Repository repository) throws IllegalArgumentException
    {
        if (repository == null)
        {
            throw new IllegalArgumentException("repository is null");
        }

        if (this.repositories.containsKey(repository.getName()))
        {
            String message = String.format("repository %s already exist", repository.getName());
            throw new IllegalArgumentException(message);
        }

        this.repositories.put(repository.getName(), repository);
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#dispose()
     */
    @Override
    public void dispose()
    {
        this.repositories.clear();
        this.index.clear();
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#exist(de.freese.maven.proxy.model.MavenRequest)
     */
    @Override
    public MavenResponse exist(final MavenRequest mavenRequest) throws Exception
    {
        String context = mavenRequest.getHttpUri();
        Repository repo = this.index.get(context);

        MavenResponse mavenResponse = null;
        MavenResponse mavenResponseLast = null;

        if (repo != null)
        {
            mavenResponse = repo.exist(mavenRequest);
        }
        else
        {
            // Suchen
            for (Repository repository : this.repositories.values())
            {
                try
                {
                    mavenResponse = repository.exist(mavenRequest);
                }
                catch (Exception ex)
                {
                    getLogger().warn(ex.getClass().getSimpleName() + ": " + ex.getMessage());
                }

                if ((mavenResponse != null) && (mavenResponse.getHttpCode() == AbstractMavenHttpObject.HTTP_OK))
                {
                    this.index.put(context, repository);
                    break;
                }

                mavenResponseLast = mavenResponse;
                mavenResponse = null;
            }
        }

        return mavenResponse == null ? mavenResponseLast : mavenResponse;
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#getName()
     */
    @Override
    public String getName()
    {
        return getClass().getSimpleName();
    }

    /**
     * @see de.freese.maven.proxy.repository.Repository#getResource(de.freese.maven.proxy.model.MavenRequest)
     */
    @Override
    public MavenResponse getResource(final MavenRequest mavenRequest) throws Exception
    {
        String context = mavenRequest.getHttpUri();
        Repository repo = this.index.get(context);

        MavenResponse mavenResponse = null;
        MavenResponse mavenResponseLast = null;

        if (repo != null)
        {
            mavenResponse = repo.getResource(mavenRequest);
        }
        else
        {
            // Suchen
            for (Repository repository : this.repositories.values())
            {
                try
                {
                    mavenResponse = repository.getResource(mavenRequest);
                }
                catch (Exception ex)
                {
                    getLogger().warn(ex.getClass().getSimpleName() + ": " + ex.getMessage());
                }

                if ((mavenResponse != null) && mavenResponse.hasResource() && (mavenResponse.getHttpCode() == AbstractMavenHttpObject.HTTP_OK))
                {
                    repo = repository;
                    this.index.put(context, repository);
                    break;
                }

                mavenResponseLast = mavenResponse;
                mavenResponse = null;
            }
        }

        if ((mavenResponse != null) && mavenResponse.hasResource() && (mavenResponse.getHttpCode() == AbstractMavenHttpObject.HTTP_OK))
        {
            getLogger().info("downloaded {}{}", repo, mavenRequest.getHttpUri());
        }

        return mavenResponse == null ? mavenResponseLast : mavenResponse;
    }
}
