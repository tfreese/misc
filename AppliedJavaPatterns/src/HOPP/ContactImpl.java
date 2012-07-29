package HOPP;

/**
 * @author Thomas Freese
 */
public class ContactImpl implements Contact
{
	/**
	 *
	 */
	private static final long serialVersionUID = 924301855067050536L;

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
	 * @see HOPP.Contact#getFirstName()
	 */
	@Override
	public String getFirstName()
	{
		return this.firstName;
	}

	/**
	 * @see HOPP.Contact#getLastName()
	 */
	@Override
	public String getLastName()
	{
		return this.lastName;
	}

	/**
	 * @see HOPP.Contact#getOrganization()
	 */
	@Override
	public String getOrganization()
	{
		return this.organization;
	}

	/**
	 * @see HOPP.Contact#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * @see HOPP.Contact#setFirstName(java.lang.String)
	 */
	@Override
	public void setFirstName(final String newFirstName)
	{
		this.firstName = newFirstName;
	}

	/**
	 * @see HOPP.Contact#setLastName(java.lang.String)
	 */
	@Override
	public void setLastName(final String newLastName)
	{
		this.lastName = newLastName;
	}

	/**
	 * @see HOPP.Contact#setOrganization(java.lang.String)
	 */
	@Override
	public void setOrganization(final String newOrganization)
	{
		this.organization = newOrganization;
	}

	/**
	 * @see HOPP.Contact#setTitle(java.lang.String)
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
