/*
 * Created on 09.09.2004
 */
package de.freese.persistence.jdbc.model;

import de.freese.persistence.exception.PersistenceException;

/**
 * Interface fuer Objektaenderungen.
 * 
 * @author Thomas Freese
 */
public interface PersistenceCallbacksIfc
{
	/**
	 * Diese Methode ist nicht durch die Transactions gesteuert !
	 * 
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void preDelete() throws PersistenceException;

	/**
	 * Diese Methode ist nicht durch die Transactions gesteuert !
	 * 
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void preInsert() throws PersistenceException;

	/**
	 * Diese Methode ist nicht durch die Transactions gesteuert !
	 * 
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void preUpdate() throws PersistenceException;
}
