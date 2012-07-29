package proxy;

import java.util.List;

/**
 * @author Thomas Freese
 */
public interface AddressBook
{
	/**
	 * @param address {@link Address}
	 */
	public void add(Address address);

	/**
	 * @param description String
	 * @return {@link Address}
	 */
	public Address getAddress(String description);

	/**
	 * @return {@link List}
	 */
	public List<Address> getAllAddresses();

	/**
     * 
     */
	public void open();

	/**
     * 
     */
	public void save();
}
