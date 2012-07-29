package chain;

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
	private static final long serialVersionUID = 2320959661562793459L;

	/**
     * 
     */
	private String details;

	/**
     * 
     */
	private String name;

	/**
     * 
     */
	private Contact owner;

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
	 * @param newDetails String
	 * @param newOwner String
	 */
	public Project(final String newName, final String newDetails, final Contact newOwner)
	{
		super();

		this.name = newName;
		this.owner = newOwner;
		this.details = newDetails;
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
	 * @see chain.ProjectItem#getDetails()
	 */
	@Override
	public String getDetails()
	{
		return this.details;
	}

	/**
	 * @return String
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @see chain.ProjectItem#getOwner()
	 */
	@Override
	public Contact getOwner()
	{
		return this.owner;
	}

	/**
	 * @see chain.ProjectItem#getParent()
	 */
	@Override
	public ProjectItem getParent()
	{
		return null;
	}

	/**
	 * @see chain.ProjectItem#getProjectItems()
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
	 * @param newDetails String
	 */
	public void setDetails(final String newDetails)
	{
		this.details = newDetails;
	}

	/**
	 * @param newName String
	 */
	public void setName(final String newName)
	{
		this.name = newName;
	}

	/**
	 * @param newOwner {@link Contact}
	 */
	public void setOwner(final Contact newOwner)
	{
		this.owner = newOwner;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.name;
	}
}
