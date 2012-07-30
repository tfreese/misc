/*
 * Created on 25.07.2004
 */
package de.freese.persistence.jdbc;

import de.freese.persistence.exception.PersistenceException;

/**
 * Interface fuer ein commitbares Objekt.
 * 
 * @author Thomas Freese
 */
public interface ICommittable
{
	/**
	 * Commit des Objektes und seiner Children.
	 * 
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void commitObject() throws PersistenceException;
}
