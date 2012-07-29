package visitor;

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
	private static final long serialVersionUID = 4726706956819074621L;

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
     * 
     */
	private double timeRequired;

	/**
	 * Creates a new {@link Task} object.
	 */
	public Task()
	{
		super();
	}

	/**
	 * Creates a new {@link Task} object.
	 * 
	 * @param newName String
	 * @param newOwner {@link Contact}
	 * @param newTimeRequired double
	 */
	public Task(final String newName, final Contact newOwner, final double newTimeRequired)
	{
		super();

		this.name = newName;
		this.owner = newOwner;
		this.timeRequired = newTimeRequired;
	}

	/**
	 * @see visitor.ProjectItem#accept(visitor.ProjectVisitor)
	 */
	@Override
	public void accept(final ProjectVisitor v)
	{
		v.visitTask(this);
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
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return {@link Contact}
	 */
	public Contact getOwner()
	{
		return this.owner;
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
	 * @return double
	 */
	public double getTimeRequired()
	{
		return this.timeRequired;
	}

	/**
	 * @param element {@link ProjectItem}
	 */
	public void removeProjectItem(final ProjectItem element)
	{
		this.projectItems.remove(element);
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
	 * @param newTimeRequired double
	 */
	public void setTimeRequired(final double newTimeRequired)
	{
		this.timeRequired = newTimeRequired;
	}
}
