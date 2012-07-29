package successiveupdate;

import java.rmi.RemoteException;

/**
 * @author Thomas Freese
 */
public class ClientPullRequester implements Runnable
{
	/**
     * 
     */
	private static final int DEFAULT_POLLING_INTERVAL = 10000;

	/**
     * 
     */
	private Task currentTask = new TaskImpl();

	/**
     * 
     */
	private PullClient parent;

	/**
     * 
     */
	private int pollingInterval = DEFAULT_POLLING_INTERVAL;

	/**
     * 
     */
	private Thread processingThread;

	/**
     * 
     */
	private boolean shutdown;

	/**
     * 
     */
	private String taskID;

	/**
     * 
     */
	private ClientPullServer updateServer;

	/**
	 * Creates a new {@link ClientPullRequester} object.
	 * 
	 * @param newParent {@link PullClient}
	 * @param newUpdateServer {@link ClientPullServer}
	 * @param newTaskID String
	 */
	public ClientPullRequester(final PullClient newParent, final ClientPullServer newUpdateServer,
			final String newTaskID)
	{
		super();

		this.parent = newParent;
		this.taskID = newTaskID;
		this.updateServer = newUpdateServer;
		this.processingThread = new Thread(this);
		this.processingThread.start();
	}

	/**
	 * @return int
	 */
	public int getPollingInterval()
	{
		return this.pollingInterval;
	}

	/**
	 * @return boolean
	 */
	public boolean isShutdown()
	{
		return this.shutdown;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		while (!isShutdown())
		{
			try
			{
				this.currentTask =
						this.updateServer.getTask(this.taskID, this.currentTask.getLastEditDate());
				this.parent.setUpdatedTask(this.currentTask);
			}
			catch (RemoteException exc)
			{
				exc.printStackTrace();
			}
			catch (UpdateException exc)
			{
				System.out.println("  " + this.parent + ": " + exc.getMessage());
			}

			try
			{
				Thread.sleep(this.pollingInterval);
			}
			catch (InterruptedException exc)
			{
				// Ignore
			}
		}
	}

	/**
	 * @param newPollingInterval int
	 */
	public void setPollingInterval(final int newPollingInterval)
	{
		this.pollingInterval = newPollingInterval;
	}

	/**
	 * @param isShutdown boolean
	 */
	public void setShutdown(final boolean isShutdown)
	{
		this.shutdown = isShutdown;
	}

	/**
	 * @param changedTask {@link Task}
	 */
	public void updateTask(final Task changedTask)
	{
		try
		{
			this.updateServer.updateTask(this.taskID, changedTask);
		}
		catch (RemoteException exc)
		{
			exc.printStackTrace();
		}
		catch (UpdateException exc)
		{
			System.out.println("  " + this.parent + ": " + exc.getMessage());
		}
	}
}
