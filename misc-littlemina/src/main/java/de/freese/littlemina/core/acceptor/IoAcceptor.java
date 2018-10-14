// Created: 06.01.2010
/**
 * 06.01.2010
 */
package de.freese.littlemina.core.acceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import de.freese.littlemina.core.service.IoService;

/**
 * Nimmt eingehende Verbindungen entgegen, kommuniziert mit den Clients.
 *
 * @author Thomas Freese
 */
public interface IoAcceptor extends IoService
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
}
