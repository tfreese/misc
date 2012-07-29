package chain;

import java.io.Serializable;
import java.util.List;

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
	 * @return String
	 */
	public String getDetails();

	/**
	 * @return {@link Contact}
	 */
	public Contact getOwner();

	/**
	 * @return {@link ProjectItem}
	 */
	public ProjectItem getParent();

	/**
	 * @return {@link List}
	 */
	public List<ProjectItem> getProjectItems();
}
