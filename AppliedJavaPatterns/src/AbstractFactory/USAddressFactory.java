package AbstractFactory;

/**
 * @author Thomas Freese
 */
public class USAddressFactory implements AddressFactory
{
	/**
	 * @see AbstractFactory.AddressFactory#createAddress()
	 */
	@Override
	public Address createAddress()
	{
		return new USAddress();
	}

	/**
	 * @see AbstractFactory.AddressFactory#createPhoneNumber()
	 */
	@Override
	public PhoneNumber createPhoneNumber()
	{
		return new USPhoneNumber();
	}
}
