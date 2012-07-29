package callback;

import java.io.Serializable;
import java.util.List;

/**
 * @author Thomas Freese
 */
public interface ProjectItem extends Serializable
{
	/**
	 * @return {@link List}
	 */
	public List<ProjectItem> getProjectItems();
}
