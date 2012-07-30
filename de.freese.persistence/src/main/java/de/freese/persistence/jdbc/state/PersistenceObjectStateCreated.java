package de.freese.persistence.jdbc.state;

import de.freese.persistence.exception.PersistenceException;
import de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject;

/**
 * Created on 09.01.2004
 * 
 * @author Thomas Freese
 */
public class PersistenceObjectStateCreated extends AbstractPersistenceObjectState
{
	/**
	 * Erstellt ein neues {@link PersistenceObjectStateCreated} Object.
	 */
	public PersistenceObjectStateCreated()
	{
		super();
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#getIntState()
	 */
	@Override
	public int getIntState()
	{
		return CREATED;
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#insert(de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject)
	 */
	@Override
	public void insert(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException, PersistenceException
	{
		po.getPersistenceManager().insert(po);
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#isCreated()
	 */
	@Override
	public boolean isCreated()
	{
		return true;
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#setStateSaved(de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject)
	 */
	@Override
	public void setStateSaved(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException
	{
		changeState(po, getStateSaved());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "CREATED";
	}
}
