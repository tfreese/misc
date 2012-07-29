package decorator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class Task implements ProjectItem
{
	/**
	 *
	 */
	private static final long serialVersionUID = -5769958314728133616L;

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
	 * @param newOwner String
	 * @param newTimeRequired ProjectItem
	 */
	public Task(final String newName, final Contact newOwner, final double newTimeRequired)
	{
		super();

		this.name = newName;
		this.owner = newOwner;
		this.timeRequired = newTimeRequired;
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
	 * @return {@link List}
	 */
	public List<ProjectItem> getProjectItems()
	{
		return this.projectItems;
	}

	/**
	 * @see decorator.ProjectItem#getTimeRequired()
	 */
	@Override
	public double getTimeRequired()
	{
		double totalTime = this.timeRequired;
		Iterator<ProjectItem> items = this.projectItems.iterator();

		while (items.hasNext())
		{
			ProjectItem item = items.next();

			totalTime += item.getTimeRequired();
		}

		return totalTime;
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

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Task: " + this.name;
	}
}
