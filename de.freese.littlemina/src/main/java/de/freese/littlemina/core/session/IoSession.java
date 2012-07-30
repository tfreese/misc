// Created: 06.01.2010
/**
 * 06.01.2010
 */
package de.freese.littlemina.core.session;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import de.freese.littlemina.core.IoHandler;
import de.freese.littlemina.core.buffer.IoBuffer;

/**
 * Die {@link IoSession} ist ein Handle fuer eine Verbindung zwischen zwei end-points eines
 * Transporttyps.
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
	 * Schreibt den {@link IoBuffer} in den Channel.
	 * 
	 * @param buffer {@link IoBuffer}
	 * @throws Exception Falls was schief geht.
	 */
	public void write(IoBuffer buffer) throws Exception;

	/**
	 * Liefert ein Attribut einer {@link IoSession}.<br>
	 * Funktionsweise:
	 * 
	 * <pre>
	 * if (containsAttribute(key))
	 * {
	 * 	return getAttribute(key);
	 * }
	 * else
	 * {
	 * 	setAttribute(key, defaultValue);
	 * 	return defaultValue;
	 * }
	 * </pre>
	 * 
	 * @param key Object
	 * @param defaultValue Object
	 * @return Object
	 */
	public Object getAttribute(Object key, Object defaultValue);

	/**
	 * Liefert den {@link SocketChannel}.
	 * 
	 * @return {@link SocketChannel}
	 */
	public SocketChannel getChannel();

	/**
	 * Liefert den {@link IoBuffer}.
	 * 
	 * @return {@link IoBuffer}
	 */
	public IoBuffer getBuffer();

	/**
	 * Setzt den {@link IoBuffer}.
	 * 
	 * @param buffer {@link IoBuffer}
	 */
	public void setBuffer(IoBuffer buffer);

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
	 * @return Object; alter Wert oder null
	 */
	public Object setAttribute(Object key, Object value);

	/**
	 * @param key {@link SelectionKey}
	 */
	public void setSelectionKey(SelectionKey key);
}
