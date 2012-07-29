package decorator;

/**
 * @author Thomas Freese
 */
public class DependentProjectItem extends ProjectDecorator
{
	/**
	 *
	 */
	private static final long serialVersionUID = -6090763304753253397L;

	/**
     * 
     */
	private ProjectItem dependentItem;

	/**
	 * Creates a new {@link DependentProjectItem} object.
	 */
	public DependentProjectItem()
	{
		super();
	}

	/**
	 * Creates a new {@link DependentProjectItem} object.
	 * 
	 * @param newDependentItem {@link ProjectItem}
	 */
	public DependentProjectItem(final ProjectItem newDependentItem)
	{
		super();

		this.dependentItem = newDependentItem;
	}

	/**
	 * @return {@link ProjectItem}
	 */
	public ProjectItem getDependentItem()
	{
		return this.dependentItem;
	}

	/**
	 * @param newDependentItem {@link ProjectItem}
	 */
	public void setDependentItem(final ProjectItem newDependentItem)
	{
		this.dependentItem = newDependentItem;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getProjectItem().toString() + EOL_STRING + "\tProjectItem dependent on: "
				+ this.dependentItem;
	}
}
