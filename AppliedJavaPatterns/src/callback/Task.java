package callback;

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
	private static final long serialVersionUID = -8733328314435452498L;

	/**
     * 
     */
	private String name;

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
	 * @param newTimeRequired double
	 */
	public Task(final String newName, final double newTimeRequired)
	{
		super();

		this.name = newName;
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
	 * @see callback.ProjectItem#getProjectItems()
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
	 * @param newTimeRequired double
	 */
	public void setTimeRequired(final double newTimeRequired)
	{
		this.timeRequired = newTimeRequired;
	}
}
