package de.freese.persistence.jdbc.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.freese.persistence.IPersistenceObject;
import de.freese.persistence.exception.PersistenceException;
import de.freese.persistence.jdbc.ICommittable;
import de.freese.persistence.jdbc.manager.AbstractPersistenceManager;
import de.freese.persistence.jdbc.state.AbstractPersistenceObjectState;
import de.freese.persistence.jdbc.state.IllegalPersistenceObjectStateException;
import de.freese.persistence.jdbc.state.PersistenceAttributeChangedEvent;
import de.freese.persistence.jdbc.state.PersistenceAttributeChangedListener;
import de.freese.persistence.jdbc.state.PersistenceStateEvent;
import de.freese.persistence.jdbc.state.PersistenceStateListener;

/**
 * Basisimplementierung eines persistenten Objektes.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractJDBCPersistenceObject implements PropertyChangeListener,
		PersistenceAttributeChangedListener, IPersistenceObject, ICommittable,
		PersistenceCallbacksIfc
{
	/**
	 *
	 */
	private static final long serialVersionUID = -5809808810898403388L;

	/**
	 * 
	 */
	public static final String FIELD_LMTS = "LMTS";

	/**
	 * 
	 */
	public static final String FIELD_OBJECTID = "ID";

	/**
	 * 
	 */
	public static final String FIELD_PARENTID = "PARENT_ID";

	/**
	 * 
	 */
	private AbstractPersistenceManager<?> persistenceManager = null;

	/**
	 * 
	 */
	private long objectID = 0;

	/**
	 * 
	 */
	private long temporaryObjectID = 0;

	/**
	 * 
	 */
	private List<PersistenceAttributeChangedListener> attributChangedListeners = null;

	/**
	 * 
	 */
	private List<PropertyChangeListener> propertyChangeListeners = null;

	/**
	 * 
	 */
	private List<PersistenceStateListener> stateListeners = null;

	/**
	 * 
	 */
	private AbstractPersistenceObjectState currentState = null;

	/**
	 * 
	 */
	private String lastModifiedTimeStamp = "";

	/**
	 * 
	 */
	private String temporaryLastModifiedTimeStamp = null;

	/**
	 * 
	 */
	private boolean allListenderSupported = true;

	/**
	 * 
	 */
	private boolean attributListenerSupported = true;

	/**
	 * 
	 */
	private boolean propertyChangeSupported = true;

	/**
	 * 
	 */
	private boolean stateListenerSupported = true;

	/**
	 * 
	 */
	private long pseudoKey = -1;

	/**
	 * Creates a new {@link AbstractJDBCPersistenceObject} object.
	 */
	public AbstractJDBCPersistenceObject()
	{
		// addStateListener(ObjectTransaction.getInstance());
		// Create ist der einzige Zustand ohne State, deshalb manuelles einhängen in die
		// ObjectTransaction
		// ObjectTransaction.getInstance().addPersistenceObject(this);
		setObjectID(getNextPseudoKey());

		addPropertyChangeListener(this);

		this.currentState = AbstractPersistenceObjectState.getStateCreated();
	}

	/**
	 * @param listener {@link PersistenceAttributeChangedListener}
	 */
	public synchronized void addAttributChangedListeners(	final PersistenceAttributeChangedListener listener)
	{
		if (listener != null)
		{
			getAttributChangedlisteners().add(listener);
		}
	}

	/**
	 * @param listener {@link PropertyChangeListener}
	 */
	public synchronized void addPropertyChangeListener(final PropertyChangeListener listener)
	{
		if (listener != null)
		{
			getPropertyChangeListeners().add(listener);
		}
	}

	/**
	 * @param listener {@link PersistenceStateListener}
	 */
	public synchronized void addStateListener(final PersistenceStateListener listener)
	{
		if (listener != null)
		{
			getStateListeners().add(listener);
		}
	}

	/**
	 * Complexe Attribute insert, update oder delete
	 * 
	 * @throws PersistenceException Falls was schief geht.
	 */
	protected abstract void commitChildren() throws PersistenceException;

	/**
	 * @see de.freese.persistence.jdbc.ICommittable#commitObject()
	 */
	@Override
	public void commitObject() throws PersistenceException
	{
		commitChildren();

		setAllListenderSupported(false);

		// Festschreiben der Änderungen
		if (this.temporaryLastModifiedTimeStamp != null)
		{
			setLastModifiedTimeStamp(this.temporaryLastModifiedTimeStamp);
			this.temporaryLastModifiedTimeStamp = null;
		}

		if (this.temporaryObjectID != 0)
		{
			setObjectID(this.temporaryObjectID);
			this.temporaryObjectID = 0;
		}

		setAllListenderSupported(true);

		// Je nach Status den Statuswechsel durchführen
		if (getCurrentState().isComplexChanged() || getCurrentState().isChanged()
				|| getCurrentState().isCreated())
		{
			getCurrentState().setStateSaved(this);
		}
	}

	/**
	 * @throws IllegalPersistenceObjectStateException Falls was schief geht.
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void delete() throws IllegalPersistenceObjectStateException, PersistenceException
	{
		getCurrentState().delete(this);
	}

	/**
	 * Je nach Status insert, update oder delete, incl. der Children.
	 * 
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void executeState() throws PersistenceException
	{
		executeStateChildren();

		if (getCurrentState().isSaved() || getCurrentState().isDeleted())
		{
			// Nichts machen
		}
		else if (getCurrentState().isChanged())
		{
			update();
		}
		else if (getCurrentState().isComplexChanged())
		{
			getCurrentState().setStateSaved(this);
		}
		else if (getCurrentState().isCreated())
		{
			insert();
		}
		else if (getCurrentState().isToDelete())
		{
			delete();
		}
		else
		{
			// Sollte NIE vorkommen
			throw new PersistenceException("Illegal State for execute State: "
					+ getCurrentState().toString());
		}
	}

	/**
	 * @throws PersistenceException Falls was schief geht.
	 */
	public abstract void executeStateChildren() throws PersistenceException;

	/**
	 * Feuert {@link PersistenceAttributeChangedEvent}.
	 * 
	 * @param event {@link PersistenceAttributeChangedEvent}
	 */
	protected synchronized void fireAttributChangedEvent(	final PersistenceAttributeChangedEvent event)
	{
		if ((this.attributChangedListeners == null) || !isAttributListenerSupported())
		{
			return;
		}

		for (PersistenceAttributeChangedListener listener : getAttributChangedlisteners())
		{
			if (listener != null)
			{
				listener.persistentAttributeChanged(event);
			}
		}
	}

	/**
	 * Feuert {@link PropertyChangeEvent}.
	 * 
	 * @param name String
	 * @param oldValue Object
	 * @param newValue Object
	 */
	protected synchronized void firePropertyChange(final String name, final Object oldValue,
													final Object newValue)
	{
		if ((this.propertyChangeListeners == null) || !isPropertyChangeSupported())
		{
			return;
		}

		PropertyChangeEvent event = new PropertyChangeEvent(this, name, oldValue, newValue);

		for (PropertyChangeListener listener : getPropertyChangeListeners())
		{
			if (listener != null)
			{
				listener.propertyChange(event);
			}
		}
	}

	/**
	 * Feuert {@link PersistenceStateEvent}.
	 * 
	 * @param event {@link PersistenceStateEvent}
	 * @param poState int
	 */
	protected synchronized void fireStateEvent(final PersistenceStateEvent event, final int poState)
	{
		if ((this.stateListeners == null) || !isStateListenerSupported())
		{
			return;
		}

		for (PersistenceStateListener listener : getStateListeners())
		{
			if (listener != null)
			{
				switch (poState)
				{
					case AbstractPersistenceObjectState.COMPLEX_CHANGED:
						listener.complexChanged(event);

						break;

					case AbstractPersistenceObjectState.CHANGED:
						listener.changed(event);

						break;

					case AbstractPersistenceObjectState.SAVED:
						listener.saved(event);

						break;

					case AbstractPersistenceObjectState.TODELETE:
						listener.toDelete(event);

						break;

					case AbstractPersistenceObjectState.DELETED:
						listener.deleted(event);

						break;

					default:
						break;
				}
			}
		}
	}

	/**
	 * @return {@link List}
	 */
	public List<PersistenceAttributeChangedListener> getAttributChangedlisteners()
	{
		if (this.attributChangedListeners == null)
		{
			this.attributChangedListeners = new ArrayList<>();
		}

		return this.attributChangedListeners;
	}

	/**
	 * @return {@link AbstractPersistenceObjectState}
	 */
	public AbstractPersistenceObjectState getCurrentState()
	{
		return this.currentState;
	}

	/**
	 * @return String
	 */
	@Override
	public String getLastModifiedTimeStamp()
	{
		return this.lastModifiedTimeStamp;
	}

	/**
	 * @return long
	 */
	protected long getNextPseudoKey()
	{
		return this.pseudoKey--;
	}

	/**
	 * @return long
	 */
	@Override
	public long getObjectID()
	{
		return this.objectID;
	}

	/**
	 * @return {@link AbstractPersistenceManager}
	 */
	public AbstractPersistenceManager<?> getPersistenceManager()
	{
		return this.persistenceManager;
	}

	/**
	 * @return {@link List}
	 */
	public synchronized List<PropertyChangeListener> getPropertyChangeListeners()
	{
		if (this.propertyChangeListeners == null)
		{
			this.propertyChangeListeners = new ArrayList<>();
		}

		return this.propertyChangeListeners;
	}

	/**
	 * @return {@link List}
	 */
	public synchronized List<PersistenceStateListener> getStateListeners()
	{
		if (this.stateListeners == null)
		{
			this.stateListeners = new ArrayList<>();
		}

		return this.stateListeners;
	}

	/**
	 * @return String
	 */
	public String getTemporaryLastModifiedTimeStamp()
	{
		return this.temporaryLastModifiedTimeStamp;
	}

	/**
	 * @throws IllegalPersistenceObjectStateException Falls was schief geht.
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void insert() throws IllegalPersistenceObjectStateException, PersistenceException
	{
		getCurrentState().insert(this);
	}

	/**
	 * @return boolean
	 */
	public boolean isAllListenderSupported()
	{
		return this.allListenderSupported;
	}

	/**
	 * @return boolean
	 */
	public boolean isAttributListenerSupported()
	{
		return this.attributListenerSupported;
	}

	/**
	 * @return boolean
	 */
	public boolean isPropertyChangeSupported()
	{
		return this.propertyChangeSupported;
	}

	/**
	 * @return boolean
	 */
	public boolean isStateListenerSupported()
	{
		return this.stateListenerSupported;
	}

	/**
	 * @see de.freese.persistence.jdbc.state.PersistenceAttributeChangedListener#persistentAttributeChanged(de.freese.persistence.jdbc.state.PersistenceAttributeChangedEvent)
	 */
	@Override
	public void persistentAttributeChanged(final PersistenceAttributeChangedEvent event)
	{
		if (getCurrentState().isSaved() && !getCurrentState().isComplexChanged())
		{
			// SAVED -> COMPLEX_CHANGED
			try
			{
				getCurrentState().setStateComplexChanged(this);
			}
			catch (IllegalPersistenceObjectStateException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * @see de.freese.persistence.jdbc.model.PersistenceCallbacksIfc#preDelete()
	 */
	@Override
	public void preDelete() throws PersistenceException
	{
		// Empty
	}

	/**
	 * @see de.freese.persistence.jdbc.model.PersistenceCallbacksIfc#preInsert()
	 */
	@Override
	public void preInsert() throws PersistenceException
	{
		setTemporaryLastModifiedTimeStamp(String.format("%1$td.%1$tm.%1$tY %1$tT", new Date()));
	}

	/**
	 * @throws PersistenceException Falls was schief geht.
	 */
	public abstract void preLoadChildren() throws PersistenceException;

	/**
	 * @see de.freese.persistence.jdbc.model.PersistenceCallbacksIfc#preUpdate()
	 */
	@Override
	public void preUpdate() throws PersistenceException
	{
		setTemporaryLastModifiedTimeStamp(String.format("%1$td.%1$tm.%1$tY %1$tT", new Date()));
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent evt)
	{
		// SAVED/COMPLEX_CHANGED -> CHANGED, wenn es nicht CREATED ist
		if ((getCurrentState().isSaved() || getCurrentState().isComplexChanged())
				&& !getCurrentState().isCreated())
		{
			try
			{
				getCurrentState().setStateChanged(this);
			}
			catch (IllegalPersistenceObjectStateException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}

	/**
	 * Freigeben aller Resourcen.
	 */
	public void release()
	{
		getPropertyChangeListeners().clear();
		this.propertyChangeListeners = null;

		getStateListeners().clear();
		this.stateListeners = null;

		getAttributChangedlisteners().clear();
		this.attributChangedListeners = null;

		releaseChildren();
	}

	/**
	 * 
	 */
	protected abstract void releaseChildren();

	/**
	 * @param listener {@link PersistenceAttributeChangedListener}
	 */
	public synchronized void removeAttributChangedListener(	final PersistenceAttributeChangedListener listener)
	{
		if ((this.attributChangedListeners != null) && (this.attributChangedListeners.size() > 0))
		{
			this.attributChangedListeners.remove(listener);

			if (this.attributChangedListeners.size() == 0)
			{
				this.attributChangedListeners = null;
			}
		}
	}

	/**
	 * @param listener {@link PropertyChangeListener}
	 */
	public synchronized void removePropertyChangeListener(final PropertyChangeListener listener)
	{
		if ((this.propertyChangeListeners != null) && (this.propertyChangeListeners.size() > 0))
		{
			this.propertyChangeListeners.remove(listener);

			if (this.propertyChangeListeners.size() == 0)
			{
				this.propertyChangeListeners = null;
			}
		}
	}

	/**
	 * @param listener {@link PersistenceStateListener}
	 */
	public synchronized void removeStateListener(final PersistenceStateListener listener)
	{
		if ((this.stateListeners != null) && (this.stateListeners.size() > 0))
		{
			this.stateListeners.remove(listener);

			if (this.stateListeners.size() == 0)
			{
				this.stateListeners = null;
			}
		}
	}

	/**
	 * @param allListenderSupported boolean
	 */
	public void setAllListenderSupported(final boolean allListenderSupported)
	{
		this.allListenderSupported = allListenderSupported;

		setPropertyChangeSupported(allListenderSupported);
		setStateListenerSupported(allListenderSupported);
		setAttributListenerSupported(allListenderSupported);
	}

	/**
	 * @param attributListenerSupported boolean
	 */
	public void setAttributListenerSupported(final boolean attributListenerSupported)
	{
		this.attributListenerSupported = attributListenerSupported;
	}

	/**
	 * @param state {@link AbstractPersistenceObjectState}
	 */
	public void setCurrentState(final AbstractPersistenceObjectState state)
	{
		if (this.currentState.getIntState() != state.getIntState())
		{
			this.currentState = state;

			fireStateEvent(new PersistenceStateEvent(this), this.currentState.getIntState());
			fireAttributChangedEvent(new PersistenceAttributeChangedEvent(this));
		}
	}

	/**
	 * @param string String
	 */
	public void setLastModifiedTimeStamp(final String string)
	{
		String old = this.lastModifiedTimeStamp;

		if ((string != null) && !string.equals(this.lastModifiedTimeStamp))
		{
			this.lastModifiedTimeStamp = string;

			firePropertyChange(AbstractJDBCPersistenceObject.FIELD_LMTS, old,
					this.lastModifiedTimeStamp);
		}
	}

	/**
	 * @param objectID long
	 */
	@SuppressWarnings("boxing")
	public void setObjectID(final long objectID)
	{
		long old = this.objectID;

		if ((objectID != 0) && (objectID != this.objectID))
		{
			this.objectID = objectID;

			firePropertyChange(AbstractJDBCPersistenceObject.FIELD_OBJECTID, old, this.objectID);
		}
	}

	/**
	 * @param manager {@link AbstractPersistenceManager}
	 */
	public void setPersistenceManager(final AbstractPersistenceManager<?> manager)
	{
		this.persistenceManager = manager;
	}

	/**
	 * @param b boolean
	 */
	public void setPropertyChangeSupported(final boolean b)
	{
		this.propertyChangeSupported = b;
	}

	/**
	 * @param b boolean
	 */
	public void setStateListenerSupported(final boolean b)
	{
		this.stateListenerSupported = b;
	}

	/**
	 * @param temporaryLastModifiedTimeStamp String
	 */
	private void setTemporaryLastModifiedTimeStamp(final String temporaryLastModifiedTimeStamp)
	{
		this.temporaryLastModifiedTimeStamp = temporaryLastModifiedTimeStamp;
	}

	/**
	 * @param temporaryObjectID long
	 */
	public void setTemporaryObjectID(final long temporaryObjectID)
	{
		this.temporaryObjectID = temporaryObjectID;
	}

	/**
	 * @throws IllegalPersistenceObjectStateException Falls was schief geht.
	 */
	public void toDelete() throws IllegalPersistenceObjectStateException
	{
		getCurrentState().setStateToDelete(this);
	}

	/**
	 * @throws IllegalPersistenceObjectStateException Falls was schief geht.
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void update() throws IllegalPersistenceObjectStateException, PersistenceException
	{
		getCurrentState().update(this);
	}
}
