package composite;

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
	private static final long serialVersionUID = -4149388230842334396L;

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
	 * @param newDetails String
	 * @param newOwner {@link Contact}
	 * @param newTimeRequired double
	 */
	public Task(final String newName, final String newDetails, final Contact newOwner,
			final double newTimeRequired)
	{
		super();

		this.name = newName;
		this.details = newDetails;
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
	 * @see composite.ProjectItem#getTimeRequired()
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
	 * @param newTimeRequired double
	 */
	public void setTimeRequired(final double newTimeRequired)
	{
		this.timeRequired = newTimeRequired;
	}
}
