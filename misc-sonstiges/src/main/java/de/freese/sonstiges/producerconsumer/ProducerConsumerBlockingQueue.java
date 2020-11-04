/**
 *
 */
package de.freese.sonstiges.producerconsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Freese
 */
public class ProducerConsumerBlockingQueue
{
    /**
     * @author Thomas Freese
     */
    private static class Consumer implements Runnable
    {
        /**
        *
        */
        private final int number;

        /**
         *
         */
        private final BlockingQueue<Integer> queue;

        /**
         * Erstellt ein neues {@link Consumer} Objekt.
         *
         * @param queue {@link BlockingQueue}
         * @param number int
         */
        public Consumer(final BlockingQueue<Integer> queue, final int number)
        {
            super();

            this.queue = queue;
            this.number = number;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public synchronized void run()
        {
            while (!Thread.interrupted())
            {
                try
                {
                    Integer value = null;
                    // value = this.queue.take();
                    value = this.queue.poll(5000, TimeUnit.MILLISECONDS);

                    if (value == null)
                    {
                        break;
                    }

                    System.out.printf("%s: Consumer-%d got: %d%n", Thread.currentThread().getName(), this.number, value);

                    Thread.sleep(3000);
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class Producer implements Runnable
    {
        /**
        *
        */
        private final int number;

        /**
         *
         */
        private final BlockingQueue<Integer> queue;

        /**
         * Erstellt ein neues {@link Producer} Objekt.
         *
         * @param queue {@link BlockingQueue}
         * @param number int
         */
        public Producer(final BlockingQueue<Integer> queue, final int number)
        {
            super();

            this.queue = queue;
            this.number = number;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public synchronized void run()
        {
            for (int i = 0; i < 10; i++)
            {
                try
                {
                    this.queue.put(Integer.valueOf(i));
                    // this.queue.offer(Integer.valueOf(i)); // Funktioniert bei SynchronousQueue nicht.

                    System.out.printf("%s: Producer-%d put: %d%n", Thread.currentThread().getName(), this.number, i);

                    Thread.sleep(300);
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);
        // BlockingQueue<Integer> queue = new SynchronousQueue<>();

        Executor executor = Executors.newCachedThreadPool();

        // Producer starten
        for (int i = 0; i < 1; i++)
        {
            executor.execute(new Producer(queue, i + 1));
        }

        Thread.sleep(500);

        // Consumer starten
        for (int i = 0; i < 2; i++)
        {
            executor.execute(new Consumer(queue, i + 1));
        }
    }
}
