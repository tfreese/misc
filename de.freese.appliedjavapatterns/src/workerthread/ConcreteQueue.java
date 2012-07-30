package workerthread;

import java.util.Vector;

/**
 * @author Thomas Freese
 */
public class ConcreteQueue implements Queue
{
	/**
	 * @author Thomas Freese
	 */
	private class Worker implements Runnable
	{
		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			while (!ConcreteQueue.this.shutdown)
			{
				RunnableTask r = take();

				r.execute();
			}
		}
	}

	/**
     * 
     */
	private boolean shutdown;

	/**
     * 
     */
	private Vector<RunnableTask> tasks = null;

	/**
     * 
     */
	private boolean waiting;

	/**
	 * Creates a new ConcreteQueue object.
	 */
	public ConcreteQueue()
	{
		this.tasks = new Vector<>();
		this.waiting = false;
		new Thread(new Worker()).start();
	}

	/**
	 * @see workerthread.Queue#put(workerthread.RunnableTask)
	 */
	@Override
	public void put(final RunnableTask r)
	{
		this.tasks.add(r);

		if (this.waiting)
		{
			synchronized (this)
			{
				notifyAll();
			}
		}
	}

	/**
	 * @param isShutdown boolean
	 */
	public void setShutdown(final boolean isShutdown)
	{
		this.shutdown = isShutdown;
	}

	/**
	 * @see workerthread.Queue#take()
	 */
	@Override
	public RunnableTask take()
	{
		if (this.tasks.isEmpty())
		{
			synchronized (this)
			{
				this.waiting = true;

				try
				{
					wait();
				}
				catch (InterruptedException ie)
				{
					this.waiting = false;
				}
			}
		}

		return this.tasks.remove(0);
	}
}
