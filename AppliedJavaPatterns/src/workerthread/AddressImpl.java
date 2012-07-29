package workerthread;

/**
 * @author Thomas Freese
 */
public class AddressImpl implements Address
{
	/**
     * 
     */
	private static final long serialVersionUID = -3191513301663505312L;

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
	 * Creates a new AddressImpl object.
	 */
	public AddressImpl()
	{
		super();
	}

	/**
	 * Creates a new AddressImpl object.
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
		this.description = newDescription;
		this.street = newStreet;
		this.city = newCity;
		this.state = newState;
		this.zipCode = newZipCode;
	}

	/**
	 * @see workerthread.Address#getCity()
	 */
	@Override
	public String getCity()
	{
		return this.city;
	}

	/**
	 * @see workerthread.Address#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @see workerthread.Address#getState()
	 */
	@Override
	public String getState()
	{
		return this.state;
	}

	/**
	 * @see workerthread.Address#getStreet()
	 */
	@Override
	public String getStreet()
	{
		return this.street;
	}

	/**
	 * @see workerthread.Address#getType()
	 */
	@Override
	public String getType()
	{
		return this.type;
	}

	/**
	 * @see workerthread.Address#getZipCode()
	 */
	@Override
	public String getZipCode()
	{
		return this.zipCode;
	}

	/**
	 * @see workerthread.Address#setCity(java.lang.String)
	 */
	@Override
	public void setCity(final String newCity)
	{
		this.city = newCity;
	}

	/**
	 * @see workerthread.Address#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(final String newDescription)
	{
		this.description = newDescription;
	}

	/**
	 * @see workerthread.Address#setState(java.lang.String)
	 */
	@Override
	public void setState(final String newState)
	{
		this.state = newState;
	}

	/**
	 * @see workerthread.Address#setStreet(java.lang.String)
	 */
	@Override
	public void setStreet(final String newStreet)
	{
		this.street = newStreet;
	}

	/**
	 * @see workerthread.Address#setType(java.lang.String)
	 */
	@Override
	public void setType(final String newType)
	{
		this.type = newType;
	}

	/**
	 * @see workerthread.Address#setZipCode(java.lang.String)
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
