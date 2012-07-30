package proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class AddressBookProxy implements AddressBook
{
	/**
     * 
     */
	private AddressBookImpl addressBook;

	/**
     * 
     */
	private File file;

	/**
     * 
     */
	private List<Address> localAddresses = new ArrayList<>();

	/**
	 * Creates a new {@link AddressBookProxy} object.
	 * 
	 * @param filename String
	 */
	public AddressBookProxy(final String filename)
	{
		super();

		this.file = new File(filename);
	}

	/**
	 * @see proxy.AddressBook#add(proxy.Address)
	 */
	@Override
	public void add(final Address address)
	{
		if (this.addressBook != null)
		{
			this.addressBook.add(address);
		}
		else if (!this.localAddresses.contains(address))
		{
			this.localAddresses.add(address);
		}
	}

	/**
	 * @see proxy.AddressBook#getAddress(java.lang.String)
	 */
	@Override
	public Address getAddress(final String description)
	{
		if (!this.localAddresses.isEmpty())
		{
			Iterator<Address> addressIterator = this.localAddresses.iterator();

			while (addressIterator.hasNext())
			{
				AddressImpl address = (AddressImpl) addressIterator.next();

				if (address.getDescription().equalsIgnoreCase(description))
				{
					return address;
				}
			}
		}

		if (this.addressBook == null)
		{
			open();
		}

		return this.addressBook.getAddress(description);
	}

	/**
	 * @see proxy.AddressBook#getAllAddresses()
	 */
	@Override
	public List<Address> getAllAddresses()
	{
		if (this.addressBook == null)
		{
			open();
		}

		return this.addressBook.getAllAddresses();
	}

	/**
	 * @see proxy.AddressBook#open()
	 */
	@Override
	public void open()
	{
		this.addressBook = new AddressBookImpl(this.file);
		Iterator<Address> addressIterator = this.localAddresses.iterator();

		while (addressIterator.hasNext())
		{
			this.addressBook.add(addressIterator.next());
		}
	}

	/**
	 * @see proxy.AddressBook#save()
	 */
	@Override
	public void save()
	{
		if (this.addressBook != null)
		{
			this.addressBook.save();
		}
		else if (!this.localAddresses.isEmpty())
		{
			open();
			this.addressBook.save();
		}
	}
}
