package callback;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * @author Thomas Freese
 */
public class CallbackServerWorkThread implements Runnable
{
	/**
     * 
     */
	private String callbackMachine;

	/**
     * 
     */
	private String callbackObjectName;

	/**
     * 
     */
	private Thread processingThread;

	/**
     * 
     */
	private String projectID;

	/**
	 * Creates a new {@link CallbackServerWorkThread} object.
	 * 
	 * @param newProjectID String
	 * @param newCallbackMachine String
	 * @param newCallbackObjectName String
	 */
	public CallbackServerWorkThread(final String newProjectID, final String newCallbackMachine,
			final String newCallbackObjectName)
	{
		super();

		this.projectID = newProjectID;
		this.callbackMachine = newCallbackMachine;
		this.callbackObjectName = newCallbackObjectName;
		this.processingThread = new Thread(this);
		this.processingThread.start();
	}

	/**
	 * @return {@link Project}
	 */
	private Project getProject()
	{
		return new Project(this.projectID, "Test project");
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		Project result = getProject();

		sendProjectToClient(result);
	}

	/**
	 * @param project {@link Project}
	 */
	private void sendProjectToClient(final Project project)
	{
		try
		{
			String url = "//" + this.callbackMachine + "/" + this.callbackObjectName;
			Object remoteClient = Naming.lookup(url);

			if (remoteClient instanceof CallbackClient)
			{
				((CallbackClient) remoteClient).receiveProject(project);
			}
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
	}
}
