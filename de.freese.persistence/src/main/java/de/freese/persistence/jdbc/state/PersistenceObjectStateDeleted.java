package de.freese.persistence.jdbc.state;

/**
 * Created on 10.01.2004
 * 
 * @author Thomas Freese
 */
public class PersistenceObjectStateDeleted extends AbstractPersistenceObjectState
{
	/**
	 * Erstellt ein neues {@link PersistenceObjectStateDeleted} Object.
	 */
	public PersistenceObjectStateDeleted()
	{
		super();
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#getIntState()
	 */
	@Override
	public int getIntState()
	{
		return DELETED;
	}

	/**
	 * @see de.freese.persistence.jdbc.state.AbstractPersistenceObjectState#isDeleted()
	 */
	@Override
	public boolean isDeleted()
	{
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "DELETED";
	}
}
