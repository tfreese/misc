package flyweight;

/**
 * @author Thomas Freese
 */
public class AddressImpl implements Address
{
	/**
	 *
	 */
	private static final long serialVersionUID = 5703463543828001128L;

	/**
     * 
     */
	public static final String HOME = "home";

	/**
     * 
     */
	public static final String WORK = "work";

	/**
     * 
     */
	private String city;

	/**
     * 
     */
	private String description;

	/**
     * 
     */
	private String state;

	/**
     * 
     */
	private String street;

	/**
     * 
     */
	private String type;

	/**
     * 
     */
	private String zipCode;

	/**
	 * Creates a new {@link AddressImpl} object.
	 */
	public AddressImpl()
	{
		super();
	}

	/**
	 * Creates a new {@link AddressImpl} object.
	 * 
	 * @param newDescription String
	 * @param newStreet String
	 * @param newCity String
	 * @param newState String
	 * @param newZipCode String
	 */
	public AddressImpl(final String newDescription, final String newStreet, final String newCity,
			final String newState, final String newZipCode)
	{
		super();

		this.description = newDescription;
		this.street = newStreet;
		this.city = newCity;
		this.state = newState;
		this.zipCode = newZipCode;
	}

	/**
	 * @see flyweight.Address#getCity()
	 */
	@Override
	public String getCity()
	{
		return this.city;
	}

	/**
	 * @see flyweight.Address#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @see flyweight.Address#getState()
	 */
	@Override
	public String getState()
	{
		return this.state;
	}

	/**
	 * @see flyweight.Address#getStreet()
	 */
	@Override
	public String getStreet()
	{
		return this.street;
	}

	/**
	 * @see flyweight.Address#getType()
	 */
	@Override
	public String getType()
	{
		return this.type;
	}

	/**
	 * @see flyweight.Address#getZipCode()
	 */
	@Override
	public String getZipCode()
	{
		return this.zipCode;
	}

	/**
	 * @see flyweight.Address#setCity(java.lang.String)
	 */
	@Override
	public void setCity(final String newCity)
	{
		this.city = newCity;
	}

	/**
	 * @see flyweight.Address#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(final String newDescription)
	{
		this.description = newDescription;
	}

	/**
	 * @see flyweight.Address#setState(java.lang.String)
	 */
	@Override
	public void setState(final String newState)
	{
		this.state = newState;
	}

	/**
	 * @see flyweight.Address#setStreet(java.lang.String)
	 */
	@Override
	public void setStreet(final String newStreet)
	{
		this.street = newStreet;
	}

	/**
	 * @see flyweight.Address#setType(java.lang.String)
	 */
	@Override
	public void setType(final String newType)
	{
		this.type = newType;
	}

	/**
	 * @see flyweight.Address#setZipCode(java.lang.String)
	 */
	@Override
	public void setZipCode(final String newZip)
	{
		this.zipCode = newZip;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.street + EOL_STRING + this.city + COMMA + SPACE + this.state + SPACE
				+ this.zipCode + EOL_STRING;
	}
}
