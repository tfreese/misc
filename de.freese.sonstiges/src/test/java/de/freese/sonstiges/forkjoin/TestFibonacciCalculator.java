/**
 * Created: 04.04.2012
 */

package de.freese.sonstiges.forkjoin;

import java.util.concurrent.ForkJoinPool;

import de.freese.sonstiges.forkjoin.fibonacci.FibonacciCalulator;

/**
 * Berechnent den Fibonacci-Wert.
 * 
 * @author Thomas Freese
 */
public class TestFibonacciCalculator
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		FibonacciCalulator calulator = new FibonacciCalulator(50, forkJoinPool);
		long result = calulator.calculate();
		System.out.printf("Max = %d, Parallelism = %d", Long.valueOf(result),
				Integer.valueOf(forkJoinPool.getParallelism()));

		forkJoinPool.shutdown();
	}
}
