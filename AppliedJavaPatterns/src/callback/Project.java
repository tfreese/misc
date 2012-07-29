package callback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class Project implements ProjectItem
{
	/**
	 *
	 */
	private static final long serialVersionUID = 406857355867635816L;

	/**
     * 
     */
	private String description;

	/**
     * 
     */
	private String name;

	/**
     * 
     */
	private List<ProjectItem> projectItems = new ArrayList<>();

	/**
	 * Creates a new {@link Project} object.
	 */
	public Project()
	{
		super();
	}

	/**
	 * Creates a new {@link Project} object.
	 * 
	 * @param newName String
	 * @param newDescription String
	 */
	public Project(final String newName, final String newDescription)
	{
		super();

		this.name = newName;
		this.description = newDescription;
	}

	/**
	 * @param element ProjectItem
	 */
	public void addProjectItem(final ProjectItem element)
	{
		if (!this.projectItems.contains(element))
		{
			this.projectItems.add(element);
		}
	}

	/**
	 * @return String
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @return String
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @see callback.ProjectItem#getProjectItems()
	 */
	@Override
	public List<ProjectItem> getProjectItems()
	{
		return this.projectItems;
	}

	/**
	 * @param element ProjectItem
	 */
	public void removeProjectItem(final ProjectItem element)
	{
		this.projectItems.remove(element);
	}

	/**
	 * @param newDescription String
	 */
	public void setDescription(final String newDescription)
	{
		this.description = newDescription;
	}

	/**
	 * @param newName String
	 */
	public void setName(final String newName)
	{
		this.name = newName;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.name + ", " + this.description;
	}
}
