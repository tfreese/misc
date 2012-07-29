package HOPP;

import java.io.Serializable;

/**
 * @author Thomas Freese
 */
public interface Location extends Serializable
{
	/**
	 * @return String
	 */
	public String getLocation();

	/**
	 * @param newLocation String
	 */
	public void setLocation(String newLocation);
}
