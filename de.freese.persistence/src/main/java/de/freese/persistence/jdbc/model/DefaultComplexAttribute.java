package de.freese.persistence.jdbc.model;

import java.util.ArrayList;
import java.util.List;

import de.freese.persistence.exception.PersistenceException;
import de.freese.persistence.jdbc.manager.AbstractPersistenceManager;
import de.freese.persistence.jdbc.manager.PersistenceManagerFactory;

/**
 * Definiert ein Complexes Attribut eines PersistenceObjects<br>
 * 
 * @author Thomas Freese
 */
public class DefaultComplexAttribute
{
	/**
     * 
     */
	private AbstractJDBCPersistenceObject parent = null;

	/**
     * 
     */
	private AbstractPersistenceManager<?> persistenceManager = null;

	/**
	 *
	 */
	private List<AbstractJDBCPersistenceObject> children = null;

	/**
	 * 
	 */
	private boolean loaded = false;

	/**
	 * Erstellt ein neues {@link DefaultComplexAttribute} Object.
	 * 
	 * @param parent {@link AbstractJDBCPersistenceObject}
	 * @param pmClass Class
	 */
	@SuppressWarnings(
	{
			"unchecked", "rawtypes"
	})
	public DefaultComplexAttribute(final AbstractJDBCPersistenceObject parent,
			final Class<?> pmClass)
	{
		super();

		setParent(parent);

		if (pmClass != null)
		{
			setPersistenceManager(PersistenceManagerFactory.getPMInstance((Class) pmClass, parent
					.getPersistenceManager().getConnection()));
		}
		else
		{
			throw new IllegalArgumentException("PersistenceManagerClass can't be NULL !!!");
		}
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void addChild(final AbstractJDBCPersistenceObject po) throws PersistenceException
	{
		getChildren().add(po);

		// Den Parent als Listener eintragen
		(po).addAttributChangedListeners(getParent());

		// Dem Parent bescheid geben
		AbstractJDBCPersistenceObject parent = getParent();

		if (parent.getCurrentState().isSaved())
		{
			parent.getCurrentState().setStateComplexChanged(parent);
		}
	}

	/**
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void commitChildren() throws PersistenceException
	{
		for (Object element : getChildren())
		{
			AbstractJDBCPersistenceObject po = (AbstractJDBCPersistenceObject) element;
			po.commitObject();
		}
	}

	/**
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void executeStateChildren() throws PersistenceException
	{
		for (Object element : getChildren())
		{
			AbstractJDBCPersistenceObject po = (AbstractJDBCPersistenceObject) element;
			po.executeState();
		}
	}

	/**
	 * @return {@link List}
	 * @throws PersistenceException Falls was schief geht.
	 */
	public List<AbstractJDBCPersistenceObject> getChildren() throws PersistenceException
	{
		if (this.children == null)
		{
			this.children = new ArrayList<>();

			if (!isLoaded())
			{
				loadChildren();
			}
		}

		return this.children;
	}

	/**
	 * @return {@link List}
	 * @throws PersistenceException Falls was schief geht.
	 */
	public List<AbstractJDBCPersistenceObject> getChildrenWithReload() throws PersistenceException
	{
		this.children.clear();
		this.children = null;
		setLoaded(false);

		return getChildren();
	}

	/**
	 * @return {@link AbstractJDBCPersistenceObject}
	 */
	public AbstractJDBCPersistenceObject getParent()
	{
		return this.parent;
	}

	/**
	 * @return {@link AbstractPersistenceManager}
	 */
	public AbstractPersistenceManager<?> getPersistenceManager()
	{
		return this.persistenceManager;
	}

	/**
	 * @return boolean
	 */
	public boolean isLoaded()
	{
		return this.loaded;
	}

	/**
	 * @return boolean
	 * @throws PersistenceException Falls was schief geht.
	 */
	public boolean isSingleChild() throws PersistenceException
	{
		if (getChildren().size() == 1)
		{
			return true;
		}

		return false;
	}

	/**
	 * @throws PersistenceException Falls was schief geht.
	 */
	@SuppressWarnings("unchecked")
	public void loadChildren() throws PersistenceException
	{
		setChildren((List<AbstractJDBCPersistenceObject>) getPersistenceManager().selectByParentID(
				getParent().getObjectID()));

		setLoaded(true);
	}

	/**
	 * Bereinigt die Liste der Children.<br>
	 * 
	 * @throws PersistenceException Falls was schief geht.
	 */
	// public void refreshComplexAttributes()
	// throws PersistenceException
	// {
	// if (_children != null)
	// {
	// // Geloeschte POs entfernen
	// for (int i = 0; i < getChildren().size(); i++)
	// {
	// AbstractJDBCPersistenceObject po = (AbstractJDBCPersistenceObject) getChildren().get(i);
	// //iter.next();
	//
	// if (po.getCurrentState().isDeleted())
	// {
	// getChildren().remove(po);
	// }
	// }
	//
	// // Aufruf weiterleiten
	// for (Iterator iter = getChildren().iterator(); iter.hasNext();)
	// {
	// AbstractJDBCPersistenceObject po = (AbstractJDBCPersistenceObject) iter.next();
	//
	// po.refreshComplexAttributes();
	// }
	//
	// // Wenn alle SAVED sind dann auch den Parent SAVED setzen
	// if (getParent().getCurrentState().isComplexChanged())
	// {
	// Iterator iter = getChildren().iterator();
	// boolean alleSave = ((AbstractJDBCPersistenceObject) iter.next()).getCurrentState().isSaved();
	//
	// while (iter.hasNext())
	// {
	// AbstractJDBCPersistenceObject po = (AbstractJDBCPersistenceObject) iter.next();
	//
	// alleSave = alleSave && po.getCurrentState().isSaved();
	// }
	//
	// if (alleSave)
	// {
	// getParent().getCurrentState().setStateSaved(getParent());
	// }
	// }
	// }
	// }
	/**
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void releaseChildren() throws PersistenceException
	{
		for (Object element : getChildren())
		{
			AbstractJDBCPersistenceObject po = (AbstractJDBCPersistenceObject) element;
			po.release();
		}
	}

	/**
	 * @param obj Object
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void removeChild(final Object obj) throws PersistenceException
	{
		getChildren().remove(obj);
	}

	/**
	 * @param list {@link List}
	 */
	protected void setChildren(final List<AbstractJDBCPersistenceObject> list)
	{
		this.children = list;

		// Den Parent als StateListener eintragen
		for (AbstractJDBCPersistenceObject abstractJDBCPersistenceObject : list)
		{
			AbstractJDBCPersistenceObject po = abstractJDBCPersistenceObject;
			po.addAttributChangedListeners(getParent());
		}
	}

	/**
	 * @param booloean boolean
	 */
	protected void setLoaded(final boolean booloean)
	{
		this.loaded = booloean;
	}

	/**
	 * @param object {@link AbstractJDBCPersistenceObject}
	 */
	private void setParent(final AbstractJDBCPersistenceObject object)
	{
		this.parent = object;
	}

	/**
	 * @param manager {@link AbstractPersistenceManager}
	 */
	private void setPersistenceManager(final AbstractPersistenceManager<?> manager)
	{
		this.persistenceManager = manager;
	}
}
