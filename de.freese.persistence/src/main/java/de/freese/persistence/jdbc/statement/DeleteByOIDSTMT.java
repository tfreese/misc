/*
 * Created on 16.09.2004
 */
package de.freese.persistence.jdbc.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.freese.persistence.exception.PersistenceException;
import de.freese.persistence.jdbc.manager.AbstractPersistenceManager;
import de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject;

/**
 * Loescht ein Objekte ueber den PromaryKey.
 * 
 * @author Thomas Freese
 */
public class DeleteByOIDSTMT extends AbstractPreparedStatement
{
	/**
	 * Erstellt ein neues {@link DeleteByOIDSTMT} Object.
	 * 
	 * @param persistenceManager {@link AbstractPersistenceManager}
	 */
	public DeleteByOIDSTMT(final AbstractPersistenceManager<?> persistenceManager)
	{
		super(persistenceManager);
	}

	/**
	 * @see de.freese.persistence.jdbc.statement.AbstractStatement#execute(java.lang.Object)
	 */
	@Override
	public Object execute(final Object anObject) throws PersistenceException
	{
		try
		{
			AbstractJDBCPersistenceObject po = (AbstractJDBCPersistenceObject) anObject;

			getPreparedStatement().setLong(1, po.getObjectID());

			getPreparedStatement().executeUpdate();

			getPreparedStatement().clearParameters();

			po.getCurrentState().setStateDeleted(po);
		}
		catch (SQLException ex)
		{
			throw new PersistenceException(ex);
		}

		return anObject;
	}

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
							"delete from " + getPersistenceManager().getTable() + " where "
									+ AbstractJDBCPersistenceObject.FIELD_OBJECTID + " = ?");
		}
		catch (SQLException ex)
		{
			throw new PersistenceException(ex);
		}

		return stmt;
	}
}
