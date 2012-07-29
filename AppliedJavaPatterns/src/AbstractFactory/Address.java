package AbstractFactory;

/**
 * @author Thomas Freese
 */
public abstract class Address
{
	/**
     * 
     */
	public static final String EOL_STRING = System.getProperty("line.separator");

	/**
     * 
     */
	public static final String SPACE = " ";

	/**
     * 
     */
	private String city;

	/**
     * 
     */
	private String postalCode;

	/**
     * 
     */
	private String region;

	/**
     * 
     */
	private String street;

	/**
	 * @return String
	 */
	public String getCity()
	{
		return this.city;
	}

	/**
	 * @return String
	 */
	public abstract String getCountry();

	/**
	 * @return String
	 */
	public String getFullAddress()
	{
		return this.street + EOL_STRING + this.city + SPACE + this.postalCode + EOL_STRING;
	}

	/**
	 * @return String
	 */
	public String getPostalCode()
	{
		return this.postalCode;
	}

	/**
	 * @return String
	 */
	public String getRegion()
	{
		return this.region;
	}

	/**
	 * @return String
	 */
	public String getStreet()
	{
		return this.street;
	}

	/**
	 * @param newCity String
	 */
	public void setCity(final String newCity)
	{
		this.city = newCity;
	}

	/**
	 * @param newPostalCode String
	 */
	public void setPostalCode(final String newPostalCode)
	{
		this.postalCode = newPostalCode;
	}

	/**
	 * @param newRegion String
	 */
	public void setRegion(final String newRegion)
	{
		this.region = newRegion;
	}

	/**
	 * @param newStreet String
	 */
	public void setStreet(final String newStreet)
	{
		this.street = newStreet;
	}
}
