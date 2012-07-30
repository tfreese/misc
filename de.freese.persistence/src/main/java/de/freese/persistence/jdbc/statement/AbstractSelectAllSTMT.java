/*
 * Created on 16.09.2004
 */
package de.freese.persistence.jdbc.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.freese.persistence.exception.PersistenceException;
import de.freese.persistence.jdbc.manager.AbstractPersistenceManager;

/**
 * Basisklasse eines LoeschenStatements.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractSelectAllSTMT extends AbstractPreparedStatement
{
	/**
	 * Erstellt ein neues {@link AbstractSelectAllSTMT} Object.
	 * 
	 * @param persistenceManager {@link AbstractPersistenceManager}
	 */
	public AbstractSelectAllSTMT(final AbstractPersistenceManager<?> persistenceManager)
	{
		super(persistenceManager);
	}

	/**
	 * @return Object
	 * @throws PersistenceException Falls was schief geht.
	 */
	@Override
	public abstract Object execute() throws PersistenceException;

	/**
	 * @see de.freese.persistence.jdbc.statement.AbstractPreparedStatement#createPreparedStatement()
	 */
	@Override
	protected PreparedStatement createPreparedStatement() throws PersistenceException
	{
		PreparedStatement stmt = null;

		try
		{
			stmt =
					getPersistenceManager().getConnection().prepareStatement(
							"select * from " + getPersistenceManager().getTable());
		}
		catch (SQLException e)
		{
			throw new PersistenceException(e);
		}

		return stmt;
	}
}
