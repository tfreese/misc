// Created: 14.01.2010
/**
 * 14.01.2010
 */
package de.freese.littlemina.core.acceptor;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Kapselt den Iterator um die {@link Selector#selectedKeys()} oder {@link Selector#keys()}<br>
 * und liefert die {@link ServerSocketChannel}s des {@link SelectionKey}s.
 * 
 * @author Thomas Freese
 */
class ServerSocketChannelIterator implements Iterator<ServerSocketChannel>
{
	/**
	 *
	 */
	private final Iterator<SelectionKey> iterator;

	/**
	 * Erstellt ein neues {@link ServerSocketChannelIterator} Object.
	 * 
	 * @param keys {@link Set}
	 */
	public ServerSocketChannelIterator(final Set<SelectionKey> keys)
	{
		super();

		this.iterator = keys.iterator();
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext()
	{
		return this.iterator.hasNext();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@Override
	public ServerSocketChannel next()
	{
		SelectionKey key = this.iterator.next();

		if (key.isValid() && key.isAcceptable())
		{
			return (ServerSocketChannel) key.channel();
		}

		return null;
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove()
	{
		this.iterator.remove();
	}
}
