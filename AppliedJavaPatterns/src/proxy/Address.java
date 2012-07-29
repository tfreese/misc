package proxy;

import java.io.Serializable;

/**
 * @author Thomas Freese
 */
public interface Address extends Serializable
{
	/**
     * 
     */
	public static final String EOL_STRING = System.getProperty("line.separator");

	/**
     * 
     */
	public static final String SPACE = " ";

	/**
     * 
     */
	public static final String COMMA = ",";

	/**
	 * @return String
	 */
	public String getAddress();

	/**
	 * @return String
	 */
	public String getCity();

	/**
	 * @return String
	 */
	public String getDescription();

	/**
	 * @return String
	 */
	public String getState();

	/**
	 * @return String
	 */
	public String getStreet();

	/**
	 * @return String
	 */
	public String getType();

	/**
	 * @return String
	 */
	public String getZipCode();

	/**
	 * @param newCity String
	 */
	public void setCity(String newCity);

	/**
	 * @param newDescription String
	 */
	public void setDescription(String newDescription);

	/**
	 * @param newState String
	 */
	public void setState(String newState);

	/**
	 * @param newStreet String
	 */
	public void setStreet(String newStreet);

	/**
	 * @param newType String
	 */
	public void setType(String newType);

	/**
	 * @param newZip String
	 */
	public void setZipCode(String newZip);
}
