package templatemethod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class Task extends ProjectItem
{
	/**
	 *
	 */
	private static final long serialVersionUID = -2691354611132934689L;

	/**
	 * 
	 */
	private List<ProjectItem> projectItems = new ArrayList<>();

	/**
	 * 
	 */
	private double taskTimeRequired;

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
	 * @param newDescription String
	 * @param newTaskTimeRequired double
	 * @param newRate double
	 */
	public Task(final String newName, final String newDescription,
			final double newTaskTimeRequired, final double newRate)
	{
		super(newName, newDescription, newRate);

		this.taskTimeRequired = newTaskTimeRequired;
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
	 * @see templatemethod.ProjectItem#getMaterialsCost()
	 */
	@Override
	public double getMaterialsCost()
	{
		double totalCost = 0;
		Iterator<ProjectItem> items = getProjectItemIterator();

		while (items.hasNext())
		{
			totalCost += items.next().getMaterialsCost();
		}

		return totalCost;
	}

	/**
	 * @return {@link Iterator}
	 */
	public Iterator<ProjectItem> getProjectItemIterator()
	{
		return this.projectItems.iterator();
	}

	/**
	 * @return double
	 */
	public double getTaskTimeRequired()
	{
		return this.taskTimeRequired;
	}

	/**
	 * @see templatemethod.ProjectItem#getTimeRequired()
	 */
	@Override
	public double getTimeRequired()
	{
		double totalTime = this.taskTimeRequired;
		Iterator<ProjectItem> items = getProjectItemIterator();

		while (items.hasNext())
		{
			totalTime += items.next().getTimeRequired();
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
	 * @param newTaskTimeRequired double
	 */
	public void setTaskTimeRequired(final double newTaskTimeRequired)
	{
		this.taskTimeRequired = newTaskTimeRequired;
	}
}
