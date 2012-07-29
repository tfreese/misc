package successiveupdate;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * @author Thomas Freese
 */
public interface ClientPullServer extends Remote
{
	/**
	 * @param taskID String
	 * @param lastUpdate {@link Date}
	 * @return {@link Task}
	 * @throws RemoteException Falls was schief geht.
	 * @throws UpdateException Falls was schief geht.
	 */
	public Task getTask(String taskID, Date lastUpdate) throws RemoteException, UpdateException;

	/**
	 * @param taskID String
	 * @param updatedTask {@link Task}
	 * @throws RemoteException Falls was schief geht
	 * @throws UpdateException Falls was schief geht
	 */
	public void updateTask(String taskID, Task updatedTask) throws RemoteException, UpdateException;
}
