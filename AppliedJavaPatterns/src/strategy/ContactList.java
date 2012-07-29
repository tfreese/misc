package strategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class ContactList implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 7528328292352265008L;

	/**
     * 
     */
	private List<Contact> contacts = new ArrayList<>();

	/**
     * 
     */
	private SummarizingStrategy summarizer;

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
	 * @return String[]
	 */
	public String[] makeSummarizedList()
	{
		return this.summarizer.makeSummarizedList(getContactsAsArray());
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
	 * @param newSummarizer {@link SummarizingStrategy}
	 */
	public void setSummarizer(final SummarizingStrategy newSummarizer)
	{
		this.summarizer = newSummarizer;
	}

	/**
	 * @return String
	 */
	public String summarize()
	{
		return this.summarizer.summarize(getContactsAsArray());
	}
}
