package proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class AddressBookImpl implements AddressBook
{
	/**
     * 
     */
	private List<Address> addresses = new ArrayList<>();

	/**
     * 
     */
	private File file = null;

	/**
	 * Creates a new {@link AddressBookImpl} object.
	 * 
	 * @param newFile File
	 */
	public AddressBookImpl(final File newFile)
	{
		super();

		this.file = newFile;
		open();
	}

	/**
	 * @see proxy.AddressBook#add(proxy.Address)
	 */
	@Override
	public void add(final Address address)
	{
		if (!this.addresses.contains(address))
		{
			this.addresses.add(address);
		}
	}

	/**
	 * @see proxy.AddressBook#getAddress(java.lang.String)
	 */
	@Override
	public Address getAddress(final String description)
	{
		Iterator<Address> addressIterator = this.addresses.iterator();

		while (addressIterator.hasNext())
		{
			AddressImpl address = (AddressImpl) addressIterator.next();

			if (address.getDescription().equalsIgnoreCase(description))
			{
				return address;
			}
		}

		return null;
	}

	/**
	 * @see proxy.AddressBook#getAllAddresses()
	 */
	@Override
	public List<Address> getAllAddresses()
	{
		return this.addresses;
	}

	/**
	 * @see proxy.AddressBook#open()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void open()
	{
		this.addresses = (ArrayList<Address>) FileLoader.loadData(this.file);
	}

	/**
	 * @see proxy.AddressBook#save()
	 */
	@Override
	public void save()
	{
		FileLoader.storeData(this.file, (ArrayList<Address>) this.addresses);
	}
}
