package visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class DependentTask extends Task
{
	/**
	 *
	 */
	private static final long serialVersionUID = 2833237220407852859L;

	/**
     * 
     */
	private double dependencyWeightingFactor;

	/**
     * 
     */
	private List<Task> dependentTasks = new ArrayList<>();

	/**
	 * Creates a new {@link DependentTask} object.
	 */
	public DependentTask()
	{
		super();
	}

	/**
	 * Creates a new {@link DependentTask} object.
	 * 
	 * @param newName String
	 * @param newOwner {@link Contact}
	 * @param newTimeRequired double
	 * @param newWeightingFactor double
	 */
	public DependentTask(final String newName, final Contact newOwner,
			final double newTimeRequired, final double newWeightingFactor)
	{
		super(newName, newOwner, newTimeRequired);

		this.dependencyWeightingFactor = newWeightingFactor;
	}

	/**
	 * @see visitor.Task#accept(visitor.ProjectVisitor)
	 */
	@Override
	public void accept(final ProjectVisitor v)
	{
		v.visitDependentTask(this);
	}

	/**
	 * @param element {@link Task}
	 */
	public void addDependentTask(final Task element)
	{
		if (!this.dependentTasks.contains(element))
		{
			this.dependentTasks.add(element);
		}
	}

	/**
	 * @return double
	 */
	public double getDependencyWeightingFactor()
	{
		return this.dependencyWeightingFactor;
	}

	/**
	 * @return {@link List}
	 */
	public List<Task> getDependentTasks()
	{
		return this.dependentTasks;
	}

	/**
	 * @param element {@link Task}
	 */
	public void removeDependentTask(final Task element)
	{
		this.dependentTasks.remove(element);
	}

	/**
	 * @param newFactor double
	 */
	public void setDependencyWeightingFactor(final double newFactor)
	{
		this.dependencyWeightingFactor = newFactor;
	}
}
