/**
 * Created: 18.09.2019
 */

package de.freese.maven.proxy.repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper für mehrere Remote-{@link RemoteRepository}.
 *
 * @author Thomas Freese
 */
public class RemoteRepositories extends AbstractRemoteRepository
{
    /**
     *
     */
    private final List<RemoteRepository> remoteRepositories = new ArrayList<>();

    /**
     * Erstellt ein neues {@link RemoteRepositories} Object.
     */
    public RemoteRepositories()
    {
        super();
    }

    /**
     * @param remoteRepository {@link RemoteRepository}
     */
    public void addRepository(final RemoteRepository remoteRepository)
    {
        this.remoteRepositories.add(remoteRepository);
    }

    /**
     * @see de.freese.maven.proxy.repository.RemoteRepository#exist(java.lang.String)
     */
    @Override
    public boolean exist(final String resource) throws Exception
    {
        boolean exist = false;

        for (RemoteRepository remoteRepository : this.remoteRepositories)
        {
            try
            {
                exist = remoteRepository.exist(resource);
            }
            catch (Exception ex)
            {
                getLogger().warn("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
            }

            if (exist)
            {
                break;
            }
        }

        return exist;
    }

    /**
     * @see de.freese.maven.proxy.repository.RemoteRepository#getInputStream(java.lang.String)
     */
    @Override
    public InputStream getInputStream(final String resource) throws Exception
    {
        InputStream inputStream = null;

        for (RemoteRepository remoteRepository : this.remoteRepositories)
        {
            try
            {
                inputStream = remoteRepository.getInputStream(resource);
            }
            catch (Exception ex)
            {
                getLogger().warn("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
            }

            if (inputStream != null)
            {
                break;
            }
        }

        return inputStream;
    }

    /**
     * @see de.freese.maven.proxy.repository.AbstractRemoteRepository#toString()
     */
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
}
