/*
 * Created on 07.09.2004
 */
package de.freese.persistence.jdbc.statement;

import java.sql.PreparedStatement;

import de.freese.persistence.exception.PersistenceException;
import de.freese.persistence.jdbc.manager.AbstractPersistenceManager;

/**
 * Basisklasse eines PreparedStatements.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractPreparedStatement extends AbstractStatement
{
	/**
	 * 
	 */
	protected PreparedStatement preparedStatement = null;

	/**
	 * Erstellt ein neues {@link AbstractPreparedStatement} Object.
	 * 
	 * @param persistenceManager {@link AbstractPersistenceManager}
	 */
	public AbstractPreparedStatement(final AbstractPersistenceManager<?> persistenceManager)
	{
		super(persistenceManager);
	}

	/**
	 * @see de.freese.persistence.jdbc.statement.AbstractStatement#close()
	 */
	@Override
	public void close() throws PersistenceException
	{
		try
		{
			getPreparedStatement().close();
		}
		catch (Throwable th)
		{
			throw new PersistenceException(th);
		}
	}

	/**
	 * @see de.freese.persistence.jdbc.statement.AbstractStatement#execute(java.lang.Object)
	 */
	@Override
	public Object execute(final Object anObject) throws PersistenceException
	{
		throw new PersistenceException("Not implemented !");
	}

	/**
	 * @see de.freese.persistence.jdbc.statement.AbstractStatement#execute()
	 */
	@Override
	public Object execute() throws PersistenceException
	{
		throw new PersistenceException("Not implemented !");
	}

	/**
	 * @return {@link PreparedStatement}
	 * @throws PersistenceException Falls was schief geht.
	 */
	protected abstract PreparedStatement createPreparedStatement() throws PersistenceException;

	/**
	 * @return {@link PreparedStatement}
	 * @throws PersistenceException Falls was schief geht.
	 */
	protected PreparedStatement getPreparedStatement() throws PersistenceException
	{
		if (this.preparedStatement == null)
		{
			this.preparedStatement = createPreparedStatement();
		}

		return this.preparedStatement;
	}
}
