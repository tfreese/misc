/**
 * Created: 04.04.2012
 */

package de.freese.sonstiges.forkjoin.fibonacci;

import java.util.HashMap;
import java.util.Map;
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
    private static final Map<Integer, Long> CACHE = new HashMap<>();

    /**
     * Algorithmus.
     *
     * @param n int
     * @return long
     */
    public static long fibonacci(final int n)
    {
        if (n <= 1)
        {
            return n;
        }

        // return fibonacci(n - 1) + fibonacci(n - 2);

        synchronized (CACHE)
        {
            return CACHE.computeIfAbsent(n, key -> {
                System.out.println(key);
                return fibonacci(key - 1) + fibonacci(key - 2);
            });
        }

        // Long result = CACHE.get(n);
        //
        // if (result == null)
        // {
        // System.out.println(n);
        //
        // result = fibonacci(n - 1) + fibonacci(n - 2);
        // CACHE.put(n, result);
        // }
        //
        // return result;
    }

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
