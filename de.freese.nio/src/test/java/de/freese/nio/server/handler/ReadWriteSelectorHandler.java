/*
 * (c) 2004, Nuno Santos, nfsantos@sapo.pt relased under terms of the GNU public license
 * http://www.gnu.org/licenses/licenses.html#TOCGPL
 */
package de.freese.nio.server.handler;

import java.io.IOException;

/**
 * Interface fuer das Handling von OP_READ / OP_WRITE Events eines SelectionKeys.
 * 
 * @author Nuno Santos
 * @author Thomas Freese
 */
public interface ReadWriteSelectorHandler extends SelectorHandler
{
	/**
	 * Wird vom Acceptor gerufen fuer die interne Konfiguration des Handlers.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	public void configHandle() throws IOException;

	/**
	 * Wird vom SelectorThread gerufen, wenn der Socket bereit fuer das lesen ist.
	 */
	public void handleRead();

	/**
	 * Wird vom SelectorThread gerufen, wenn der Socket bereit fuer das schreiben ist.
	 */
	public void handleWrite();
}