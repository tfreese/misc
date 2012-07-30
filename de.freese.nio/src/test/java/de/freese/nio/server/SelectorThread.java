/*
 * (c) 2004, Nuno Santos, nfsantos@sapo.pt relased under terms of the GNU public license
 * http://www.gnu.org/licenses/licenses.html#TOCGPL
 */
package de.freese.nio.server;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.nio.server.handler.AcceptSelectorHandler;
import de.freese.nio.server.handler.ReadWriteSelectorHandler;
import de.freese.nio.server.handler.SelectorHandler;

/**
 * Eventqueue fuer I/O Events eines Selectors.
 * 
 * @author Nuno Santos
 * @author Thomas Freese
 */
public final class SelectorThread implements Runnable
{
	/**
	 *
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SelectorThread.class);

	/**
	 * Selector fuer das I/O multiplexing
	 */
	private Selector selector;

	/**
	 * Der Thread fuer den Selector
	 */
	private final Thread selectorThread;

	/**
	 * Flag fuer die Beendigung des SelectorThreads,
	 */
	private boolean closeRequested = false;

	/**
	 * Liste von Tasks zur Ausfuehrung innerhalb des SelectorThreads.
	 */
	private final List<Runnable> pendingTasks = new ArrayList<>(32);

	/**
	 * Erstellt ein neues {@link SelectorThread} Object.<br>
	 * Erzeugt einen neuen Selector und startet innerhalb des Konstruktors den Thread.
	 * 
	 * @param name String, Optional
	 * @throws IOException Falls was schief geht.
	 */
	SelectorThread(final String name) throws IOException
	{
		super();

		this.selector = Selector.open();

		this.selectorThread = new Thread(this, name != null ? name : getClass().getSimpleName());
		this.selectorThread.start();
	}

