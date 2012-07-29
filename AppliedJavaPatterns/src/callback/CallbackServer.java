package callback;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Thomas Freese
 */
public interface CallbackServer extends Remote
{
	/**
	 * @param projectID String
	 * @param callbackMachine String
	 * @param callbackObjectName String
	 * @throws RemoteException Falls was schief geht.
	 */
	public void getProject(String projectID, String callbackMachine, String callbackObjectName)
		throws RemoteException;
}
