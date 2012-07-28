/**
 * 
 */
package de.freese.sonstiges.producerconsumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Freese
 */
public class ProducerConsumer2
{
	/**
	 * @author Thomas Freese
	 */
	private static class Consumer implements Runnable
	{
		/**
		 * 
		 */
		private final String name;

		/**
		 * 
		 */
		private final BlockingQueue<Integer> queue;

		/**
		 * Erstellt ein neues {@link Consumer} Objekt.
		 * 
		 * @param name String
		 * @param queue {@link BlockingQueue}
		 */
		public Consumer(final String name, final BlockingQueue<Integer> queue)
		{
			super();

			this.name = name;
			this.queue = queue;
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
					Integer value = null;
					// value = this.queue.take();
					value = this.queue.poll(5000, TimeUnit.MILLISECONDS);

					if (value == null)
					{
						break;
					}

					System.out.println(this.name + ": takes " + value.intValue());
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
		private final String name;

		/**
		 * 
		 */
		private final BlockingQueue<Integer> queue;

		/**
		 * Erstellt ein neues {@link Producer} Objekt.
		 * 
		 * @param name String
		 * @param queue {@link BlockingQueue}
		 */
		public Producer(final String name, final BlockingQueue<Integer> queue)
		{
			super();

			this.name = name;
			this.queue = queue;
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
					System.out.println(this.name + ": puts " + i);
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
	 */
	public static void main(final String[] args)
	{
		BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
		// BlockingQueue<Integer> queue = new SynchronousQueue<Integer>();

		String name = null;

		// Producer starten
		for (int i = 0; i < 1; i++)
		{
			name = "Producer " + (i + 1);
			Thread t = new Thread(new Producer(name, queue), name);
			t.start();
		}

		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}

		// Consumer starten
		for (int i = 0; i < 2; i++)
		{
			name = "Consumer " + (i + 1);
			Thread t = new Thread(new Consumer(name, queue), name);
			t.start();
		}
	}
}
