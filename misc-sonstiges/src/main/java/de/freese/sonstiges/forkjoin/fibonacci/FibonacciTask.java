/**
 * Created: 12.05.2012
 */

package de.freese.sonstiges.forkjoin.fibonacci;

import java.util.concurrent.RecursiveTask;

/**
 * {@link RecursiveTask} für Fibonacci Algorithmus.
 *
 * @author Thomas Freese
 */
public class FibonacciTask extends RecursiveTask<Long>
{
    /**
     *
     */
    private static final long serialVersionUID = 67781993370162624L;

    /**
     * Schwellenwert, bei dem die Berechnung sequenziell durchgeführt wird.
     */
    private static final int THRESHOLD = 15;

    /**
     *
     */
    public final int n;

    /**
     * Erstellt ein neues {@link FibonacciTask} Object.
     *
     * @param n int
     */
    public FibonacciTask(final int n)
    {
        super();

        this.n = n;
    }

    /**
     * @see java.util.concurrent.RecursiveTask#compute()
     */
    @Override
    protected Long compute()
    {
        long result = 0;

        if (this.n < THRESHOLD)
        {
            result = FibonacciCalulator.fibonacci(this.n);
        }
        else
        {
            FibonacciTask task1 = new FibonacciTask(this.n - 1);
            FibonacciTask task2 = new FibonacciTask(this.n - 2);
            task2.fork();

            result = task1.compute() + task2.join().longValue();
        }

        return Long.valueOf(result);
    }
}
