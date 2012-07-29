package session;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * @author Thomas Freese
 */
public class SessionClient
{
	/**
	 * 
	 */
	private static final String SESSION_SERVER_SERVICE_NAME = "sessionServer";

	/**
	 * 
	 */
	private static final String SESSION_SERVER_MACHINE_NAME = "localhost";

	/**
	 * 
	 */
	private long sessionID;

	/**
	 * 
	 */
	private SessionServer sessionServer;

	/**
	 * Creates a new {@link SessionClient} object.
	 */
	public SessionClient()
	{
		try
		{
			String url = "//" + SESSION_SERVER_MACHINE_NAME + "/" + SESSION_SERVER_SERVICE_NAME;

			this.sessionServer = (SessionServer) Naming.lookup(url);
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
		catch (ClassCastException exc)
		{
			// Ignore
		}
	}

	/**
	 * @param address {@link Address}
	 * @throws SessionException Falls was schief geht
	 */
	public void addAddress(final Address address) throws SessionException
	{
		try
		{
			this.sessionServer.addAddress(address, this.sessionID);
		}
		catch (RemoteException exc)
		{
			// Ignore
		}
	}

	/**
	 * @param contact {@link Contact}
	 * @throws SessionException Falls was schief geht
	 */
	public void addContact(final Contact contact) throws SessionException
	{
		try
		{
			this.sessionID = this.sessionServer.addContact(contact, 0);
		}
		catch (RemoteException exc)
		{
			// Ignore
		}
	}

	/**
	 * @throws SessionException Falls was schief geht
	 */
	public void commitChanges() throws SessionException
	{
		try
		{
			this.sessionID = this.sessionServer.finalizeContact(this.sessionID);
		}
		catch (RemoteException exc)
		{
			// Ignore
		}
	}

	/**
	 * @param address {@link Address}
	 * @throws SessionException Falls was schief geht
	 */
	public void removeAddress(final Address address) throws SessionException
	{
		try
		{
			this.sessionServer.removeAddress(address, this.sessionID);
		}
		catch (RemoteException exc)
		{
			// Ignore
		}
	}
}
