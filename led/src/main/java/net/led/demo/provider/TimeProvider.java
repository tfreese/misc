package net.led.demo.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class TimeProvider implements Runnable
{
	private Thread feedThread;

	private List listeners = new ArrayList();

	private List symbols;

	public TimeProvider()
	{
		this.symbols = new ArrayList();
	}

	/**
	 * Adds a symbol to be updated.
	 */
	public void addSymbol(final String symbol)
	{
		synchronized (this.symbols)
		{
			this.symbols.add(symbol);
		}
	}

	public void addUpdateListener(final UpdateListener listener)
	{
		synchronized (this.listeners)
		{
			if (!this.listeners.contains(listener))
			{
				this.listeners.add(listener);
			}
		}
	}

	/**
	 * Reads data from Yahoo! for each symbol.
	 */
	private void readSymbolData(final String symbol)
	{
		sendTime(new Date());
	}

	/**
	 * Removes all symbols.
	 */
	public void removeAllElements()
	{
		synchronized (this.symbols)
		{
			this.symbols.clear();
		}
	}

	/**
	 * Removes a specific symbol from the provider's list of symbols to updated.
	 */
	public void removeSymbol(final String symbol)
	{
		synchronized (this.symbols)
		{
			this.symbols.remove(symbol);
		}
	}

	public void removeUpdateListener(final UpdateListener listener)
	{
		synchronized (this.listeners)
		{
			this.listeners.remove(listener);
		}
	}

	@Override
	public void run()
	{
		Thread currentThread = Thread.currentThread();

		int index = 0;
		String symbol;
		long time;

		while (currentThread == this.feedThread)
		{
			time = System.currentTimeMillis();

			synchronized (this.symbols)
			{
				if (index >= this.symbols.size())
				{
					index = 0;
				}

				if (this.symbols.isEmpty())
				{
					symbol = null;
				}
				else
				{
					symbol = (String) this.symbols.get(index++);
				}
			}

			if (symbol != null)
			{
				readSymbolData(symbol);
			}

			time = 1000 - (System.currentTimeMillis() - time);

			if (time > 10)
			{
				try
				{
					Thread.sleep(time);
				}
				catch (InterruptedException e1)
				{
					// Ignore
				}
			}
		}
	}

	private void sendTime(final Date newValue)
	{
		synchronized (this.listeners)
		{
			for (Iterator it = this.listeners.iterator(); it.hasNext();)
			{
				((UpdateListener) it.next()).update(newValue);
			}
		}
	}

	public void start()
	{
		this.feedThread = new Thread(this, "Time Provider");
		this.feedThread.start();
	}

	public void stop()
	{
		Thread t = this.feedThread;
		this.feedThread = null;

		if (t != null)
		{
			t.interrupt();
		}
	}
}
