package command;

import java.util.Date;

/**
 * @author Thomas Freese
 */
public class Appointment
{
	/**
     * 
     */
	private Contact[] contacts;

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
	private String reason;

	/**
     * 
     */
	private Date startDate;

	/**
	 * Creates a new {@link Appointment} object.
	 * 
	 * @param reason String
	 * @param contacts {@link Contact}[]
	 * @param location {@link Location}
	 * @param startDate {@link Date}
	 * @param endDate {@link Date}
	 */
	public Appointment(final String reason, final Contact[] contacts, final Location location,
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
	 * @return {@link Contact}[]
	 */
	public Contact[] getContacts()
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
	 * @param location {@link Location}
	 */
	public void setLocation(final Location location)
	{
		this.location = location;
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
