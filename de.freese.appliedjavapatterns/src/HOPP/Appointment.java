package HOPP;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class Appointment implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -5992369771125390448L;

	/**
     * 
     */
	private List<Contact> contacts;

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
	 * Creates a new {@link Appointment} object.
	 * 
	 * @param description String
	 * @param contacts {@link List}
	 * @param location {@link Location}
	 * @param startDate {@link Date}
	 * @param endDate {@link Date}
	 */
	public Appointment(final String description, final List<Contact> contacts,
			final Location location, final Date startDate, final Date endDate)
	{
		super();

		this.description = description;
		this.contacts = contacts;
		this.location = location;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/**
	 * @return {@link List}
	 */
	public List<Contact> getContacts()
	{
		return this.contacts;
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
	 * @return {@link Location}
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
	 * @param contacts {@link List}
	 */
	public void setContacts(final List<Contact> contacts)
	{
		this.contacts = contacts;
	}

	/**
	 * @param description String
	 */
	public void setDescription(final String description)
	{
		this.description = description;
	}

	/**
	 * @param endDate {@link Date}
	 */
	public void setEndDate(final Date endDate)
	{
		this.endDate = endDate;
	}

	/**
	 * @param location {@link Location}
	 */
	public void setLocation(final Location location)
	{
		this.location = location;
	}

	/**
	 * @param startDate {@link Date}
	 */
	public void setStartDate(final Date startDate)
	{
		this.startDate = startDate;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Appointment:" + "\n    Description: " + this.description + "\n    Location: "
				+ this.location + "\n    Start: " + this.startDate + "\n    End: " + this.endDate
				+ "\n";
	}
}
