package AbstractFactory;

/**
 * @author Thomas Freese
 */
public interface AddressFactory
{
	/**
	 * @return {@link Address}
	 */
	public Address createAddress();

	/**
	 * @return {@link PhoneNumber}
	 */
	public PhoneNumber createPhoneNumber();
}
