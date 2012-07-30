package builder;

import java.util.Date;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class AppointmentBuilder
{
	/**
     * 
     */
	public static final int START_DATE_REQUIRED = 1;

	/**
     * 
     */
	public static final int END_DATE_REQUIRED = 2;

	/**
     * 
     */
	public static final int DESCRIPTION_REQUIRED = 4;

	/**
     * 
     */
	public static final int ATTENDEE_REQUIRED = 8;

	/**
     * 
     */
	public static final int LOCATION_REQUIRED = 16;

	/**
     * 
     */
	protected Appointment appointment;

	/**
     * 
     */
	protected int requiredElements;

	/**
     * 
     */
	public void buildAppointment()
	{
		this.appointment = new Appointment();
	}

	/**
	 * @param attendees {@link List}
	 */
	public void buildAttendees(final List<Contact> attendees)
	{
		if ((attendees != null) && (!attendees.isEmpty()))
		{
			this.appointment.setAttendees(attendees);
		}
	}

	/**
	 * @param startDate {@link Date}
	 * @param endDate {@link Date}
	 */
	public void buildDates(final Date startDate, final Date endDate)
	{
		Date currentDate = new Date();

		if ((startDate != null) && (startDate.after(currentDate)))
		{
			this.appointment.setStartDate(startDate);
		}

		if ((endDate != null) && (endDate.after(startDate)))
		{
			this.appointment.setEndDate(endDate);
		}
	}

	/**
	 * @param newDescription String
	 */
	public void buildDescription(final String newDescription)
	{
		this.appointment.setDescription(newDescription);
	}

	/**
	 * @param newLocation {@link Location}
	 */
	public void buildLocation(final Location newLocation)
	{
		if (newLocation != null)
		{
			this.appointment.setLocation(newLocation);
		}
	}

	/**
	 * @return {@link Appointment}
	 * @throws InformationRequiredException Falls was schief geht.
	 */
	public Appointment getAppointment() throws InformationRequiredException
	{
		this.requiredElements = 0;

		if (this.appointment.getStartDate() == null)
		{
			this.requiredElements += START_DATE_REQUIRED;
		}

		if (this.appointment.getLocation() == null)
		{
			this.requiredElements += LOCATION_REQUIRED;
		}

		if (this.appointment.getAttendees().isEmpty())
		{
			this.requiredElements += ATTENDEE_REQUIRED;
		}

		if (this.requiredElements > 0)
		{
			throw new InformationRequiredException(this.requiredElements);
		}

		return this.appointment;
	}

	/**
	 * @return int
	 */
	public int getRequiredElements()
	{
		return this.requiredElements;
	}
}
