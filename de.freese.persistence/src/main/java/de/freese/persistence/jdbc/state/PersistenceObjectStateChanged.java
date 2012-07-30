package de.freese.persistence.jdbc.state;

import de.freese.persistence.exception.PersistenceException;
import de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject;

/**
 * Created on 09.01.2004
 * 
 * @author Thomas Freese
 */
public class PersistenceObjectStateChanged extends AbstractPersistenceObjectState
{
	/**
	 * Erstellt ein neues {@link PersistenceObjectStateChanged} Object.
	 */
	public PersistenceObjectStateChanged()
	{
		super();
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#getIntState()
	 */
	@Override
	public int getIntState()
	{
		return CHANGED;
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#isChanged()
	 */
	@Override
	public boolean isChanged()
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
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#setStateToDelete(de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject)
	 */
	@Override
	public void setStateToDelete(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException
	{
		changeState(po, getStateToDelete());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "CHANGED";
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#update(de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject)
	 */
	@Override
	public void update(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException, PersistenceException
	{
		po.getPersistenceManager().update(po);
	}
}
