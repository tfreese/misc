package callback;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Thomas Freese
 */
public interface CallbackClient extends Remote
{
	/**
	 * @param project Project
	 * @throws RemoteException Falls was schief geht
	 */
	public void receiveProject(Project project) throws RemoteException;
}
