/*
 * (c) 2004, Nuno Santos, nfsantos@sapo.pt relased under terms of the GNU public license
 * http://www.gnu.org/licenses/licenses.html#TOCGPL
 */
package de.freese.nio.server.handler;

/**
 * Interface fuer das Handling von OP_ACCEPT Events eines SelectionKeys.
 * 
 * @author Nuno Santos
 * @author Thomas Freese
 */
public interface AcceptSelectorHandler extends SelectorHandler
{
	/**
	 * Called by SelectorThread when the server socket associated with the class implementing this
	 * interface receives a request for establishing a connection.<br>
	 * This method should not be called from anywhere else.
	 */
	public void handleAccept();
}
