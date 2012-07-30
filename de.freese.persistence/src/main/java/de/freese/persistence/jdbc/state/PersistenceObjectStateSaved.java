package de.freese.persistence.jdbc.state;

import de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject;

/**
 * Created on 09.01.2004
 * 
 * @author Thomas Freese
 */
public class PersistenceObjectStateSaved extends AbstractPersistenceObjectState
{
	/**
	 * Erstellt ein neues {@link PersistenceObjectStateSaved} Object.
	 */
	public PersistenceObjectStateSaved()
	{
		super();
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#getIntState()
	 */
	@Override
	public int getIntState()
	{
		return SAVED;
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#isSaved()
	 */
	@Override
	public boolean isSaved()
	{
		return true;
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#setStateChanged(de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject)
	 */
	@Override
	public void setStateChanged(final AbstractJDBCPersistenceObject po)
	{
		changeState(po, getStateChanged());
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#setStateComplexChanged(de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject)
	 */
	@Override
	public void setStateComplexChanged(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException
	{
		changeState(po, getStateComplexChanged());
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
		return "SAVED";
	}
}
