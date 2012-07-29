package workerthread;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Thomas Freese
 */
public interface ServerDataStore extends Remote
{
	/**
	 * @param addressID long
	 * @return {@link Address}
	 * @throws RemoteException Falls was schief geht.
	 */
	public Address retrieveAddress(long addressID) throws RemoteException;

	/**
	 * @param contactID long
	 * @return {@link Contact}
	 * @throws RemoteException Falls was schief geht.
	 */
	public Contact retrieveContact(long contactID) throws RemoteException;
}
