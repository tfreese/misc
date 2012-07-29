package decorator;

/**
 * @author Thomas Freese
 */
public class ContactImpl implements Contact
{
	/**
	 *
	 */
	private static final long serialVersionUID = 3160024100606947109L;

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
		super();

		this.firstName = newFirstName;
		this.lastName = newLastName;
		this.title = newTitle;
		this.organization = newOrganization;
	}

	/**
	 * @see decorator.Contact#getFirstName()
	 */
	@Override
	public String getFirstName()
	{
		return this.firstName;
	}

	/**
	 * @see decorator.Contact#getLastName()
	 */
	@Override
	public String getLastName()
	{
		return this.lastName;
	}

	/**
	 * @see decorator.Contact#getOrganization()
	 */
	@Override
	public String getOrganization()
	{
		return this.organization;
	}

	/**
	 * @see decorator.Contact#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * @see decorator.Contact#setFirstName(java.lang.String)
	 */
	@Override
	public void setFirstName(final String newFirstName)
	{
		this.firstName = newFirstName;
	}

	/**
	 * @see decorator.Contact#setLastName(java.lang.String)
	 */
	@Override
	public void setLastName(final String newLastName)
	{
		this.lastName = newLastName;
	}

	/**
	 * @see decorator.Contact#setOrganization(java.lang.String)
	 */
	@Override
	public void setOrganization(final String newOrganization)
	{
		this.organization = newOrganization;
	}

	/**
	 * @see decorator.Contact#setTitle(java.lang.String)
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
