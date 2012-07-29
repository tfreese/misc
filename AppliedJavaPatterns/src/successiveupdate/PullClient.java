package successiveupdate;

import java.rmi.Naming;

/**
 * @author Thomas Freese
 */
public class PullClient
{
	/**
     * 
     */
	private static final String UPDATE_SERVER_SERVICE_NAME = "updateServer";

	/**
     * 
     */
	private static final String UPDATE_SERVER_MACHINE_NAME = "localhost";

	/**
     * 
     */
	private String clientName;

	/**
     * 
     */
	private ClientPullRequester requester;

	/**
     * 
     */
	private Task updatedTask;

	/**
     * 
     */
	private ClientPullServer updateServer;

	/**
	 * Creates a new {@link PullClient} object.
	 * 
	 * @param newClientName String
	 */
	public PullClient(final String newClientName)
	{
		this.clientName = newClientName;

		try
		{
			String url = "//" + UPDATE_SERVER_MACHINE_NAME + "/" + UPDATE_SERVER_SERVICE_NAME;

			this.updateServer = (ClientPullServer) Naming.lookup(url);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * @return {@link Task}
	 */
	public Task getUpdatedTask()
	{
		return this.updatedTask;
	}

	/**
	 * @param taskID String
	 */
	public void requestTask(final String taskID)
	{
		this.requester = new ClientPullRequester(this, this.updateServer, taskID);
	}

	/**
	 * @param task {@link Task}
	 */
	public void setUpdatedTask(final Task task)
	{
		this.updatedTask = task;
		System.out.println(this.clientName + ": received updated task: " + task);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.clientName;
	}

	/**
	 * @param task {@link Task}
	 */
	public void updateTask(final Task task)
	{
		this.requester.updateTask(task);
	}
}
