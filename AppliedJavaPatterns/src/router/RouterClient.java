package router;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Thomas Freese
 */
public class RouterClient implements OutputChannel
{
	/**
	 * 
	 */
	private static final String ROUTER_CLIENT_SERVICE_PREFIX = "routerClient";

	/**
	 * 
	 */
	private static final String ROUTER_SERVER_MACHINE_NAME = "localhost";

	/**
	 * 
	 */
	private static final String ROUTER_SERVER_SERVICE_NAME = "router";

	/**
	 * 
	 */
	private static int clientIndex = 1;

	/**
	 * 
	 */
	private Receiver receiver;

	/**
	 * 
	 */
	private OutputChannel router;

	/**
	 * 
	 */
	private String routerClientServiceName = ROUTER_CLIENT_SERVICE_PREFIX + clientIndex++;

	/**
	 * Creates a new RouterClient object.
	 * 
	 * @param newReceiver {@link Receiver}
	 */
	public RouterClient(final Receiver newReceiver)
	{
		this.receiver = newReceiver;

		try
		{
			UnicastRemoteObject.exportObject(this);
			Naming.rebind(this.routerClientServiceName, this);

			String url = "//" + ROUTER_SERVER_MACHINE_NAME + "/" + ROUTER_SERVER_SERVICE_NAME;

			this.router = (OutputChannel) Naming.lookup(url);
		}
		catch (Exception exc)
		{
			System.err.println("Error using RMI to register the Router " + exc);
		}
	}

	/**
	 * @see router.OutputChannel#sendMessage(router.Message)
	 */
	@Override
	public void sendMessage(final Message message)
	{
		this.receiver.receiveMessage(message);
	}

	/**
	 * @param message {@link Message}
	 */
	public void sendMessageToRouter(final Message message)
	{
		try
		{
			this.router.sendMessage(message);
		}
		catch (RemoteException exc)
		{
			// Ignore
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.routerClientServiceName;
	}
}
