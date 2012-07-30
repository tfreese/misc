package chain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class Task implements ProjectItem
{
	/**
	 *
	 */
	private static final long serialVersionUID = 3709410254980826553L;

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
	private ProjectItem parent;

	/**
     * 
     */
	private boolean primaryTask;

	/**
     * 
     */
	private List<ProjectItem> projectItems = new ArrayList<>();

	/**
	 * Creates a new {@link Task} object.
	 * 
	 * @param newParent {@link ProjectItem}
	 */
	public Task(final ProjectItem newParent)
	{
		this(newParent, "", "", null, false);
	}

	/**
	 * Creates a new {@link Task} object.
	 * 
	 * @param newParent {@link ProjectItem}
	 * @param newName String
	 * @param newDetails String
	 * @param newOwner {@link Contact}
	 * @param newPrimaryTask boolean
	 */
	public Task(final ProjectItem newParent, final String newName, final String newDetails,
			final Contact newOwner, final boolean newPrimaryTask)
	{
		super();

		this.parent = newParent;
		this.name = newName;
		this.owner = newOwner;
		this.details = newDetails;
		this.primaryTask = newPrimaryTask;
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
		if (this.primaryTask)
		{
			return this.details;
		}

		return this.parent.getDetails() + EOL_STRING + "\t" + this.details;
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
		if (this.owner == null)
		{
			return this.parent.getOwner();
		}

		return this.owner;
	}

	/**
	 * @see chain.ProjectItem#getParent()
	 */
	@Override
	public ProjectItem getParent()
	{
		return this.parent;
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
	 * @return boolean
	 */
	public boolean isPrimaryTask()
	{
		return this.primaryTask;
	}

	/**
	 * @param element ProjectItem
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
	 * @param newParent {@link ProjectItem}
	 */
	public void setParent(final ProjectItem newParent)
	{
		this.parent = newParent;
	}

	/**
	 * @param newPrimaryTask boolean
	 */
	public void setPrimaryTask(final boolean newPrimaryTask)
	{
		this.primaryTask = newPrimaryTask;
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
