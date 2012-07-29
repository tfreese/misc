package session;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Thomas Freese
 */
public interface SessionServer extends Remote
{
	/**
	 * @param address {@link Address}
	 * @param sessionID long
	 * @return long
	 * @throws RemoteException Falls was schief geht
	 * @throws SessionException Falls was schief geht
	 */
	public long addAddress(Address address, long sessionID)
		throws RemoteException, SessionException;

	/**
	 * @param contact {@link Contact}
	 * @param sessionID long
	 * @return long
	 * @throws RemoteException Falls was schief geht
	 * @throws SessionException Falls was schief geht
	 */
	public long addContact(Contact contact, long sessionID)
		throws RemoteException, SessionException;

	/**
	 * @param sessionID long
	 * @return long
	 * @throws RemoteException Falls was schief geht
	 * @throws SessionException Falls was schief geht
	 */
	public long finalizeContact(long sessionID) throws RemoteException, SessionException;

	/**
	 * @param address {@link Address}
	 * @param sessionID long
	 * @return long
	 * @throws RemoteException Falls was schief geht
	 * @throws SessionException Falls was schief geht
	 */
	public long removeAddress(Address address, long sessionID)
		throws RemoteException, SessionException;
}
