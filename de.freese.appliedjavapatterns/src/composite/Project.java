package composite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class Project implements ProjectItem
{
	/**
	 *
	 */
	private static final long serialVersionUID = -5401400332464969269L;

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
	 * @param newName ProjectItem
	 * @param newDescription ProjectItem
	 */
	public Project(final String newName, final String newDescription)
	{
		super();

		this.name = newName;
		this.description = newDescription;
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
		double totalTime = 0;
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
