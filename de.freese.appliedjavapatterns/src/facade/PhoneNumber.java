package facade;

/**
 * @author Thomas Freese
 */
public class PhoneNumber
{
	/**
     * 
     */
	private static String selectedInterPrefix;

	/**
	 * @return String
	 */
	public static String getSelectedInterPrefix()
	{
		return selectedInterPrefix;
	}

	/**
	 * @param prefix String
	 */
	public static void setSelectedInterPrefix(final String prefix)
	{
		selectedInterPrefix = prefix;
	}

	/**
     * 
     */
	private String areaNumber;

	/**
     * 
     */
	private String internationalPrefix;

	/**
     * 
     */
	private String netNumber;

	/**
	 * Creates a new {@link PhoneNumber} object.
	 * 
	 * @param intPrefix String
	 * @param areaNumber String
	 * @param netNumber String
	 */
	public PhoneNumber(final String intPrefix, final String areaNumber, final String netNumber)
	{
		super();

		this.internationalPrefix = intPrefix;
		this.areaNumber = areaNumber;
		this.netNumber = netNumber;
	}

	/**
	 * @return String
	 */
	public String getAreaNumber()
	{
		return this.areaNumber;
	}

	/**
	 * @return String
	 */
	public String getInternationalPrefix()
	{
		return this.internationalPrefix;
	}

	/**
	 * @return String
	 */
	public String getNetNumber()
	{
		return this.netNumber;
	}

	/**
	 * @param newAreaNumber String
	 */
	public void setAreaNumber(final String newAreaNumber)
	{
		this.areaNumber = newAreaNumber;
	}

	/**
	 * @param newPrefix String
	 */
	public void setInternationalPrefix(final String newPrefix)
	{
		this.internationalPrefix = newPrefix;
	}

	/**
	 * @param newNetNumber String
	 */
	public void setNetNumber(final String newNetNumber)
	{
		this.netNumber = newNetNumber;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.internationalPrefix + this.areaNumber + this.netNumber;
	}
}
