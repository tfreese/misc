package decorator;

/**
 * @author Thomas Freese
 */
public abstract class ProjectDecorator implements ProjectItem
{
	/**
	 *
	 */
	private static final long serialVersionUID = -462632982661835662L;

	/**
     * 
     */
	private ProjectItem projectItem;

	/**
	 * Erstellt ein neues {@link ProjectDecorator} Object.
	 */
	ProjectDecorator()
	{
		super();
	}

	/**
	 * @return {@link ProjectItem}
	 */
	protected ProjectItem getProjectItem()
	{
		return this.projectItem;
	}

	/**
	 * @see decorator.ProjectItem#getTimeRequired()
	 */
	@Override
	public double getTimeRequired()
	{
		return this.projectItem.getTimeRequired();
	}

	/**
	 * @param newProjectItem {@link ProjectItem}
	 */
	public void setProjectItem(final ProjectItem newProjectItem)
	{
		this.projectItem = newProjectItem;
	}
}
