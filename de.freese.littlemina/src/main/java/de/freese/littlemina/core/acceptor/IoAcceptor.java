// Created: 06.01.2010
/**
 * 06.01.2010
 */
package de.freese.littlemina.core.acceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

import de.freese.littlemina.core.IoHandler;

/**
 * Nimmt eingehende Verbindungen entgegen, kommuniziert mit den Clients.
 * 
 * @author Thomas Freese
 */
public interface IoAcceptor
{
	/**
	 * Bindet die lokale Addresse und ist anschliessend bereit Anfragen zu verarbeiten.
	 * 
	 * @param localAddress {@link InetSocketAddress}
	 * @throws IOException Wenn die Addresse bereits belegt ist.
	 */
	public void bind(InetSocketAddress localAddress) throws IOException;

	/**
	 * Bindet die lokale Addresse und ist anschliessend bereit Anfragen zu verarbeiten.
	 * 
	 * @param port int
	 * @throws IOException Wenn die Addresse bereits belegt ist.
	 */
	public void bind(int port) throws IOException;

	/**
	 * Freigeben aller Resourcen.
	 */
	public void dispose();

	/**
	 * {@link IoHandler} fuer das konkrete Protokoll.
	 * 
	 * @return {@link IoHandler}
	 */
	public IoHandler getHandler();

	/**
	 * Liefert die mit dem {@link IoAcceptor} verbundenen lokalen Addresse.
	 * 
	 * @return {@link InetSocketAddress}
	 */
	public InetSocketAddress getLocalAddress();

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
