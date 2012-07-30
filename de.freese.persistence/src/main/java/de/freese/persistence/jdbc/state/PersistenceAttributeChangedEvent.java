package de.freese.persistence.jdbc.state;

import java.util.EventObject;

/**
 * Created on 12.03.2004
 * 
 * @author Thomas Freese
 */
public class PersistenceAttributeChangedEvent extends EventObject
{
	/**
	 *
	 */
	private static final long serialVersionUID = -1766277396097555867L;

	/**
	 * Erstellt ein neues {@link PersistenceAttributeChangedEvent} Object.
	 * 
	 * @param source Object
	 */
	public PersistenceAttributeChangedEvent(final Object source)
	{
		super(source);
	}
}
