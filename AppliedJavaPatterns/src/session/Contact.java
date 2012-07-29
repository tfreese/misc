package session;

import java.io.Serializable;
import java.util.List;

/**
 * @author Thomas Freese
 */
public interface Contact extends Serializable
{
	/**
	 * 
	 */
	public static final String SPACE = " ";

	/**
	 * 
	 */
	public static final String EOL_STRING = System.getProperty("line.separator");

	/**
	 * @param address {@link Address}
	 */
	public void addAddress(Address address);

	/**
	 * @return {@link List}
	 */
	public List<Address> getAddresses();

	/**
	 * @return String
	 */
	public String getFirstName();

	/**
	 * @return String
	 */
	public String getLastName();

	/**
	 * @return String
	 */
	public String getOrganization();

	/**
	 * @return String
	 */
	public String getTitle();

	/**
	 * @param address String
	 */
	public void removeAddress(Address address);

	/**
	 * @param newFirstName String
	 */
	public void setFirstName(String newFirstName);

	/**
	 * @param newLastName String
	 */
	public void setLastName(String newLastName);

	/**
	 * @param newOrganization String
	 */
	public void setOrganization(String newOrganization);

	/**
	 * @param newTitle String
	 */
	public void setTitle(String newTitle);
}
