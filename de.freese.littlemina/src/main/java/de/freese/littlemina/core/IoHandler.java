// Created: 11.01.2010
/**
 * 11.01.2010
 */
package de.freese.littlemina.core;

import de.freese.littlemina.core.session.IoSession;

/**
 * IoHandler fuer die konkrete Implementierung des Protokolls.
 * 
 * @author Thomas Freese
 */
public interface IoHandler
{
	/**
	 * Wird aufgerufen, wenn die {@link IoSession} bereit fuer die Verarbeitung ist.
	 * 
	 * @param session {@link de.freese.littlemina.core.session.IoSession}
	 * @throws Exception Falls was schief geht.
	 */
	public void sessionOpened(final IoSession session) throws Exception;

	/**
	 * Wird aufgerufen, wenn die {@link IoSession} geschlossen ist.
	 * 
	 * @param session {@link de.freese.littlemina.core.session.IoSession}
	 * @throws Exception Falls was schief geht.
	 */
	public void sessionClosed(final IoSession session) throws Exception;

	/**
	 * Wird aufgerufen, wenn die {@link IoSession} Daten empfangen hat.
	 * 
	 * @param session {@link de.freese.littlemina.core.session.IoSession}
	 * @throws Exception Falls was schief geht.
	 */
	public void messageReceived(final IoSession session) throws Exception;
}
