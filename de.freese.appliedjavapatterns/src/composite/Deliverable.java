package composite;

/**
 * @author Thomas Freese
 */
public class Deliverable implements ProjectItem
{
	/**
	 *
	 */
	private static final long serialVersionUID = 3582105480476239652L;

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
	private Contact owner;

	/**
	 * Creates a new {@link Deliverable} object.
	 */
	public Deliverable()
	{
		super();
	}

	/**
	 * Creates a new {@link Deliverable} object.
	 * 
	 * @param newName String
	 * @param newDescription String
	 * @param newOwner String
	 */
	public Deliverable(final String newName, final String newDescription, final Contact newOwner)
	{
		super();

		this.name = newName;
		this.description = newDescription;
		this.owner = newOwner;
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
	 * @return {@link Contact}
	 */
	public Contact getOwner()
	{
		return this.owner;
	}

	/**
	 * @see composite.ProjectItem#getTimeRequired()
	 */
	@Override
	public double getTimeRequired()
	{
		return 0;
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

	/**
	 * @param newOwner {@link Contact}
	 */
	public void setOwner(final Contact newOwner)
	{
		this.owner = newOwner;
	}
}
