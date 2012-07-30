// Created: 13.07.2009
/**
 * 13.07.2009
 */
package de.freese.nio.beispiele.lea;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Thomas Freese
 */
public class Reactor implements Runnable
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		Reactor reactor = new Reactor(8000);
		new Thread(reactor, "Reactor").start();
	}

	// class Acceptor implements Runnable
	// {
	// public void run()
	// {
	// try
	// {
	// SocketChannel socketChannel = Reactor.this.serverSocketChannel.accept();
	//
	// if (socketChannel != null)
	// {
	// new Handler(Reactor.this.selector, socketChannel);
	// }
	// }
	// catch (IOException ex)
	// {
	// ex.printStackTrace();
	// }
	// }
	// }
	/**
 * 
 */
	private final Selector selector;

	/**
	 * 
	 */
	private final ServerSocketChannel serverSocketChannel;

	/**
	 * Erstellt ein neues {@link Reactor} Object.
	 * 
	 * @param port int
	 * @throws IOException Falls was schief geht.
	 */
	public Reactor(final int port) throws IOException
	{
		super();

		this.selector = Selector.open();
		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.socket().bind(new InetSocketAddress(port));
		this.serverSocketChannel.configureBlocking(false);

		// SelectionKey sk = this.serverSocketChannel.register(this.selector,
		// SelectionKey.OP_ACCEPT);
		// sk.attach(new Acceptor(this.serverSocketChannel, this.selector));
		new Acceptor(this.serverSocketChannel, this.selector);
	}

	/**
	 * @param selectionKey {@link SelectionKey}
	 */
	void dispatch(final SelectionKey selectionKey)
	{
		Runnable runnable = (Runnable) selectionKey.attachment();

		if (runnable != null)
		{
			System.out.println("Reactor.dispatch(): " + selectionKey.interestOps());
			runnable.run();
			// ThreadQueue.getInstance().execute(runnable);
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			while (!Thread.interrupted())
			{
				this.selector.select();
				Set<SelectionKey> selected = this.selector.selectedKeys();
				Iterator<SelectionKey> iterator = selected.iterator();

				while (iterator.hasNext())
				{
					SelectionKey selectionKey = iterator.next();
					iterator.remove();

					dispatch(selectionKey);
				}
				selected.clear();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
}
