package state;

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
	private static final long serialVersionUID = 7184380688303162340L;

	/**
     * 
     */
	private List<Contact> contacts = null;

	/**
     * 
     */
	private Date endDate = null;

	/**
     * 
     */
	private Location location = null;

	/**
     * 
     */
	private String reason = null;

	/**
     * 
     */
	private Date startDate = null;

	/**
	 * Creates a new {@link Appointment} object.
	 * 
	 * @param reason String
	 * @param contacts {@link List}
	 * @param location {@link Location}
	 * @param startDate {@link Date}
	 * @param endDate {@link Date}
	 */
	public Appointment(final String reason, final List<Contact> contacts, final Location location,
			final Date startDate, final Date endDate)
	{
		super();

		this.reason = reason;
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
	 * @return String
	 */
	public String getReason()
	{
		return this.reason;
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
	 * @param reason String
	 */
	public void setReason(final String reason)
	{
		this.reason = reason;
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
		return "Appointment:" + "\n    Reason: " + this.reason + "\n    Location: " + this.location
				+ "\n    Start: " + this.startDate + "\n    End: " + this.endDate + "\n";
	}
}
