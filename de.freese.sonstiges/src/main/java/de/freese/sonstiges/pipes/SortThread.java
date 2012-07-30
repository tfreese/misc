package de.freese.sonstiges.pipes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Thomas Freese
 */
public class SortThread extends Thread
{
	/**
	 * 
	 */
	private BufferedReader in = null;

	/**
	 * 
	 */
	private PrintWriter out = null;

	/**
	 * Creates a new {@link SortThread} object.
	 * 
	 * @param out {@link PrintWriter}
	 * @param in {@link BufferedReader}
	 */
	public SortThread(final PrintWriter out, final BufferedReader in)
	{
		this.out = out;
		this.in = in;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		int MAXWORDS = 50;

		if ((this.out != null) && (this.in != null))
		{
			try
			{
				String[] listOfWords = new String[MAXWORDS];
				int numwords = 0;

				while ((listOfWords[numwords] = this.in.readLine()) != null)
				{
					numwords++;
				}

				quicksort(listOfWords, 0, numwords - 1);

				for (int i = 0; i < numwords; i++)
				{
					this.out.println(listOfWords[i]);
				}

				this.out.close();
			}
			catch (IOException e)
			{
				System.err.println("SortThread run: " + e);
			}
		}
	}

	/**
	 * @param a String[]
	 * @param lo0 int
	 * @param hi0 int
	 */
	private static void quicksort(final String[] a, final int lo0, final int hi0)
	{
		int lo = lo0;
		int hi = hi0;

		if (lo >= hi)
		{
			return;
		}

		String mid = a[(lo + hi) / 2];

		while (lo < hi)
		{
			while ((lo < hi) && (a[lo].compareTo(mid) < 0))
			{
				lo++;
			}

			while ((lo < hi) && (a[hi].compareTo(mid) > 0))
			{
				hi--;
			}

			if (lo < hi)
			{
				String T = a[lo];
				a[lo] = a[hi];
				a[hi] = T;
				lo++;
				hi--;
			}
		}

		if (hi < lo)
		{
			int T = hi;
			hi = lo;
			lo = T;
		}

		quicksort(a, lo0, lo);
		quicksort(a, (lo == lo0) ? (lo + 1) : lo, hi0);
	}
}
