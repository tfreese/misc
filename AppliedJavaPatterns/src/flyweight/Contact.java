package flyweight;

import java.io.Serializable;

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
