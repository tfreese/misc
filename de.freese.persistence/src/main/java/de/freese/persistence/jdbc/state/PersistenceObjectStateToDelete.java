package de.freese.persistence.jdbc.state;

import de.freese.persistence.exception.PersistenceException;
import de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject;

/**
 * Created on 03.03.2004
 * 
 * @author Thomas Freese
 */
public class PersistenceObjectStateToDelete extends AbstractPersistenceObjectState
{
	/**
	 * Erstellt ein neues {@link PersistenceObjectStateToDelete} Object.
	 */
	public PersistenceObjectStateToDelete()
	{
		super();
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#delete(de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject)
	 */
	@Override
	public void delete(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException, PersistenceException
	{
		po.getPersistenceManager().delete(po);
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#getIntState()
	 */
	@Override
	public int getIntState()
	{
		return TODELETE;
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#isToDelete()
	 */
	@Override
	public boolean isToDelete()
	{
		return true;
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#setStateDeleted(de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject)
	 */
	@Override
	public void setStateDeleted(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException
	{
		changeState(po, getStateDeleted());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "TODELETE";
	}
}
