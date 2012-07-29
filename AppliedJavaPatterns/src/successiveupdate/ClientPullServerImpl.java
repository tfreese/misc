package successiveupdate;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

/**
 * @author Thomas Freese
 */
public class ClientPullServerImpl implements ClientPullServer
{
	/**
     * 
     */
	private static final String UPDATE_SERVER_SERVICE_NAME = "updateServer";

	/**
	 * Creates a new {@link ClientPullServerImpl} object.
	 */
	public ClientPullServerImpl()
	{
		super();

		try
		{
			UnicastRemoteObject.exportObject(this);
			Naming.rebind(UPDATE_SERVER_SERVICE_NAME, this);
		}
		catch (Exception exc)
		{
			System.err.println("Error using RMI to register the ClientPullServerImpl " + exc);
		}
	}

	/**
	 * @see successiveupdate.ClientPullServer#getTask(java.lang.String, java.util.Date)
	 */
	@Override
	public Task getTask(final String taskID, final Date lastUpdate) throws UpdateException
	{
		return UpdateServerDelegate.getTask(taskID, lastUpdate);
	}

	/**
	 * @see successiveupdate.ClientPullServer#updateTask(java.lang.String, successiveupdate.Task)
	 */
	@Override
	public void updateTask(final String taskID, final Task updatedTask) throws UpdateException
	{
		UpdateServerDelegate.updateTask(taskID, updatedTask);
	}
}
