package transaction;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * @author Thomas Freese
 */
public interface AppointmentTransactionParticipant extends Remote
{
	/**
	 * @param transactionID long
	 * @throws RemoteException Falls was schief geht
	 */
	public void cancel(long transactionID) throws RemoteException;

	/**
	 * @param transactionID long
	 * @param appointment {@link Appointment}
	 * @param newStartDate {@link Date}
	 * @return boolean
	 * @throws TransactionException Falls was schief geht
	 * @throws RemoteException Falls was schief geht
	 */
	public boolean changeDate(long transactionID, Appointment appointment, Date newStartDate)
		throws TransactionException, RemoteException;

	/**
	 * @param transactionID long
	 * @throws TransactionException Falls was schief geht
	 * @throws RemoteException Falls was schief geht
	 */
	public void commit(long transactionID) throws TransactionException, RemoteException;

	/**
	 * @param transactionID long
	 * @return boolean
	 * @throws RemoteException Falls was schief geht
	 */
	public boolean join(long transactionID) throws RemoteException;
}