	/**
	 * Wie die Methode addChannelInterestNow(), wird aber asynchron im SelectorThread ausgefuehrt.
	 * 
	 * @param channel {@link SelectableChannel}; Zu beobachtender Channel.
	 * @param interest int; Zu beobachtende Interest, sollte eine Kombination der SelectionKey
	 *            Konstanten sein.
	 */
	public void addChannelInterestLater(final SelectableChannel channel, final int interest)
	{
		invokeLater(new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				try
				{
					addChannelInterestNow(channel, interest);
				}
				catch (IOException ex)
				{
					LOGGER.error(null, ex);
				}
			}
		});
	}

	/**
	 * Fuegt dem SelectionKey des Channels fuer diesen Selector ein weiteres Interest-Event hinzu.
	 * Diese Methode sollte nur im SelectorThread ausgefuehrt werden.
	 * 
	 * @param channel {@link SelectableChannel}; Zu beobachtender Channel.
	 * @param interest int; Zu beobachtende Interest, sollte eine Kombination der SelectionKey
	 *            Konstanten sein.
	 * @throws IOException Falls der rufende Thread nicht der SelectorThread ist.
	 */
	public void addChannelInterestNow(final SelectableChannel channel, final int interest)
		throws IOException
	{
		if (Thread.currentThread() != this.selectorThread)
		{
			throw new IOException("Method can only be called from selector thread");
		}

		SelectionKey sk = channel.keyFor(this.selector);
		changeKeyInterest(sk, sk.interestOps() | interest);
	}

	/**
	 * Aendern der InterestOps eines SelectionKeys.
	 * 
	 * @param sk {@link SelectionKey}
	 * @param newInterest int
	 * @throws IOException Falls was schief geht.
	 */
	private void changeKeyInterest(final SelectionKey sk, final int newInterest) throws IOException
	{
		try
		{
			sk.interestOps(newInterest);
		}
		catch (Exception ex)
		{
			IOException ioe = new IOException("Failed to change channel interest.");
			ioe.initCause(ex);
			throw ioe;
		}
	}

	/**
	 * Aufraeumen aller SelectionKeys, die am Selector registriet sind.
	 */
	private void closeSelectorAndChannels()
	{
		Set<SelectionKey> keys = this.selector.keys();

		for (SelectionKey selectionKey : keys)
		{
			SelectionKey sk = selectionKey;

			try
			{
				sk.channel().close();
			}
			catch (IOException ex)
			{
				LOGGER.warn(null, ex);
			}
		}
		try
		{
			this.selector.close();
		}
		catch (IOException ex)
		{
			LOGGER.warn(null, ex);
		}
	}

	/**
	 * Ausfuehren aller wartenden Tasks.
	 */
	private void doInvocations()
	{
		synchronized (this.pendingTasks)
		{
			for (int i = 0; i < this.pendingTasks.size(); i++)
			{
				Runnable task = this.pendingTasks.get(i);
				task.run();
			}

			this.pendingTasks.clear();
		}
	}

	/**
	 * Ausfuehrung des Tasks synchron im SelectorThread.
	 * 
	 * @param task {@link Runnable}
	 * @throws InterruptedException Falls was schief geht.
	 */
	public void invokeAndWait(final Runnable task) throws InterruptedException
	{
		if (Thread.currentThread() == this.selectorThread)
		{
			// Wir sind bereits im SelectorThread, daher direkt ausfuehren.
			task.run();
		}
		else
		{
			// ThreadSperre
			// final Object latch = new Object();
			final CountDownLatch latch = new CountDownLatch(1);

			synchronized (latch)
			{
				invokeLater(new Runnable()
				{
					/**
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run()
					{
						task.run();

						// SelectorThread benachrichtigen
						// latch.notify();
						latch.countDown();
					}
				});

				// Warten auf die Benachrichtigung des Tasks
				// latch.wait();
				latch.await();
			}
		}
	}

	/**
	 * Ausfuehrung des Tasks asynchron im SelectorThread.
	 * 
	 * @param task {@link Runnable}
	 */
	public void invokeLater(final Runnable task)
	{
		synchronized (this.pendingTasks)
		{
			this.pendingTasks.add(task);
		}

		this.selector.wakeup();
	}

	/**
	 * Wie die Methode registerChannelInterestNow(), wird aber asynchron im SelectorThread
	 * ausgefuehrt.
	 * 
	 * @param channel {@link SelectableChannel}; Zu beobachtender Channel.
	 * @param interest int; Zu beobachtende Interest, sollte eine Kombination der SelectionKey
	 *            Konstanten sein.
	 * @param handler {@link SelectorHandler}; Handler fuer die verarbeitung der Interestevents auf
	 *            dem Channel.
	 */
	public void registerChannelInterestLater(final SelectableChannel channel, final int interest,
												final SelectorHandler handler)
	{
		invokeLater(new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				try
				{
					registerChannelInterestNow(channel, interest, handler);
				}
				catch (IOException ex)
				{
					LOGGER.error(null, ex);
				}
			}
		});
	}

	/**
	 * Bindet einen SelectableChannel an diesen Selector und registriert einen Handler fuer
	 * bestimmte Interest-Events.<br>
	 * Diese Methode sollte nur im SelectorThread ausgefuehrt werden.
	 * 
	 * @param channel {@link SelectableChannel}; Zu beobachtender Channel.
	 * @param interest int; Zu beobachtende Interest, sollte eine Kombination der SelectionKey
	 *            Konstanten sein.
	 * @param handler {@link SelectorHandler}; Handler fuer die verarbeitung der Interestevents auf
	 *            dem Channel.
	 * @throws IOException Falls was schief geht.
	 */
	public void registerChannelInterestNow(final SelectableChannel channel, final int interest,
											final SelectorHandler handler) throws IOException
	{
		if (Thread.currentThread() != this.selectorThread)
		{
			throw new IOException("Method can only be called from selector thread");
		}

		if (!channel.isOpen())
		{
			throw new IOException("Channel is not open.");
		}

		try
		{
			if (channel.isRegistered())
			{
				SelectionKey sk = channel.keyFor(this.selector);

				if (sk == null)
				{
					throw new IOException("Channel is not registered with selector.");
				}

				sk.interestOps(interest);
				Object previousHandler = sk.attach(handler);

				if (previousHandler == null)
				{
					throw new IOException("No Handler attached to SelectionKey for Interest.");
				}
			}
			else
			{
				channel.configureBlocking(false);
				channel.register(this.selector, interest, handler);
			}
		}
		catch (Exception ex)
		{
			IOException ioe = new IOException("Error registering channel.");
			ioe.initCause(ex);
			throw ioe;
		}
	}

	/**
	 * Wie die Methode removeChannelInterestNow(), wird aber asynchron im SelectorThread
	 * ausgefuehrt.
	 * 
	 * @param channel {@link SelectableChannel}; Zu aktualisierender Channel, muss registriert sein.
	 * @param interest int; Zu entfernender Interest, sollte einer der definierten Interests im
	 *            SelectionKey sein.
	 */
	public void removeChannelInterestLater(final SelectableChannel channel, final int interest)
	{
		invokeLater(new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				try
				{
					removeChannelInterestNow(channel, interest);
				}
				catch (IOException ex)
				{
					LOGGER.error(null, ex);
				}
			}
		});
	}

	/**
	 * Entfernt einen Interest eines Channels mit sofortiger Wirkung.<br>
	 * Diese Methode sollte nur aus dem SelectorThread aufgerufen werden.
	 * 
	 * @param channel {@link SelectableChannel}; Zu aktualisierender Channel, muss registriert sein.
	 * @param interest int; Zu entfernender Interest, sollte einer der definierten Interests im
	 *            SelectionKey sein.
	 * @throws IOException Falls der rufende Thread nicht der SelectorThread ist.
	 */
	public void removeChannelInterestNow(final SelectableChannel channel, final int interest)
		throws IOException
	{
		if (Thread.currentThread() != this.selectorThread)
		{
			throw new IOException("Method can only be called from selector thread");
		}

		SelectionKey sk = channel.keyFor(this.selector);
		changeKeyInterest(sk, sk.interestOps() & ~interest);
	}

	/**
	 * Setzt ein internes Flag fuer den Austritt aus der run-Methode.
	 */
	public void requestClose()
	{
		this.closeRequested = true;

		// Selector anstossen
		this.selector.wakeup();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		// Hauptmethode: Der Selector reagiert auf alle registrierten Operationen
		while (true)
		{
			// Alle wartenden Selector Tasks ausfuehren
			doInvocations();

			// Abbruch ?
			if (this.closeRequested)
			{
				return;
			}

			int selectedKeys = 0;

			try
			{
				selectedKeys = this.selector.select();
			}
			catch (IOException ioe)
			{
				// Duerfte eigentlich nie passieren
				LOGGER.error(null, ioe);

				continue;
			}

			if (selectedKeys == 0)
			{
				continue;
			}

			Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();

			// Dispatching des Events fuer jeden SelectionKey.
			while (it.hasNext())
			{
				SelectionKey sk = it.next();
				LOGGER.info(sk + " " + sk.interestOps());

				it.remove();

				try
				{
					// Aktuelle IntestOps des Keys ermitteln.
					int readyOps = sk.readyOps();

					// Diesen Interest deaktivieren, um mehrfache Events dieses Typs zu vermeiden.
					sk.interestOps(sk.interestOps() & ~readyOps);
					SelectorHandler handler = (SelectorHandler) sk.attachment();

					// Die InterestOps des SelectionKeys pruefen.
					if (sk.isAcceptable())
					{
						// Bereit fuer das auslesen
						((AcceptSelectorHandler) handler).handleAccept();

					}
					else if (sk.isConnectable())
					{
						// A connection is ready to be accepted
						// ((ConnectorSelectorHandler) handler).handleConnect();
					}
					else
					{
						ReadWriteSelectorHandler rwHandler = (ReadWriteSelectorHandler) handler;

						if (sk.isReadable() && sk.isReadable())
						{
							rwHandler.handleRead();
						}

						if (sk.isValid() && sk.isWritable())
						{
							// It is read to write
							rwHandler.handleWrite();
						}
					}
				}
				catch (Throwable t)
				{
					// Eigentlich duerfen hier keine Exceptions auftreten
					closeSelectorAndChannels();
					LOGGER.error(null, t);
					return;
				}
			}
		}
	}
}