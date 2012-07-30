package net.led.demo.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class YahooProvider implements Runnable
{

	private Thread feedThread;

	private List symbols;

	private List listeners = new ArrayList();

	public YahooProvider()
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

	private Double parseDouble(String s)
	{
		if (s.startsWith("\""))
		{
			s = s.substring(1, s.length() - 1);
		}
		if (s.endsWith("%"))
		{
			s = s.substring(0, s.length() - 1);
		}

		try
		{
			return Double.valueOf(s);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	/**
	 * Reads data from Yahoo! for each symbol.
	 */
	private void readSymbolData(final String symbol)
	{
		String feedURL = "http://finance.yahoo.com/d/quotes.csv?s=" + symbol + "&f=sl9p4&e=.csv";
		URL url = null;
		try
		{
			url = new URL(feedURL);
		}
		catch (MalformedURLException e1)
		{
			System.out.println("Unable to open connection !");
			return;
		}
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(url.openStream()));
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		if (br == null)
		{
			System.out.println("Cannot read data from feed !");
			return;
		}
		String line = null;
		StringTokenizer st;
		try
		{
			line = br.readLine();
		}
		catch (IOException e2)
		{
			// Ignore
		}

		if (line != null)
		{
			st = new StringTokenizer(line, ",");

			if (st.hasMoreTokens())
			{
				String name = st.nextToken();
				if (name.startsWith("\""))
				{
					name = name.substring(1, name.length() - 1);
				}
				if (!(name.equals(symbol)))
				{
					return;
				}
			}
			else
			{
				return;
			}

			Double last = null;
			if (st.hasMoreTokens())
			{
				last = parseDouble(st.nextToken());
			}

			Double changePercent = null;
			if (st.hasMoreTokens())
			{
				changePercent = parseDouble(st.nextToken());

				// Sometimes the feed sends invalid data (like -9999.00)
				// for change percent
				if (Math.abs(changePercent.doubleValue()) > 50)
				{
					changePercent = new Double(Double.NaN);
				}
			}

			if ((last != null) && (changePercent != null))
			{
				Stock stock = new Stock(symbol, last, changePercent);
				sendStock(stock);
			}
		}
		try
		{
			br.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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

	private void sendStock(final Stock stock)
	{
		synchronized (this.listeners)
		{
			for (Iterator it = this.listeners.iterator(); it.hasNext();)
			{
				((UpdateListener) it.next()).update(stock);
			}
		}
	}

	public void start()
	{
		this.feedThread = new Thread(this, "Yahoo Provider");
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