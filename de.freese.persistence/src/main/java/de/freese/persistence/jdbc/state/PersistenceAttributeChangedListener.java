package de.freese.persistence.jdbc.state;

import java.util.EventListener;

/**
 * Created on 12.03.2004
 * 
 * @author Thomas Freese
 */
public interface PersistenceAttributeChangedListener extends EventListener
{
	/**
	 * @param event {@link PersistenceAttributeChangedEvent}
	 */
	public void persistentAttributeChanged(PersistenceAttributeChangedEvent event);
}
