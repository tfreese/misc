package callback;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Thomas Freese
 */
public class CallbackClientImpl implements CallbackClient
{
	/**
     * 
     */
	private static final String CALLBACK_CLIENT_SERVICE_NAME = "callbackClient";

	/**
     * 
     */
	private static final String CALLBACK_SERVER_SERVICE_NAME = "callbackServer";

	/**
     * 
     */
	private static final String CALLBACK_SERVER_MACHINE_NAME = "localhost";

	/**
     * 
     */
	private boolean projectAvailable;

	/**
     * 
     */
	private Project requestedProject;

	/**
	 * Creates a new {@link CallbackClientImpl} object.
	 */
	public CallbackClientImpl()
	{
		super();

		try
		{
			UnicastRemoteObject.exportObject(this);
			Naming.rebind(CALLBACK_CLIENT_SERVICE_NAME, this);
		}
		catch (Exception exc)
		{
			System.err.println("Error using RMI to register the CallbackClientImpl " + exc);
		}
	}

	/**
	 * @return {@link Project}
	 */
	public Project getProject()
	{
		return this.requestedProject;
	}

	/**
	 * @return boolean
	 */
	public boolean isProjectAvailable()
	{
		return this.projectAvailable;
	}

	/**
	 * @see callback.CallbackClient#receiveProject(callback.Project)
	 */
	@Override
	public void receiveProject(final Project project)
	{
		this.requestedProject = project;
		this.projectAvailable = true;
	}

	/**
	 * @param projectName String
	 */
	public void requestProject(final String projectName)
	{
		try
		{
			String url = "//" + CALLBACK_SERVER_MACHINE_NAME + "/" + CALLBACK_SERVER_SERVICE_NAME;
			Object remoteServer = Naming.lookup(url);

			if (remoteServer instanceof CallbackServer)
			{
				((CallbackServer) remoteServer).getProject(projectName, InetAddress.getLocalHost()
						.getHostName(), CALLBACK_CLIENT_SERVICE_NAME);
			}

			this.projectAvailable = false;
		}
		catch (RemoteException exc)
		{
			// Ignore
		}
		catch (NotBoundException exc)
		{
			// Ignore
		}
		catch (MalformedURLException exc)
		{
			// Ignore
		}
		catch (UnknownHostException exc)
		{
			// Ignore
		}
	}
}
