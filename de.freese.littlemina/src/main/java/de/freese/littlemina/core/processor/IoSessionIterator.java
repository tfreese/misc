// Created: 14.01.2010
/**
 * 14.01.2010
 */
package de.freese.littlemina.core.processor;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import de.freese.littlemina.core.session.IoSession;
import de.freese.littlemina.core.session.NioSocketSession;

/**
 * Kapselt den Iterator um die {@link Selector#selectedKeys()} oder {@link Selector#keys()}<br>
 * und liefert die {@link IoSession} des {@link SelectionKey}s.
 * 
 * @author Thomas Freese
 */
class IoSessionIterator implements Iterator<NioSocketSession>
{
	/**
	 *
	 */
	private final Iterator<SelectionKey> iterator;

	/**
	 * Erstellt ein neues {@link IoSessionIterator} Object.
	 * 
	 * @param keys {@link Set}
	 */
	public IoSessionIterator(final Set<SelectionKey> keys)
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
	public NioSocketSession next()
	{
		SelectionKey key = this.iterator.next();
		NioSocketSession nioSession = (NioSocketSession) key.attachment();

		return nioSession;
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
