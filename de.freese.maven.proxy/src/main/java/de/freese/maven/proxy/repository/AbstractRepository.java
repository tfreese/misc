/**
 * Created: 28.12.2011
 */

package de.freese.maven.proxy.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisimplementierung eines Repositories.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractRepository implements IRepository
{
	/**
	 * 
	 */
	private boolean active = true;

	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Erstellt ein neues {@link AbstractRepository} Object.
	 */
	public AbstractRepository()
	{
		super();
	}

	/**
	 * @return {@link Logger}
	 */
	protected Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * @see de.freese.maven.proxy.repository.IRemoteRepository#isActive()
	 */
	@Override
	public boolean isActive()
	{
		return this.active;
	}

	/**
	 * @see de.freese.maven.proxy.repository.IRemoteRepository#setActive(boolean)
	 */
	@Override
	public void setActive(final boolean value)
	{
		this.active = value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getName();
	}
}
