package memento;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class AddressBook
{
	/**
	 * @author Thomas Freese
	 */
	private class AddressBookMemento
	{
		/**
         * 
         */
		private final List<Contact> state;

		/**
		 * Creates a new {@link AddressBookMemento} object.
		 * 
		 * @param contacts {@link List}
		 */
		private AddressBookMemento(final List<Contact> contacts)
		{
			this.state = contacts;
		}
	}

	/**
     * 
     */
	private List<Contact> contacts = new ArrayList<>();

	/**
	 * Creates a new {@link AddressBook} object.
	 */
	public AddressBook()
	{
		super();
	}

	/**
	 * Creates a new {@link AddressBook} object.
	 * 
	 * @param newContacts {@link List}
	 */
	public AddressBook(final List<Contact> newContacts)
	{
		this.contacts = newContacts;
	}

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
	 * @return {@link List}
	 */
	public List<Contact> getContacts()
	{
		return this.contacts;
	}

	/**
	 * @return Object
	 */
	public Object getMemento()
	{
		return new AddressBookMemento(this.contacts);
	}

	/**
     * 
     */
	public void removeAllContacts()
	{
		this.contacts = new ArrayList<>();
	}

	/**
	 * @param contact {@link Contact}
	 */
	public void removeContact(final Contact contact)
	{
		this.contacts.remove(contact);
	}

	/**
	 * @param object Object
	 */
	public void setMemento(final Object object)
	{
		if (object instanceof AddressBookMemento)
		{
			AddressBookMemento memento = (AddressBookMemento) object;

			this.contacts = memento.state;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.contacts.toString();
	}
}
