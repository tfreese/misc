/**
 * Created: 01.01.2012
 */

package de.freese.maven.proxy.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import de.freese.maven.proxy.model.HTTPHeader;
import de.freese.maven.proxy.model.MavenRequest;
import de.freese.maven.proxy.model.MavenResponse;

/**
 * Vvirtuelles Repository, welches die {@link IRepository} zusammenfasst.
 * 
 * @author Thomas Freese
 */
public class VirtualRepository extends AbstractRepository
{
	/**
	 * 
	 */
	private Map<String, IRepository> repositories = new HashMap<>();

	/**
	 * 
	 */
	private Map<String, IRepository> index = new TreeMap<>();

	/**
	 * Erstellt ein neues {@link VirtualRepository} Object.
	 */
	public VirtualRepository()
	{
		super();
	}

	/**
	 * Hinzufügen eines {@link IRepository}.
	 * 
	 * @param repository {@link IRepository}
	 * @throws IllegalArgumentException falls Name des Repositories schon existiert.
	 */
	public void addRepository(final IRepository repository) throws IllegalArgumentException
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
	 * @see de.freese.maven.proxy.repository.IRepository#dispose()
	 */
	@Override
	public void dispose()
	{
		this.repositories.clear();
		this.index.clear();
	}

	/**
	 * @see de.freese.maven.proxy.repository.IRepository#exist(de.freese.maven.proxy.model.MavenRequest)
	 */
	@Override
	public MavenResponse exist(final MavenRequest mavenRequest) throws Exception
	{
		String context = mavenRequest.getHttpHeader().getContext();
		IRepository repo = this.index.get(context);

		MavenResponse mavenResponse = null;
		MavenResponse mavenResponseLast = null;

		if (repo != null)
		{
			mavenResponse = repo.exist(mavenRequest);
		}
		else
		{
			// Suchen
			for (IRepository repository : this.repositories.values())
			{
				if (!repository.isActive())
				{
					continue;
				}

				try
				{
					mavenResponse = repository.exist(mavenRequest);
				}
				catch (Exception ex)
				{
					// Ignore
				}

				if ((mavenResponse != null)
						&& (mavenResponse.getHttpHeader().getResponseCode() == HTTPHeader.HTTP_OK))
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
	 * @see de.freese.maven.proxy.repository.IRepository#getName()
	 */
	@Override
	public String getName()
	{
		return getClass().getSimpleName();
	}

	/**
	 * @see de.freese.maven.proxy.repository.IRepository#getResource(de.freese.maven.proxy.model.MavenRequest)
	 */
	@Override
	public MavenResponse getResource(final MavenRequest mavenRequest) throws Exception
	{
		String context = mavenRequest.getHttpHeader().getContext();
		IRepository repo = this.index.get(context);

		MavenResponse mavenResponse = null;
		MavenResponse mavenResponseLast = null;

		if (repo != null)
		{
			mavenResponse = repo.getResource(mavenRequest);
		}
		else
		{
			// Suchen
			for (IRepository repository : this.repositories.values())
			{
				if (!repository.isActive())
				{
					continue;
				}

				try
				{
					mavenResponse = repository.getResource(mavenRequest);
				}
				catch (Exception ex)
				{
					// Ignore
				}

				if ((mavenResponse != null) && mavenResponse.hasResource()
						&& (mavenResponse.getHttpHeader().getResponseCode() == HTTPHeader.HTTP_OK))
				{
					repo = repository;
					this.index.put(context, repository);
					break;
				}

				mavenResponseLast = mavenResponse;
				mavenResponse = null;
			}
		}

		if ((mavenResponse != null) && mavenResponse.hasResource()
				&& (mavenResponse.getHttpHeader().getResponseCode() == HTTPHeader.HTTP_OK))
		{
			getLogger().info("downloaded {}{}", repo, mavenRequest.getHttpHeader().getContext());
		}

		return mavenResponse == null ? mavenResponseLast : mavenResponse;
	}

	/**
	 * @see de.freese.maven.proxy.repository.IRepository#init()
	 */
	@Override
	public void init()
	{
		if (this.repositories.isEmpty())
		{
			throw new IllegalStateException("Repository is empty");
		}
	}

	/**
	 * @see de.freese.maven.proxy.repository.AbstractRepository#isActive()
	 */
	@Override
	public boolean isActive()
	{
		return true;
	}

	/**
	 * @see de.freese.maven.proxy.repository.AbstractRepository#setActive(boolean)
	 */
	@Override
	public void setActive(final boolean value)
	{
		getLogger().warn("not supported");
	}
}
