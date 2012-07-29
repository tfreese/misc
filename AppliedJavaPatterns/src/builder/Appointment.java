package builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class Appointment
{
	/**
     * 
     */
	public static final String EOL_STRING = System.getProperty("line.separator");

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
	private Date endDate;

	/**
     * 
     */
	private Location location;

	/**
     * 
     */
	private Date startDate;

	/**
	 * Erstellt ein neues {@link Appointment} Object.
	 */
	Appointment()
	{
		super();
	}

	/**
	 * @param attendee {@link adapter.Contact}
	 */
	public void addAttendee(final Contact attendee)
	{
		if (!this.attendees.contains(attendee))
		{
			this.attendees.add(attendee);
		}
	}

	/**
	 * @return {@link List}
	 */
	public List<Contact> getAttendees()
	{
		return this.attendees;
	}

	/**
	 * @return String
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @return {@link Date}
	 */
	public Date getEndDate()
	{
		return this.endDate;
	}

	/**
	 * @return Location
	 */
	public Location getLocation()
	{
		return this.location;
	}

	/**
	 * @return {@link Date}
	 */
	public Date getStartDate()
	{
		return this.startDate;
	}

	/**
	 * @param attendee {@link adapter.Contact}
	 */
	public void removeAttendee(final Contact attendee)
	{
		this.attendees.remove(attendee);
	}

	/**
	 * @param newAttendees {@link List}
	 */
	public void setAttendees(final List<Contact> newAttendees)
	{
		if (newAttendees != null)
		{
			this.attendees = newAttendees;
		}
	}

	/**
	 * @param newDescription String
	 */
	public void setDescription(final String newDescription)
	{
		this.description = newDescription;
	}

	/**
	 * @param newEndDate {@link Date}
	 */
	public void setEndDate(final Date newEndDate)
	{
		this.endDate = newEndDate;
	}

	/**
	 * @param newLocation {@link Date}
	 */
	public void setLocation(final Location newLocation)
	{
		this.location = newLocation;
	}

	/**
	 * @param newStartDate {@link Date}
	 */
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
				+ this.startDate + EOL_STRING + "  End Date: " + this.endDate + EOL_STRING
				+ "  Location: " + this.location + EOL_STRING + "  Attendees: " + this.attendees;
	}
}
