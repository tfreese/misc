// Created: 05.10.2009
/**
 * 05.10.2009
 */
package de.freese.simulationen;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadFactory mit {@link UncaughtExceptionHandler}.
 * 
 * @author Thomas Freese
 */
public class SimulationThreadFactory implements ThreadFactory, Thread.UncaughtExceptionHandler
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
	 * Creates a new {@link SimulationThreadFactory} object.
	 * 
	 * @param name String
	 */
	public SimulationThreadFactory(final String name)
	{
		super();

		SecurityManager s = System.getSecurityManager();

		this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		this.namePrefix = name + "-" + queueNumber.getAndIncrement() + "-thread-";
	}

	/**
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	@Override
	public Thread newThread(final Runnable r)
	{
		Thread t =
				new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0);
		t.setUncaughtExceptionHandler(this);

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

	/**
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread,
	 *      java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(final Thread t, final Throwable e)
	{
		if (e != null)
		{
			e.printStackTrace();
		}
	}
}
