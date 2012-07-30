package de.freese.nio.server;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Benutzt internen einen {@link ThreadPoolExecutor} mit nur einem einzigen Thread.<br>
 * Die {@link Runnable}s werden nacheinander abgearbeitet.
 * 
 * @author Thomas Freese
 */
public final class ThreadQueue
{
	/**
	 * SPIELEREI !!! Statt pool-x steht ThreadQueue-x im ThreadNamen, aber schoen schauts aus :-)
	 * 
	 * @author Thomas Freese
	 */
	private static class ThreadQueueThreadFactory implements ThreadFactory
	{
		/**
		 *
		 */
		private static final AtomicInteger queueNumber = new AtomicInteger(1);

		/**
		 * 
		 */
		private final ThreadGroup group;

		/**
		 * 
		 */
		private final String namePrefix;

		/**
		 * 
		 */
		private final AtomicInteger threadNumber = new AtomicInteger(1);

		/**
		 * Creates a new {@link ThreadQueueThreadFactory} object.
		 */
		private ThreadQueueThreadFactory()
		{
			SecurityManager s = System.getSecurityManager();

			this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			this.namePrefix = "ThreadQueue-" + queueNumber.getAndIncrement() + "-thread-";
		}

		/**
		 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
		 */
		@Override
		public Thread newThread(final Runnable r)
		{
			Thread t =
					new Thread(this.group, r,
							this.namePrefix + this.threadNumber.getAndIncrement(), 0);

			if (t.isDaemon())
			{
				t.setDaemon(false);
			}

			if (t.getPriority() != Thread.NORM_PRIORITY)
			{
				t.setPriority(Thread.NORM_PRIORITY);
			}

			return t;
		}
	}

	/**
	 *
	 */
	private static final ThreadQueue DEFAULT_QUEUE = newInstance();

	/**
	 * Liefert die default Instantz.
	 * 
	 * @return {@link ThreadQueue}
	 */
	public static ThreadQueue getInstance()
	{
		return DEFAULT_QUEUE;
	}

	/**
	 * Erzeugt eine neue Instanz.
	 * 
	 * @return {@link ThreadQueue}
	 */
	public static ThreadQueue newInstance()
	{
		return new ThreadQueue();
	}

	/**
	 *
	 */
	private final ThreadPoolExecutor executor;

	/**
	 * Creates a new {@link ThreadQueue} object.
	 */
	private ThreadQueue()
	{
		super();

		// this.executor = Executors.newSingleThreadExecutor(new
		// ThreadQueueThreadFactory());
		this.executor =
				new ThreadPoolExecutor(3, 10, 60L, TimeUnit.SECONDS,
						new LinkedBlockingQueue<Runnable>(), new ThreadQueueThreadFactory());
	}

	/**
	 * Haengt einen neuen {@link Runnable} und die Queue.<br>
	 * Dieser wird irgendwann in der Zukunft abgearbeitet.
	 * 
	 * @param command {@link Runnable}
	 */
	public void execute(final Runnable command)
	{
		this.executor.execute(command);
	}

	/**
	 * Liefert die Anzahl der Threads in der Queue.
	 * 
	 * @return long
	 */
	public long getActiveCount()
	{
		return this.executor.getActiveCount();
	}

	/**
	 * Beenden der ThreadQueue nach dem aktuellen Task..
	 */
	public final void shutdown()
	{
		this.executor.shutdown();
	}

	/**
	 * Sofortiges Beenden der ThreadQueue.
	 * 
	 * @return {@link List}
	 */
	public final List<Runnable> shutdownNow()
	{
		return this.executor.shutdownNow();
	}
}
