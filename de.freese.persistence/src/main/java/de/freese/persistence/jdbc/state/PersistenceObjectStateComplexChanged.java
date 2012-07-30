package de.freese.persistence.jdbc.state;

import de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject;

/**
 * Created on 13.03.2004
 * 
 * @author Thomas Freese
 */
public class PersistenceObjectStateComplexChanged extends PersistenceObjectStateSaved
{
	/**
	 * Erstellt ein neues {@link PersistenceObjectStateComplexChanged} Object.
	 */
	public PersistenceObjectStateComplexChanged()
	{
		super();
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#getIntState()
	 */
	@Override
	public int getIntState()
	{
		return COMPLEX_CHANGED;
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#isComplexChanged()
	 */
	@Override
	public boolean isComplexChanged()
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
		return "COMPLEX_CHANGED";
	}
}
