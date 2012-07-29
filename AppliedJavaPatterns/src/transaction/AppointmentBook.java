package transaction;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class AppointmentBook implements AppointmentTransactionParticipant
{
	/**
     * 
     */
	private static final String TRANSACTION_SERVICE_PREFIX = "transactionParticipant";

	/**
     * 
     */
	private static final String TRANSACTION_HOSTNAME = "localhost";

	/**
     * 
     */
	private static int index = 1;

	/**
     * 
     */
	private Map<Date, Appointment> appointments = new HashMap<>();

	/**
     * 
     */
	private Appointment currentAppointment;

	/**
     * 
     */
	private long currentTransaction;

	/**
     * 
     */
	private String serviceName = TRANSACTION_SERVICE_PREFIX + index++;

	/**
     * 
     */
	private Date updateStartDate;

	/**
	 * Creates a new {@link AppointmentBook} object.
	 */
	public AppointmentBook()
	{
		super();

		try
		{
			UnicastRemoteObject.exportObject(this);
			Naming.rebind(this.serviceName, this);
		}
		catch (Exception exc)
		{
			System.err.println("Error using RMI to register the AppointmentBook " + exc);
		}
	}

	/**
	 * @param appointment {@link Appointment}
	 */
	public void addAppointment(final Appointment appointment)
	{
		if (!this.appointments.containsValue(appointment))
		{
			if (!this.appointments.containsKey(appointment.getStartDate()))
			{
				this.appointments.put(appointment.getStartDate(), appointment);
			}
		}
	}

	/**
	 * @see transaction.AppointmentTransactionParticipant#cancel(long)
	 */
	@Override
	public void cancel(final long transactionID)
	{
		if (this.currentTransaction == transactionID)
		{
			this.currentTransaction = 0;
			this.appointments.remove(this.updateStartDate);
		}
	}

	/**
	 * @param transactionID long
	 * @param participants {@link AppointmentTransactionParticipant}[]
	 * @throws RemoteException Falls was schief geht
	 */
	private void cancelAll(final long transactionID,
							final AppointmentTransactionParticipant[] participants)
		throws RemoteException
	{
		for (AppointmentTransactionParticipant participant : participants)
		{
			participant.cancel(transactionID);
		}
	}

	/**
	 * @param appointment {@link Appointment}
	 * @param possibleDates {@link Date}[]
	 * @param participants {@link AppointmentTransactionParticipant}[]
	 * @param transactionID long
	 * @return boolean
	 */
	public boolean changeAppointment(final Appointment appointment, final Date[] possibleDates,
										final AppointmentTransactionParticipant[] participants,
										final long transactionID)
	{
		try
		{
			for (int i = 0; i < participants.length; i++)
			{
				if (!participants[i].join(transactionID))
				{
					return false;
				}
			}

			for (Date possibleDate : possibleDates)
			{
				if (isDateAvailable(transactionID, appointment, possibleDate, participants))
				{
					try
					{
						commitAll(transactionID, participants);

						return true;
					}
					catch (TransactionException exc)
					{
						exc.printStackTrace();
					}
				}
			}
		}
		catch (RemoteException exc)
		{
			exc.printStackTrace();
		}

		try
		{
			cancelAll(transactionID, participants);
		}
		catch (RemoteException exc)
		{
			exc.printStackTrace();
		}

		return false;
	}

	/**
	 * @see transaction.AppointmentTransactionParticipant#changeDate(long, transaction.Appointment,
	 *      java.util.Date)
	 */
	@Override
	public boolean changeDate(final long transactionID, final Appointment appointment,
								final Date newStartDate) throws TransactionException
	{
		if ((this.appointments.containsValue(appointment))
				&& (!this.appointments.containsKey(newStartDate)))
		{
			this.appointments.put(newStartDate, null);
			this.updateStartDate = newStartDate;
			this.currentAppointment = appointment;

			return true;
		}

		return false;
	}

	/**
	 * @see transaction.AppointmentTransactionParticipant#commit(long)
	 */
	@Override
	public void commit(final long transactionID) throws TransactionException
	{
		if (this.currentTransaction != transactionID)
		{
			throw new TransactionException("Invalid TransactionID");
		}

		removeAppointment(this.currentAppointment);
		this.currentAppointment.setStartDate(this.updateStartDate);
		this.appointments.put(this.updateStartDate, this.currentAppointment);
	}

	/**
	 * @param transactionID long
	 * @param participants {@link AppointmentTransactionParticipant}[]
	 * @throws TransactionException Falls was schief geht
	 * @throws RemoteException Falls was schief geht
	 */
	private void commitAll(final long transactionID,
							final AppointmentTransactionParticipant[] participants)
		throws TransactionException, RemoteException
	{
		for (AppointmentTransactionParticipant participant : participants)
		{
			participant.commit(transactionID);
		}
	}

	/**
	 * @return String
	 */
	public String getUrl()
	{
		return "//" + TRANSACTION_HOSTNAME + "/" + this.serviceName;
	}

	/**
	 * @param transactionID long
	 * @param appointment {@link Appointment}
	 * @param date {@link Date}
	 * @param participants {@link AppointmentTransactionParticipant}[]
	 * @return boolean
	 */
	private boolean isDateAvailable(final long transactionID, final Appointment appointment,
									final Date date,
									final AppointmentTransactionParticipant[] participants)
	{
		try
		{
			for (int i = 0; i < participants.length; i++)
			{
				try
				{
					if (!participants[i].changeDate(transactionID, appointment, date))
					{
						return false;
					}
				}
				catch (TransactionException exc)
				{
					return false;
				}
			}
		}
		catch (RemoteException exc)
		{
			return false;
		}

		return true;
	}

	/**
	 * @see transaction.AppointmentTransactionParticipant#join(long)
	 */
	@Override
	public boolean join(final long transactionID)
	{
		if (this.currentTransaction != 0)
		{
			return false;
		}

		this.currentTransaction = transactionID;

		return true;
	}

	/**
	 * @param appointment {@link Appointment}
	 */
	public void removeAppointment(final Appointment appointment)
	{
		if (this.appointments.containsValue(appointment))
		{
			this.appointments.remove(appointment.getStartDate());
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.serviceName + " " + this.appointments.values().toString();
	}
}
