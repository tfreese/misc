package mediator;

/**
 * @author Thomas Freese
 */
public interface ContactMediator
{
	/**
	 * @param firstName String
	 * @param lastName String
	 * @param title String
	 * @param organization String
	 */
	public void createContact(String firstName, String lastName, String title, String organization);

	/**
	 * @return {@link Contact}[]
	 */
	public Contact[] getAllContacts();

	/**
	 * @param contact {@link Contact}
	 */
	public void selectContact(Contact contact);

	/**
	 * @param displayPanel {@link ContactDisplayPanel}
	 */
	public void setContactDisplayPanel(ContactDisplayPanel displayPanel);

	/**
	 * @param editorPanel {@link ContactEditorPanel}
	 */
	public void setContactEditorPanel(ContactEditorPanel editorPanel);

	/**
	 * @param selectorPanel {@link ContactSelectorPanel}
	 */
	public void setContactSelectorPanel(ContactSelectorPanel selectorPanel);

	/**
	 * @param firstName String
	 * @param lastName String
	 * @param title String
	 * @param organization String
	 */
	public void updateContact(String firstName, String lastName, String title, String organization);
}
