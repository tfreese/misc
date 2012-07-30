package prototype;

/**
 * @author Thomas Freese
 */
public class Address implements Copyable
{
	/**
	 * 
	 */
	public static final String EOL_STRING = System.getProperty("line.separator");

	/**
	 * 
	 */
	public static final String COMMA = ",";

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
	 * Creates a new {@link Address} object.
	 */
	public Address()
	{
		super();

	}

	/**
	 * Creates a new {@link Address} object.
	 * 
	 * @param initType String
	 */
	public Address(final String initType)
	{
		this.type = initType;
	}

	/**
	 * Creates a new {@link Address} object.
	 * 
	 * @param initStreet String
	 * @param initCity String
	 * @param initState String
	 * @param initZip String
	 */
	public Address(final String initStreet, final String initCity, final String initState,
			final String initZip)
	{
		this(WORK, initStreet, initCity, initState, initZip);
	}

	/**
	 * Creates a new {@link Address} object.
	 * 
	 * @param initType String
	 * @param initStreet String
	 * @param initCity String
	 * @param initState String
	 * @param initZip String
	 */
	public Address(final String initType, final String initStreet, final String initCity,
			final String initState, final String initZip)
	{
		super();

		this.type = initType;
		this.street = initStreet;
		this.city = initCity;
		this.state = initState;
		this.zipCode = initZip;
	}

	/**
	 * @see prototype.Copyable#copy()
	 */
	@Override
	public Object copy()
	{
		return new Address(this.street, this.city, this.state, this.zipCode);
	}

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
	public String getState()
	{
		return this.state;
	}

	/**
	 * @return String
	 */
	public String getStreet()
	{
		return this.street;
	}

	/**
	 * @return String
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * @return String
	 */
	public String getZipCode()
	{
		return this.zipCode;
	}

	/**
	 * @param newCity String
	 */
	public void setCity(final String newCity)
	{
		this.city = newCity;
	}

	/**
	 * @param newState String
	 */
	public void setState(final String newState)
	{
		this.state = newState;
	}

	/**
	 * @param newStreet String
	 */
	public void setStreet(final String newStreet)
	{
		this.street = newStreet;
	}

	/**
	 * @param newType String
	 */
	public void setType(final String newType)
	{
		this.type = newType;
	}

	/**
	 * @param newZip String
	 */
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
		return "\t" + this.street + COMMA + " " + EOL_STRING + "\t" + this.city + COMMA + " "
				+ this.state + " " + this.zipCode;
	}
}
