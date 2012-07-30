package HOPP;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Freese
 */
public interface Calendar extends Remote
{
	/**
	 * @param appointment {@link Appointment}
	 * @param date {@link Date}
	 * @throws RemoteException Falls was schief geht
	 */
	public void addAppointment(Appointment appointment, Date date) throws RemoteException;

	/**
	 * @param date {@link Date}
	 * @return {@link List}
	 * @throws RemoteException Falls was schief geht.
	 */
	public List<Appointment> getAppointments(Date date) throws RemoteException;

	/**
	 * @return String
	 * @throws RemoteException Falls was schief geht.
	 */
	public String getHost() throws RemoteException;
}
