package session;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Thomas Freese
 */
public class SessionServerImpl implements SessionServer
{
	/**
	 * 
	 */
	private static final String SESSION_SERVER_SERVICE_NAME = "sessionServer";

	/**
	 * Creates a new {@link SessionServerImpl} object.
	 */
	public SessionServerImpl()
	{
		super();

		try
		{
			UnicastRemoteObject.exportObject(this);
			Naming.rebind(SESSION_SERVER_SERVICE_NAME, this);
		}
		catch (Exception exc)
		{
			System.err.println("Error using RMI to register the SessionServerImpl " + exc);
		}
	}

	/**
	 * @see session.SessionServer#addAddress(session.Address, long)
	 */
	@Override
	public long addAddress(final Address address, final long sessionID) throws SessionException
	{
		return SessionServerDelegate.addAddress(address, sessionID);
	}

	/**
	 * @see session.SessionServer#addContact(session.Contact, long)
	 */
	@Override
	public long addContact(final Contact contact, final long sessionID) throws SessionException
	{
		return SessionServerDelegate.addContact(contact, sessionID);
	}

	/**
	 * @see session.SessionServer#finalizeContact(long)
	 */
	@Override
	public long finalizeContact(final long sessionID) throws SessionException
	{
		return SessionServerDelegate.finalizeContact(sessionID);
	}

	/**
	 * @see session.SessionServer#removeAddress(session.Address, long)
	 */
	@Override
	public long removeAddress(final Address address, final long sessionID) throws SessionException
	{
		return SessionServerDelegate.removeAddress(address, sessionID);
	}
}
