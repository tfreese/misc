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
        int n = 50;
        long result = 0;

        result = FibonacciCalulator.fibonacci(n);
        System.out.printf("n = %d, Result = %d%n", n, result);

        // ForkJoin braucht signifikant länger durch das erzeugen der vielen Tasks, steuerbar über FibonacciTask.THRESHOLD.
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        FibonacciCalulator calulator = new FibonacciCalulator(n, forkJoinPool);
        result = calulator.calculate();
        System.out.printf("n = %d, Result = %d, Parallelism = %d%n", n, result, Integer.valueOf(forkJoinPool.getParallelism()));

        forkJoinPool.shutdown();
    }
}
