package transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class AppointmentImpl implements Appointment
{
	/**
	 *
	 */
	private static final long serialVersionUID = 6865001438213331856L;

	/**
     * 
     */
	private List<Contact> attendees = new ArrayList<>();

	/**
     * 
     */
	private String description;

	/**
     * 
     */
	private Location location;

	/**
     * 
     */
	private Date startDate;

	/**
	 * Creates a new {@link AppointmentImpl} object.
	 * 
	 * @param newDescription String
	 * @param newAttendees {@link List}
	 * @param newLocation {@link Location}
	 * @param newStartDate {@link Date}
	 */
	public AppointmentImpl(final String newDescription, final List<Contact> newAttendees,
			final Location newLocation, final Date newStartDate)
	{
		super();

		this.description = newDescription;
		this.attendees = newAttendees;
		this.location = newLocation;
		this.startDate = newStartDate;
	}

	/**
	 * @see transaction.Appointment#addAttendee(transaction.Contact)
	 */
	@Override
	public void addAttendee(final Contact attendee)
	{
		if (!this.attendees.contains(attendee))
		{
			this.attendees.add(attendee);
		}
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof AppointmentImpl))
		{
			return false;
		}

		if (object.hashCode() != hashCode())
		{
			return false;
		}

		return true;
	}

	/**
	 * @see transaction.Appointment#getAttendees()
	 */
	@Override
	public List<Contact> getAttendees()
	{
		return this.attendees;
	}

	/**
	 * @see transaction.Appointment#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return this.description;
	}

	@Override
	public Location getLocation()
	{
		return this.location;
	}

	/**
	 * @see transaction.Appointment#getStartDate()
	 */
	@Override
	public Date getStartDate()
	{
		return this.startDate;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return this.description.hashCode() ^ this.startDate.hashCode();
	}

	/**
	 * @see transaction.Appointment#removeAttendee(transaction.Contact)
	 */
	@Override
	public void removeAttendee(final Contact attendee)
	{
		this.attendees.remove(attendee);
	}

	/**
	 * @see transaction.Appointment#setAttendees(java.util.List)
	 */
	@Override
	public void setAttendees(final List<Contact> newAttendees)
	{
		if (newAttendees != null)
		{
			this.attendees = newAttendees;
		}
	}

	/**
	 * @see transaction.Appointment#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(final String newDescription)
	{
		this.description = newDescription;
	}

	/**
	 * @see transaction.Appointment#setLocation(transaction.Location)
	 */
	@Override
	public void setLocation(final Location newLocation)
	{
		this.location = newLocation;
	}

	/**
	 * @see transaction.Appointment#setStartDate(java.util.Date)
	 */
	@Override
	public void setStartDate(final Date newStartDate)
	{
		this.startDate = newStartDate;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "  Description: " + this.description + EOL_STRING + "  Start Date: "
				+ this.startDate + EOL_STRING + "  Location: " + this.location + EOL_STRING
				+ "  Attendees: " + this.attendees;
	}
}
