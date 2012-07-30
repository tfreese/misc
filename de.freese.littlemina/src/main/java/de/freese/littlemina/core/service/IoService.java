// Created: 16.01.2010
/**
 * 16.01.2010
 */
package de.freese.littlemina.core.service;

/**
 * BasisInterfacer aller {@link IoService}es.
 * 
 * @author Thomas Freese
 */
public interface IoService
{
	/**
	 * Freigeben aller Resourcen.
	 */
	public void dispose();

	/**
	 * Liefert <tt>true</tt>, wenn das Disposing beendet ist.
	 * 
	 * @return boolean
	 */
	public boolean isDisposed();

	/**
	 * Liefert <tt>true</tt>, wenn dispose aufgerufen worden ist.
	 * 
	 * @return boolean
	 */
	public boolean isDisposing();
}
