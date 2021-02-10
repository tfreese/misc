/**
 * Created: 04.04.2012
 */

package de.freese.sonstiges.forkjoin;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import de.freese.sonstiges.forkjoin.finder.MaxFinder;

/**
 * Beispiel aus JavaMagazin 05/2012.<br>
 * Suche nach grössten Element im Array.
 * 
 * @author Thomas Freese
 */
public class MaxFinderDemo
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		Random random = new Random();

		// Zufallsarray erstellen.
		int[] array = new int[100000000];

		for (int i = 0; i < array.length; i++)
		{
			array[i] = random.nextInt();
		}

		ForkJoinPool forkJoinPool = new ForkJoinPool();
		MaxFinder finder = new MaxFinder(array, 1000, forkJoinPool);
		int result = finder.find();
		System.out.printf("Max = %d, Parallelism = %d", Integer.valueOf(result),
				Integer.valueOf(forkJoinPool.getParallelism()));

		forkJoinPool.shutdown();
	}
}
