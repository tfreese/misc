package mvc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class ContactModel
{
	/**
     * 
     */
	private List<ContactView> contactViews = new ArrayList<>();

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
	 * Creates a new {@link ContactModel} object.
	 */
	public ContactModel()
	{
		this(null);
	}

	/**
	 * Creates a new {@link ContactModel} object.
	 * 
	 * @param view {@link ContactView}
	 */
	public ContactModel(final ContactView view)
	{
		this.firstName = "";
		this.lastName = "";
		this.title = "";
		this.organization = "";

		if (view != null)
		{
			this.contactViews.add(view);
		}
	}

	/**
	 * @param view {@link ContactView}
	 */
	public void addContactView(final ContactView view)
	{
		if (!this.contactViews.contains(view))
		{
			this.contactViews.add(view);
		}
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
	 * @param input String
	 * @return boolean
	 */
	private boolean isEmptyString(final String input)
	{
		return ((input == null) || input.equals(""));
	}

	/**
	 * @param view {@link ContactView}
	 */
	public void removeContactView(final ContactView view)
	{
		this.contactViews.remove(view);
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
		if (!isEmptyString(newFirstName))
		{
			setFirstName(newFirstName);
		}

		if (!isEmptyString(newLastName))
		{
			setLastName(newLastName);
		}

		if (!isEmptyString(newTitle))
		{
			setTitle(newTitle);
		}

		if (!isEmptyString(newOrganization))
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
		Iterator<ContactView> notifyViews = this.contactViews.iterator();

		while (notifyViews.hasNext())
		{
			notifyViews.next().refreshContactView(this.firstName, this.lastName, this.title,
					this.organization);
		}
	}
}
