package callback;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Thomas Freese
 */
public class CallbackServerImpl implements CallbackServer
{
	/**
     * 
     */
	private static final String CALLBACK_SERVER_SERVICE_NAME = "callbackServer";

	/**
	 * Creates a new {@link CallbackServerImpl} object.
	 */
	public CallbackServerImpl()
	{
		super();

		try
		{
			UnicastRemoteObject.exportObject(this);
			Naming.rebind(CALLBACK_SERVER_SERVICE_NAME, this);
		}
		catch (Exception exc)
		{
			System.err.println("Error using RMI to register the CallbackServerImpl " + exc);
		}
	}

	/**
	 * @see callback.CallbackServer#getProject(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void getProject(final String projectID, final String callbackMachine,
							final String callbackObjectName)
	{
		new CallbackServerDelegate(projectID, callbackMachine, callbackObjectName);
	}
}
