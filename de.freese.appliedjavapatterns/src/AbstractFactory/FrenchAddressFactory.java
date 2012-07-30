package AbstractFactory;

/**
 * @author Thomas Freese
 */
public class FrenchAddressFactory implements AddressFactory
{
	/**
	 * @see AbstractFactory.AddressFactory#createAddress()
	 */
	@Override
	public Address createAddress()
	{
		return new FrenchAddress();
	}

	/**
	 * @see AbstractFactory.AddressFactory#createPhoneNumber()
	 */
	@Override
	public PhoneNumber createPhoneNumber()
	{
		return new FrenchPhoneNumber();
	}
}
