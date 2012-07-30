package de.freese.persistence.jdbc.state;

import java.util.EventObject;

/**
 * Created on 14.02.2004
 * 
 * @author Thomas Freese
 */
public class PersistenceStateEvent extends EventObject
{
	/**
	 *
	 */
	private static final long serialVersionUID = -2348851789569532403L;

	// protected int _type;
	/**
	 * Erstellt ein neues {@link PersistenceStateEvent} Object.
	 * 
	 * @param source {@link Object}
	 */
	public PersistenceStateEvent(final Object source)
	{
		super(source);

		// _type = type;
	}

	// /**
	// * @see de.freese.jamafi.event.JamafiEvent#dispatch(java.lang.Object)
	// */
	// public void dispatch(Object obj)
	// {
	// switch(_type)
	// {
	// case CHANGED :
	// ((PersistenceStateListener) obj).changed(this);
	//
	// break;
	//
	// case SAVED :
	// ((PersistenceStateListener) obj).saved(this);
	//
	// break;
	//
	// case DELETED :
	// ((PersistenceStateListener) obj).deleted(this);
	//
	// break;
	// }
	// }
}
