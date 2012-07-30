/**
 * 
 */
package de.freese.sonstiges.producerconsumer;

/**
 * @author Thomas Freese
 */
public class ProducerConsumer1
{
	/**
	 * @author Thomas Freese
	 */
	private static class Consumer extends Thread
	{
		/**
		 *
		 */
		private final CubbyHole cubbyhole;

		/**
		 * @param cubbyHole CubbyHole
		 * @param number int
		 */
		public Consumer(final CubbyHole cubbyHole, final int number)
		{
			super("Consumer " + number);

			this.cubbyhole = cubbyHole;
		}

		/**
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			int value = 0;

			for (int i = 0; i < 10; i++)
			{
				value = this.cubbyhole.get();

				System.out.println(getName() + " got: " + value);

				try
				{
					sleep(3000);
				}
				catch (InterruptedException ex)
				{
					// Ignore
				}
			}
		}
	}

	/**
	 * @author Thomas Freese
	 */
	private static class CubbyHole
	{
		/**
		 *
		 */
		private boolean available = false;

		/**
		 *
		 */
		private int contents = 0;

		/**
		 * 
		 */
		public CubbyHole()
		{
			super();
		}

		/**
		 * Liefert Wert.
		 * 
		 * @return int
		 */
		public synchronized int get()
		{
			while (this.available == false)
			{
				try
				{
					wait(); // wait for Producer to put value
				}
				catch (InterruptedException ex)
				{
					// Ignore
				}
			}

			this.available = false;
			notifyAll(); // notify Producer that value has been retrieved

			return this.contents;
		}

		/**
		 * Setzt Wert.
		 * 
		 * @param value int
		 */
		public synchronized void put(final int value)
		{
			while (this.available == true)
			{
				try
				{
					wait(); // wait for Consumer to get value
				}
				catch (InterruptedException ex)
				{
					// Ignore
				}
			}

			this.contents = value;
			this.available = true;

			notifyAll(); // notify Consumer that value has been set
		}
	}

	/**
	 * @author Thomas Freese
	 */
	private static class Producer extends Thread
	{
		/**
		 *
		 */
		private final CubbyHole cubbyhole;

		/**
		 * @param cubbyHole {@link CubbyHole}
		 * @param number int
		 */
		public Producer(final CubbyHole cubbyHole, final int number)
		{
			super("Producer " + number);

			this.cubbyhole = cubbyHole;
		}

		/**
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			for (int i = 0; i < 10; i++)
			{
				this.cubbyhole.put(i);

				System.out.println(getName() + " put: " + i);

				try
				{
					sleep(300);
				}
				catch (InterruptedException ex)
				{
					// Ignore
				}
			}
		}
	}

	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		CubbyHole cubbyHole = new CubbyHole();

		Producer p1 = new Producer(cubbyHole, 1);
		Consumer c1 = new Consumer(cubbyHole, 1);
		Consumer c2 = new Consumer(cubbyHole, 2);

		p1.start();
		c1.start();
		c2.start();
	}
}
