package decorator;

import java.io.Serializable;

/**
 * @author Thomas Freese
 */
public interface ProjectItem extends Serializable
{
	/**
     * 
     */
	public static final String EOL_STRING = System.getProperty("line.separator");

	/**
	 * @return double
	 */
	public double getTimeRequired();
}
