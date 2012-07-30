package workerthread;

import java.rmi.Naming;

/**
 * @author Thomas Freese
 */
public class AddressRetriever implements RunnableTask
{
	/**
     * 
     */
	private Address address;

	/**
     * 
     */
	private long addressID;

	/**
     * 
     */
	private String url;

	/**
	 * Creates a new AddressRetriever object.
	 * 
	 * @param newAddressID long
	 * @param newUrl String
	 */
	public AddressRetriever(final long newAddressID, final String newUrl)
	{
		this.addressID = newAddressID;
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

			this.address = dataStore.retrieveAddress(this.addressID);
		}
		catch (Exception exc)
		{
			// Ignore
		}
	}

	/**
	 * @return {@link Address}
	 */
	public Address getAddress()
	{
		return this.address;
	}

	/**
	 * @return boolean
	 */
	public boolean isAddressAvailable()
	{
		return (this.address == null) ? false : true;
	}
}
