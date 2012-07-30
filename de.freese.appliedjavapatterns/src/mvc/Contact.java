package mvc;

/**
 * @author Thomas Freese
 */
public class Contact
{
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
     * 
     */
	private ContactView view;

	/**
	 * Creates a new {@link Contact} object.
	 * 
	 * @param v {@link ContactView}
	 */
	public Contact(final ContactView v)
	{
		this.firstName = "";
		this.lastName = "";
		this.title = "";
		this.organization = "";

		this.view = v;
	}

	/**
	 * @return String
	 */
	public String getFirstName()
	{
		return this.firstName;
	}

	/**
	 * @return String
	 */
	public String getLastName()
	{
		return this.lastName;
	}

	/**
	 * @return String
	 */
	public String getOrganization()
	{
		return this.organization;
	}

	/**
	 * @return String
	 */
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * @param newFirstName String
	 */
	public void setFirstName(final String newFirstName)
	{
		this.firstName = newFirstName;
	}

	/**
	 * @param newLastName String
	 */
	public void setLastName(final String newLastName)
	{
		this.lastName = newLastName;
	}

	/**
	 * @param newOrganization String
	 */
	public void setOrganization(final String newOrganization)
	{
		this.organization = newOrganization;
	}

	/**
	 * @param newTitle String
	 */
	public void setTitle(final String newTitle)
	{
		this.title = newTitle;
	}

	/**
	 * @param newFirstName String
	 * @param newLastName String
	 * @param newTitle String
	 * @param newOrganization String
	 */
	public void updateModel(final String newFirstName, final String newLastName,
							final String newTitle, final String newOrganization)
	{
		if ((newFirstName != null) && !newFirstName.equals(""))
		{
			setFirstName(newFirstName);
		}

		if ((newLastName != null) && !newLastName.equals(""))
		{
			setLastName(newLastName);
		}

		if ((newTitle != null) && !newTitle.equals(""))
		{
			setTitle(newTitle);
		}

		if ((newOrganization != null) && !newOrganization.equals(""))
		{
			setOrganization(newOrganization);
		}

		updateView();
	}

	/**
     * 
     */
	private void updateView()
	{
		this.view.refreshContactView(this.firstName, this.lastName, this.title, this.organization);
	}
}
