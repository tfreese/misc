package visitor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Thomas Freese
 */
public interface ProjectItem extends Serializable
{
	/**
	 * @param v {@link ProjectVisitor}
	 */
	public void accept(ProjectVisitor v);

	/**
	 * @return {@link List}
	 */
	public List<ProjectItem> getProjectItems();
}
