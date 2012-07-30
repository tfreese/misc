package interpreter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class ContactList implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -2183359217171355114L;

	/**
     * 
     */
	private List<Contact> contacts = new ArrayList<>();

	/**
	 * Erstellt ein neues {@link ContactList} Object.
	 */
	ContactList()
	{
		super();
	}

	/**
	 * @param element {@link Contact}
	 */
	public void addContact(final Contact element)
	{
		if (!this.contacts.contains(element))
		{
			this.contacts.add(element);
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
	 * @return {@link Contact}[]
	 */
	public Contact[] getContactsAsArray()
	{
		return this.contacts.toArray(new Contact[0]);
	}

	/**
	 * @param expr {@link Expression}
	 * @param ctx {@link Context}
	 * @param key Object
	 * @return {@link List}
	 */
	public List<Contact> getContactsMatchingExpression(final Expression expr, final Context ctx,
														final Object key)
	{
		List<Contact> results = new ArrayList<>();
		Iterator<Contact> elements = this.contacts.iterator();

		while (elements.hasNext())
		{
			Contact currentContact = elements.next();

			ctx.addVariable(key, currentContact);
			expr.interpret(ctx);
			Object interpretResult = ctx.get(expr);

			if ((interpretResult != null) && (interpretResult.equals(Boolean.TRUE)))
			{
				results.add(currentContact);
			}
		}

		return results;
	}

	/**
	 * @param element {@link Contact}
	 */
	public void removeContact(final Contact element)
	{
		this.contacts.remove(element);
	}

	/**
	 * @param newContacts {@link List}
	 */
	public void setContacts(final List<Contact> newContacts)
	{
		this.contacts = newContacts;
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
