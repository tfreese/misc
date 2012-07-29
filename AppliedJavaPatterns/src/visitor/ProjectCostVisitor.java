package visitor;

/**
 * @author Thomas Freese
 */
public class ProjectCostVisitor implements ProjectVisitor
{
	/**
     * 
     */
	private double hourlyRate;

	/**
     * 
     */
	private double totalCost;

	/**
	 * Erstellt ein neues {@link ProjectCostVisitor} Object.
	 */
	ProjectCostVisitor()
	{
		super();
	}

	/**
	 * @return double
	 */
	public double getHourlyRate()
	{
		return this.hourlyRate;
	}

	/**
	 * @return double
	 */
	public double getTotalCost()
	{
		return this.totalCost;
	}

	/**
     * 
     */
	public void resetTotalCost()
	{
		this.totalCost = 0.0;
	}

	/**
	 * @param rate double
	 */
	public void setHourlyRate(final double rate)
	{
		this.hourlyRate = rate;
	}

	/**
	 * @see visitor.ProjectVisitor#visitDeliverable(visitor.Deliverable)
	 */
	@Override
	public void visitDeliverable(final Deliverable p)
	{
		this.totalCost += p.getMaterialsCost() + p.getProductionCost();
	}

	/**
	 * @see visitor.ProjectVisitor#visitDependentTask(visitor.DependentTask)
	 */
	@Override
	public void visitDependentTask(final DependentTask p)
	{
		double taskCost = p.getTimeRequired() * this.hourlyRate;

		taskCost *= p.getDependencyWeightingFactor();
		this.totalCost += taskCost;
	}

	/**
	 * @see visitor.ProjectVisitor#visitProject(visitor.Project)
	 */
	@Override
	public void visitProject(final Project p)
	{
		// Empty
	}

	/**
	 * @see visitor.ProjectVisitor#visitTask(visitor.Task)
	 */
	@Override
	public void visitTask(final Task p)
	{
		this.totalCost += p.getTimeRequired() * this.hourlyRate;
	}
}
