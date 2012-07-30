package de.freese.persistence.jdbc.state;

import java.util.EventListener;

/**
 * Created on 14.02.2004
 * 
 * @author Thomas Freese
 */
public interface PersistenceStateListener extends EventListener
{
	/**
	 * @param event {@link PersistenceStateEvent}
	 */
	public void changed(PersistenceStateEvent event);

	/**
	 * @param event {@link PersistenceStateEvent}
	 */
	public void complexChanged(PersistenceStateEvent event);

	/**
	 * @param event {@link PersistenceStateEvent}
	 */
	public void deleted(PersistenceStateEvent event);

	/**
	 * @param event {@link PersistenceStateEvent}
	 */
	public void saved(PersistenceStateEvent event);

	/**
	 * @param event {@link PersistenceStateEvent}
	 */
	public void toDelete(PersistenceStateEvent event);
}
