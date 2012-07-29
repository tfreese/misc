package HOPP;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class CalendarHOPP implements Calendar, java.io.Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 3273946123265916101L;

	/**
     * 
     */
	private static final String PROTOCOL = "rmi://";

	/**
     * 
     */
	private static final String REMOTE_SERVICE = "/calendarimpl";

	/**
     * 
     */
	private static final String HOPP_SERVICE = "calendar";

	/**
     * 
     */
	private static final String DEFAULT_HOST = "localhost";

	/**
     * 
     */
	private Calendar calendar;

	/**
     * 
     */
	private String host;

	/**
	 * Creates a new {@link CalendarHOPP} object.
	 */
	public CalendarHOPP()
	{
		this(DEFAULT_HOST);
	}

	/**
	 * Creates a new {@link CalendarHOPP} object.
	 * 
	 * @param host String
	 */
	public CalendarHOPP(final String host)
	{
		try
		{
			this.host = host;
			String url = PROTOCOL + host + REMOTE_SERVICE;

			this.calendar = (Calendar) Naming.lookup(url);
			Naming.rebind(HOPP_SERVICE, this);
		}
		catch (Exception exc)
		{
			System.err
					.println("Error using RMI to look up the CalendarImpl or register the CalendarHOPP "
							+ exc);
		}
	}

	/**
	 * @see HOPP.Calendar#addAppointment(HOPP.Appointment, java.util.Date)
	 */
	@Override
	public void addAppointment(final Appointment appointment, final Date date)
		throws RemoteException
	{
		this.calendar.addAppointment(appointment, date);
	}

	/**
	 * @see HOPP.Calendar#getAppointments(java.util.Date)
	 */
	@Override
	public List<Appointment> getAppointments(final Date date) throws RemoteException
	{
		return this.calendar.getAppointments(date);
	}

	/**
	 * @see HOPP.Calendar#getHost()
	 */
	@Override
	public String getHost()
	{
		return this.host;
	}
}
