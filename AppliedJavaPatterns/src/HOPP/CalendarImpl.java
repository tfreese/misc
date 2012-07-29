package HOPP;

import java.io.File;
import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class CalendarImpl implements Calendar
{
	/**
     * 
     */
	private static final String REMOTE_SERVICE = "calendarimpl";

	/**
     * 
     */
	private static final String DEFAULT_FILE_NAME = "calendar.ser";

	/**
     * 
     */
	private Map<Long, List<Appointment>> appointmentCalendar = new HashMap<>();

	/**
	 * Creates a new {@link CalendarImpl} object.
	 */
	public CalendarImpl()
	{
		this(DEFAULT_FILE_NAME);
	}

	/**
	 * Creates a new {@link CalendarImpl} object.
	 * 
	 * @param filename String
	 */
	@SuppressWarnings("unchecked")
	public CalendarImpl(final String filename)
	{
		File inputFile = new File(filename);

		this.appointmentCalendar =
				(HashMap<Long, List<Appointment>>) FileLoader.loadData(inputFile);

		if (this.appointmentCalendar == null)
		{
			this.appointmentCalendar = new HashMap<>();
		}

		try
		{
			UnicastRemoteObject.exportObject(this);
			Naming.rebind(REMOTE_SERVICE, this);
		}
		catch (Exception exc)
		{
			System.err.println("Error using RMI to register the CalendarImpl " + exc);
		}
	}

	/**
	 * @see HOPP.Calendar#addAppointment(HOPP.Appointment, java.util.Date)
	 */
	@Override
	public void addAppointment(final Appointment appointment, final Date date)
	{
		Long appointmentKey = Long.valueOf(date.getTime());

		if (this.appointmentCalendar.containsKey(appointmentKey))
		{
			List<Appointment> appointments = this.appointmentCalendar.get(appointmentKey);

			appointments.add(appointment);
		}
		else
		{
			List<Appointment> appointments = new ArrayList<>();

			appointments.add(appointment);
			this.appointmentCalendar.put(appointmentKey, appointments);
		}
	}

	/**
	 * @see HOPP.Calendar#getAppointments(java.util.Date)
	 */
	@Override
	public List<Appointment> getAppointments(final Date date)
	{
		List<Appointment> returnValue = null;
		Long appointmentKey = new Long(date.getTime());

		if (this.appointmentCalendar.containsKey(appointmentKey))
		{
			returnValue = this.appointmentCalendar.get(appointmentKey);
		}

		return returnValue;
	}

	/**
	 * @see HOPP.Calendar#getHost()
	 */
	@Override
	public String getHost()
	{
		return "";
	}
}
