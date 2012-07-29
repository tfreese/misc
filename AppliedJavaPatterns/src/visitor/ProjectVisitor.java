package visitor;

/**
 * @author Thomas Freese
 */
public interface ProjectVisitor
{
	/**
	 * @param p {@link Deliverable}
	 */
	public void visitDeliverable(Deliverable p);

	/**
	 * @param p {@link DependentTask}
	 */
	public void visitDependentTask(DependentTask p);

	/**
	 * @param p {@link Project}
	 */
	public void visitProject(Project p);

	/**
	 * @param p {@link Task}
	 */
	public void visitTask(Task p);
}
