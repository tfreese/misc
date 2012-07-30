package de.freese.persistence.jdbc.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.persistence.exception.PersistenceException;
import de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPersistenceObjectState
{
	/**
	 * 
	 */
	public static final int CHANGED = 2;

	/**
	 * 
	 */
	public static final int CREATED = 1;

	/**
	 * 
	 */
	public static final int DELETED = -1;

	/**
	 * 
	 */
	public static final int TODELETE = 0;

	/**
	 * 
	 */
	public static final int SAVED = 4;

	/**
	 * 
	 */
	public static final int COMPLEX_CHANGED = 5;

	/**
	 * 
	 */
	private static final AbstractPersistenceObjectState STATE_CHANGED =
			new PersistenceObjectStateChanged();

	/**
	 * 
	 */
	private static final AbstractPersistenceObjectState STATE_CREATED =
			new PersistenceObjectStateCreated();

	/**
	 * 
	 */
	private static final AbstractPersistenceObjectState STATE_DELETED =
			new PersistenceObjectStateDeleted();

	/**
	 * 
	 */
	private static final AbstractPersistenceObjectState STATE_SAVED =
			new PersistenceObjectStateSaved();

	/**
	 * 
	 */
	private static final AbstractPersistenceObjectState STATE_TODELETE =
			new PersistenceObjectStateToDelete();

	/**
	 * 
	 */
	private static final AbstractPersistenceObjectState STATE_COMPLEX_CHANGED =
			new PersistenceObjectStateComplexChanged();

	/**
	 * @return {@link AbstractPersistenceObjectState}
	 */
	public static final AbstractPersistenceObjectState getStateChanged()
	{
		return STATE_CHANGED;
	}

	/**
	 * @return {@link AbstractPersistenceObjectState}
	 */
	public static final AbstractPersistenceObjectState getStateComplexChanged()
	{
		return STATE_COMPLEX_CHANGED;
	}

	/**
	 * @return {@link AbstractPersistenceObjectState}
	 */
	public static final AbstractPersistenceObjectState getStateCreated()
	{
		return STATE_CREATED;
	}

	/**
	 * @return {@link AbstractPersistenceObjectState}
	 */
	public static final AbstractPersistenceObjectState getStateDeleted()
	{
		return STATE_DELETED;
	}

	/**
	 * @return {@link AbstractPersistenceObjectState}
	 */
	public static final AbstractPersistenceObjectState getStateSaved()
	{
		return STATE_SAVED;
	}

	/**
	 * @return {@link AbstractPersistenceObjectState}
	 */
	public static final AbstractPersistenceObjectState getStateToDelete()
	{
		return STATE_TODELETE;
	}

	/**
	 *
	 */
	private final Logger logger;

	/**
	 * Erstellt ein neues {@link AbstractPersistenceObjectState} Object.
	 */
	public AbstractPersistenceObjectState()
	{
		super();

		this.logger = LoggerFactory.getLogger(getClass());
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @param newState {@link AbstractPersistenceObjectState}
	 */
	protected final void changeState(final AbstractJDBCPersistenceObject po,
										final AbstractPersistenceObjectState newState)
	{
		AbstractPersistenceObjectState oldState = po.getCurrentState();

		po.setCurrentState(newState);

		getLogger().debug(
				po + " - Switching State (" + oldState + " -> " + po.getCurrentState() + ")");
		getLogger().debug(po.toString());
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws IllegalPersistenceObjectStateException Falls was schief geht.
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void delete(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException, PersistenceException
	{
		throw new IllegalPersistenceObjectStateException(po + " - Delete is in State "
				+ po.getCurrentState() + " not allowed");
	}

	/**
	 * @return int
	 */
	public abstract int getIntState();

	/**
	 * @return {@link Logger}
	 */
	protected Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws IllegalPersistenceObjectStateException Falls was schief geht.
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void insert(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException, PersistenceException
	{
		throw new IllegalPersistenceObjectStateException(po + " - Insert is in State "
				+ po.getCurrentState() + " not allowed");
	}

	/**
	 * @return boolean
	 */
	public boolean isChanged()
	{
		return false;
	}

	/**
	 * @return boolean
	 */
	public boolean isComplexChanged()
	{
		return false;
	}

	/**
	 * @return boolean
	 */
	public boolean isCreated()
	{
		return false;
	}

	/**
	 * @return boolean
	 */
	public boolean isDeleted()
	{
		return false;
	}

	/**
	 * @return boolean
	 */
	public boolean isSaved()
	{
		return false;
	}

	/**
	 * @return boolean
	 */
	public boolean isToDelete()
	{
		return false;
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws IllegalPersistenceObjectStateException Falls was schief geht.
	 */
	public void setStateChanged(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException
	{
		throw new IllegalPersistenceObjectStateException(po + " - Change State from "
				+ po.getCurrentState() + " to " + getStateChanged() + " is not allowed");
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws IllegalPersistenceObjectStateException Falls was schief geht.
	 */
	public void setStateComplexChanged(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException
	{
		throw new IllegalPersistenceObjectStateException(po + " - Change State from "
				+ po.getCurrentState() + " to " + getStateComplexChanged() + " is not allowed");
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws IllegalPersistenceObjectStateException Falls was schief geht.
	 */
	public void setStateDeleted(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException
	{
		throw new IllegalPersistenceObjectStateException(po + " - Change State from "
				+ po.getCurrentState() + " to " + getStateDeleted() + " is not allowed");
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws IllegalPersistenceObjectStateException Falls was schief geht.
	 */
	public void setStateSaved(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException
	{
		throw new IllegalPersistenceObjectStateException(po + " - Change State from "
				+ po.getCurrentState() + " to " + getStateSaved() + " is not allowed");
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws IllegalPersistenceObjectStateException Falls was schief geht.
	 */
	public void setStateToDelete(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException
	{
		throw new IllegalPersistenceObjectStateException(po + " - Change State from "
				+ po.getCurrentState() + " to " + getStateToDelete() + " is not allowed");
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws IllegalPersistenceObjectStateException Falls was schief geht.
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void update(final AbstractJDBCPersistenceObject po)
		throws IllegalPersistenceObjectStateException, PersistenceException
	{
		throw new IllegalPersistenceObjectStateException(po + " - Update is in State "
				+ po.getCurrentState() + " not allowed");
	}
}
