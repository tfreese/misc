package transaction;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Freese
 */
public interface Appointment extends Serializable
{
	/**
     * 
     */
	public static final String EOL_STRING = System.getProperty("line.separator");

	/**
	 * @param attendee {@link Contact}
	 */
	public void addAttendee(Contact attendee);

	/**
	 * @return {@link List}
	 */
	public List<Contact> getAttendees();

	/**
	 * @return String
	 */
	public String getDescription();

	/**
	 * @return {@link Location}
	 */
	public Location getLocation();

	/**
	 * @return {@link Date}
	 */
	public Date getStartDate();

	/**
	 * @param attendee {@link Contact}
	 */
	public void removeAttendee(Contact attendee);

	/**
	 * @param newAttendees {@link List}
	 */
	public void setAttendees(List<Contact> newAttendees);

	/**
	 * @param newDescription String
	 */
	public void setDescription(String newDescription);

	/**
	 * @param newLocation {@link Location}
	 */
	public void setLocation(Location newLocation);

	/**
	 * @param newStartDate {@link Date}
	 */
	public void setStartDate(Date newStartDate);
}
