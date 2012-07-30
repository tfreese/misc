package AbstractFactory;

/**
 * @author Thomas Freese
 */
public class FrenchAddress extends Address
{
	/**
     * 
     */
	private static final String COUNTRY = "FRANCE";

	/**
	 * @see AbstractFactory.Address#getCountry()
	 */
	@Override
	public String getCountry()
	{
		return COUNTRY;
	}

	/**
	 * @see AbstractFactory.Address#getFullAddress()
	 */
	@Override
	public String getFullAddress()
	{
		return getStreet() + EOL_STRING + getPostalCode() + SPACE + getCity() + EOL_STRING
				+ COUNTRY + EOL_STRING;
	}
}
