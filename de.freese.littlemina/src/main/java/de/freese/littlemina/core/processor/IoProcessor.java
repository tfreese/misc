// Created: 06.01.2010
/**
 * 06.01.2010
 */
package de.freese.littlemina.core.processor;

import de.freese.littlemina.core.session.IoSession;

/**
 * Ein internes Interface, welches einen 'I/O Processor' representiert.<br>
 * Der Processor verarbeitet die I/O Operationen der {@link IoSession}s<br>
 * und abstrahiert das Reaktor Patter, wie die Java NIO-API.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter Typ der {@link IoSession}
 */
public interface IoProcessor<T extends IoSession>
{
	/**
	 * Hinzufuegen einer {@link IoSession} um alle ihrer I/O Operationen zu verarbeiten.
	 * 
	 * @param session {@link IoSession}
	 */
	public void scheduleAdd(T session);

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

	/**
	 * Entfernt und schliesst die {@link IoSession}.<br>
	 * Der {@link IoProcessor} wird anschliessend die Verbindungen schliessen und verwendetete
	 * Resourcen freigeben.
	 * 
	 * @param session {@link IoSession}
	 */
	public void scheduleRemove(T session);

	/**
	 * Markiert die {@link IoSession} fuer das Schreiben.
	 * 
	 * @param session {@link IoSession}
	 * @throws Exception Falls was schief geht.
	 */
	public void scheduleWrite(T session) throws Exception;
}
