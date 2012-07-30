/*
 * Created on 07.09.2004
 */
package de.freese.persistence.jdbc.statement;

import de.freese.persistence.exception.PersistenceException;
import de.freese.persistence.jdbc.manager.AbstractPersistenceManager;

/**
 * Basisklasse eines Statements.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractStatement
{
	/**
	 *
	 */
	private AbstractPersistenceManager<?> persistenceManager = null;

	/**
	 * Erstellt ein neues {@link AbstractStatement} Object.
	 * 
	 * @param persistenceManager {@link AbstractPersistenceManager}
	 */
	public AbstractStatement(final AbstractPersistenceManager<?> persistenceManager)
	{
		super();

		setPersistenceManager(persistenceManager);
	}

	/**
	 * @throws PersistenceException Falls was schief geht.
	 */
	public abstract void close() throws PersistenceException;

	/**
	 * @return Object
	 * @throws PersistenceException Falls was schief geht.
	 */
	public abstract Object execute() throws PersistenceException;

	/**
	 * @param anObject Object
	 * @return Object
	 * @throws PersistenceException Falls was schief geht.
	 */
	public abstract Object execute(Object anObject) throws PersistenceException;

	/**
	 * @param persistenceManager {@link AbstractPersistenceManager}
	 */
	protected void setPersistenceManager(final AbstractPersistenceManager<?> persistenceManager)
	{
		this.persistenceManager = persistenceManager;
	}

	/**
	 * @return {@link AbstractPersistenceManager}
	 */
	protected AbstractPersistenceManager<?> getPersistenceManager()
	{
		return this.persistenceManager;
	}
}
