package workerthread;

import java.rmi.Naming;

/**
 * @author Thomas Freese
 */
public class ContactRetriever implements RunnableTask
{
	/**
     * 
     */
	private Contact contact;

	/**
     * 
     */
	private long contactID;

	/**
     * 
     */
	private String url;

	/**
	 * Creates a new ContactRetriever object.
	 * 
	 * @param newContactID long
	 * @param newUrl String
	 */
	public ContactRetriever(final long newContactID, final String newUrl)
	{
		this.contactID = newContactID;
		this.url = newUrl;
	}

	/**
	 * @see workerthread.RunnableTask#execute()
	 */
	@Override
	public void execute()
	{
		try
		{
			ServerDataStore dataStore = (ServerDataStore) Naming.lookup(this.url);

			this.contact = dataStore.retrieveContact(this.contactID);
		}
		catch (Exception exc)
		{
			// Ignore
		}
	}

	/**
	 * @return {@link Contact}
	 */
	public Contact getContact()
	{
		return this.contact;
	}

	/**
	 * @return boolean
	 */
	public boolean isContactAvailable()
	{
		return (this.contact == null) ? false : true;
	}
}
