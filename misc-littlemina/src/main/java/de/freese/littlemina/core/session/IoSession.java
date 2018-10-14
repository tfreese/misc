// Created: 06.01.2010
/**
 * 06.01.2010
 */
package de.freese.littlemina.core.session;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import de.freese.littlemina.core.IoHandler;
import de.freese.littlemina.core.buffer.AbstractIoBuffer;

/**
 * Die {@link IoSession} ist ein Handle für eine Verbindung zwischen zwei end-points eines Transporttyps.
 *
 * @author Thomas Freese
 */
public interface IoSession
{
    /**
     * Schliessen der {@link IoSession} und deren Verbindung.
     */
    public void close();

    /**
     * Liefert ein Attribut einer {@link IoSession}.
     *
     * @param key Object
     * @return <tt>null</tt> wenn kein Attribut existiert
     */
    public Object getAttribute(Object key);

    /**
     * Liefert den {@link AbstractIoBuffer}.
     *
     * @return {@link AbstractIoBuffer}
     */
    public AbstractIoBuffer getBuffer();

    /**
     * Liefert den {@link SocketChannel}.
     *
     * @return {@link SocketChannel}
     */
    public SocketChannel getChannel();

    /**
     * {@link IoHandler} fuer das konkrete Protokoll.
     *
     * @return {@link IoHandler}
     */
    public IoHandler getHandler();

    /**
     * @return {@link SelectionKey}
     */
    public SelectionKey getSelectionKey();

    /**
     * Setzt ein Attribut einer {@link IoSession}.<br>
     *
     * @param key Object
     * @param value Object
     */
    public void setAttribute(Object key, Object value);

    /**
     * Setzt den {@link AbstractIoBuffer}.
     *
     * @param buffer {@link AbstractIoBuffer}
     */
    public void setBuffer(AbstractIoBuffer buffer);

    /**
     * @param key {@link SelectionKey}
     */
    public void setSelectionKey(SelectionKey key);

    /**
     * Schreibt den {@link AbstractIoBuffer} in den Channel.
     *
     * @param buffer {@link AbstractIoBuffer}
     * @throws Exception Falls was schief geht.
     */
    public void write(AbstractIoBuffer buffer) throws Exception;
}
