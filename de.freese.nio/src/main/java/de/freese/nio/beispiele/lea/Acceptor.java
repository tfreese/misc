// Created: 14.07.2009
/**
 * 14.07.2009
 */
package de.freese.nio.beispiele.lea;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Thomas Freese
 */
public class Acceptor implements Runnable
{
	/**
	 * 
	 */
	private final ServerSocketChannel serverSocketChannel;

	/**
	 * 
	 */
	private final Selector selector;

	/**
	 * Erstellt ein neues {@link Acceptor} Object.
	 * 
	 * @param serverSocketChannel {@link ServerSocketChannel}
	 * @param selector {@link Selector}
	 * @throws IOException Falls was schief geht.
	 */
	public Acceptor(final ServerSocketChannel serverSocketChannel, final Selector selector)
		throws IOException
	{
		super();

		this.serverSocketChannel = serverSocketChannel;
		this.selector = selector;

		SelectionKey sk = this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		sk.attach(this);
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			SocketChannel socketChannel = this.serverSocketChannel.accept();

			if (socketChannel != null)
			{
				new Handler(this.selector, socketChannel);
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
}
