package router;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Thomas Freese
 */
public interface OutputChannel extends Remote
{
	/**
	 * @param message {@link Message}
	 * @throws RemoteException Falls was schief geht.
	 */
	public void sendMessage(Message message) throws RemoteException;
}
