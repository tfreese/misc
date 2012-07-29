package AbstractFactory;

/**
 * @author Thomas Freese
 */
public class FrenchPhoneNumber extends PhoneNumber
{
	/**
     * 
     */
	private static final String COUNTRY_CODE = "33";

	/**
     * 
     */
	private static final int NUMBER_LENGTH = 9;

	/**
	 * @see AbstractFactory.PhoneNumber#getCountryCode()
	 */
	@Override
	public String getCountryCode()
	{
		return COUNTRY_CODE;
	}

	/**
	 * @see AbstractFactory.PhoneNumber#setPhoneNumber(java.lang.String)
	 */
	@Override
	public void setPhoneNumber(final String newNumber)
	{
		if (newNumber.length() == NUMBER_LENGTH)
		{
			super.setPhoneNumber(newNumber);
		}
	}
}
