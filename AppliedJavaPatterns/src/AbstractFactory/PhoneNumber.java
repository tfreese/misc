package AbstractFactory;

/**
 * @author Thomas Freese
 */
public abstract class PhoneNumber
{
	/**
     * 
     */
	private String phoneNumber;

	/**
	 * @return String
	 */
	public abstract String getCountryCode();

	/**
	 * @return String
	 */
	public String getPhoneNumber()
	{
		return this.phoneNumber;
	}

	/**
	 * @param newNumber String
	 */
	public void setPhoneNumber(final String newNumber)
	{
		try
		{
			Long.parseLong(newNumber);
			this.phoneNumber = newNumber;
		}
		catch (NumberFormatException exc)
		{
			// Ignore
		}
	}
}
