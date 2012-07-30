/**
 * Created: 04.04.2012
 */

package de.freese.sonstiges.forkjoin.fibonacci;

import java.util.concurrent.ForkJoinPool;

/**
 * Berechnent den Fibonacci-Wert.
 * 
 * @author Thomas Freese
 */
public class FibonacciCalulator
{
	/**
	 * 
	 */
	private final int n;

	/**
	 * 
	 */
	private final ForkJoinPool forkJoinPool;

	/**
	 * Erstellt ein neues {@link FibonacciCalulator} Object.
	 * 
	 * @param n int
	 * @param forkJoinPool {@link ForkJoinPool}
	 */
	public FibonacciCalulator(final int n, final ForkJoinPool forkJoinPool)
	{
		super();

		this.n = n;
		this.forkJoinPool = forkJoinPool;
	}

	/**
	 * @return long
	 */
	public long calculate()
	{
		FibonacciTask task = new FibonacciTask(this.n);
		Long result = this.forkJoinPool.invoke(task);

		return result.longValue();
	}
}
