package visitor;

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
	private static final long serialVersionUID = 2033718482220172524L;

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
	 * @see visitor.ProjectItem#accept(visitor.ProjectVisitor)
	 */
	@Override
	public void accept(final ProjectVisitor v)
	{
		v.visitProject(this);
	}

	/**
	 * @param element {@link ProjectItem}
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
	 * @see visitor.ProjectItem#getProjectItems()
	 */
	@Override
	public List<ProjectItem> getProjectItems()
	{
		return this.projectItems;
	}

	/**
	 * @param element {@link ProjectItem}
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
}
