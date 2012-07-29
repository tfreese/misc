package mediator;

/**
 * @author Thomas Freese
 */
public class ContactImpl implements Contact
{
	/**
	 *
	 */
	private static final long serialVersionUID = -242788009777491161L;

	/**
     * 
     */
	private String firstName;

	/**
     * 
     */
	private String lastName;

	/**
     * 
     */
	private String organization;

	/**
     * 
     */
	private String title;

	/**
	 * Creates a new {@link ContactImpl} object.
	 */
	public ContactImpl()
	{
		super();
	}

	/**
	 * Creates a new {@link ContactImpl} object.
	 * 
	 * @param newFirstName String
	 * @param newLastName String
	 * @param newTitle String
	 * @param newOrganization String
	 */
	public ContactImpl(final String newFirstName, final String newLastName, final String newTitle,
			final String newOrganization)
	{
		this.firstName = newFirstName;
		this.lastName = newLastName;
		this.title = newTitle;
		this.organization = newOrganization;
	}

	/**
	 * @see mediator.Contact#getFirstName()
	 */
	@Override
	public String getFirstName()
	{
		return this.firstName;
	}

	/**
	 * @see mediator.Contact#getLastName()
	 */
	@Override
	public String getLastName()
	{
		return this.lastName;
	}

	/**
	 * @see mediator.Contact#getOrganization()
	 */
	@Override
	public String getOrganization()
	{
		return this.organization;
	}

	/**
	 * @see mediator.Contact#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * @see mediator.Contact#setFirstName(java.lang.String)
	 */
	@Override
	public void setFirstName(final String newFirstName)
	{
		this.firstName = newFirstName;
	}

	/**
	 * @see mediator.Contact#setLastName(java.lang.String)
	 */
	@Override
	public void setLastName(final String newLastName)
	{
		this.lastName = newLastName;
	}

	/**
	 * @see mediator.Contact#setOrganization(java.lang.String)
	 */
	@Override
	public void setOrganization(final String newOrganization)
	{
		this.organization = newOrganization;
	}

	/**
	 * @see mediator.Contact#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(final String newTitle)
	{
		this.title = newTitle;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.firstName + SPACE + this.lastName;
	}
}
