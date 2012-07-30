package mediator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class ContactMediatorImpl implements ContactMediator
{
	/**
     * 
     */
	private int contactIndex;

	/**
     * 
     */
	private List<Contact> contacts = new ArrayList<>();

	/**
     * 
     */
	private ContactDisplayPanel display;

	/**
     * 
     */
	private ContactEditorPanel editor;

	/**
     * 
     */
	private ContactSelectorPanel selector;

	/**
	 * @param contact {@link Contact}
	 */
	public void addContact(final Contact contact)
	{
		if (!this.contacts.contains(contact))
		{
			this.contacts.add(contact);
		}
	}

	/**
	 * @see mediator.ContactMediator#createContact(java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void createContact(final String firstName, final String lastName, final String title,
								final String organization)
	{
		Contact newContact = new ContactImpl(firstName, lastName, title, organization);

		addContact(newContact);
		this.selector.addContact(newContact);
		this.display.contactChanged(newContact);
	}

	/**
	 * @see mediator.ContactMediator#getAllContacts()
	 */
	@Override
	public Contact[] getAllContacts()
	{
		return this.contacts.toArray(new Contact[1]);
	}

	/**
	 * @see mediator.ContactMediator#selectContact(mediator.Contact)
	 */
	@Override
	public void selectContact(final Contact contact)
	{
		if (this.contacts.contains(contact))
		{
			this.contactIndex = this.contacts.indexOf(contact);
			this.display.contactChanged(contact);
			this.editor.setContactFields(contact);
		}
	}

	/**
	 * @see mediator.ContactMediator#setContactDisplayPanel(mediator.ContactDisplayPanel)
	 */
	@Override
	public void setContactDisplayPanel(final ContactDisplayPanel displayPanel)
	{
		this.display = displayPanel;
	}

	/**
	 * @see mediator.ContactMediator#setContactEditorPanel(mediator.ContactEditorPanel)
	 */
	@Override
	public void setContactEditorPanel(final ContactEditorPanel editorPanel)
	{
		this.editor = editorPanel;
	}

	/**
	 * @see mediator.ContactMediator#setContactSelectorPanel(mediator.ContactSelectorPanel)
	 */
	@Override
	public void setContactSelectorPanel(final ContactSelectorPanel selectorPanel)
	{
		this.selector = selectorPanel;
	}

	/**
	 * @see mediator.ContactMediator#updateContact(java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void updateContact(final String firstName, final String lastName, final String title,
								final String organization)
	{
		Contact updateContact = this.contacts.get(this.contactIndex);

		if (updateContact != null)
		{
			updateContact.setFirstName(firstName);
			updateContact.setLastName(lastName);
			updateContact.setTitle(title);
			updateContact.setOrganization(organization);
			this.display.contactChanged(updateContact);
		}
	}
}
