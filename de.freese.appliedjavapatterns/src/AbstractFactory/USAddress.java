package AbstractFactory;

/**
 * @author Thomas Freese
 */
public class USAddress extends Address
{
	/**
     * 
     */
	private static final String COUNTRY = "UNITED STATES";

	/**
     * 
     */
	private static final String COMMA = ",";

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
		return getStreet() + EOL_STRING + getCity() + COMMA + SPACE + getRegion() + SPACE
				+ getPostalCode() + EOL_STRING + COUNTRY + EOL_STRING;
	}
}
