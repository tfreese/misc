package session;

/**
 * @author Thomas Freese
 */
public class AddressImpl implements Address
{
	/**
	 *
	 */
	private static final long serialVersionUID = -2874948602875600167L;

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
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj == null)
		{
			return false;
		}

		if (!(obj instanceof AddressImpl))
		{
			return false;
		}

		AddressImpl other = (AddressImpl) obj;

		if (this.city == null)
		{
			if (other.city != null)
			{
				return false;
			}
		}
		else if (!this.city.equals(other.city))
		{
			return false;
		}

		if (this.state == null)
		{
			if (other.state != null)
			{
				return false;
			}
		}
		else if (!this.state.equals(other.state))
		{
			return false;
		}

		if (this.street == null)
		{
			if (other.street != null)
			{
				return false;
			}
		}
		else if (!this.street.equals(other.street))
		{
			return false;
		}

		if (this.zipCode == null)
		{
			if (other.zipCode != null)
			{
				return false;
			}
		}
		else if (!this.zipCode.equals(other.zipCode))
		{
			return false;
		}

		return true;
	}

	/**
	 * @see session.Address#getCity()
	 */
	@Override
	public String getCity()
	{
		return this.city;
	}

	/**
	 * @see session.Address#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @see session.Address#getState()
	 */
	@Override
	public String getState()
	{
		return this.state;
	}

	/**
	 * @see session.Address#getStreet()
	 */
	@Override
	public String getStreet()
	{
		return this.street;
	}

	/**
	 * @see session.Address#getType()
	 */
	@Override
	public String getType()
	{
		return this.type;
	}

	/**
	 * @see session.Address#getZipCode()
	 */
	@Override
	public String getZipCode()
	{
		return this.zipCode;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.city == null) ? 0 : this.city.hashCode());
		result = (prime * result) + ((this.state == null) ? 0 : this.state.hashCode());
		result = (prime * result) + ((this.street == null) ? 0 : this.street.hashCode());
		result = (prime * result) + ((this.zipCode == null) ? 0 : this.zipCode.hashCode());

		return result;
	}

	/**
	 * @see session.Address#setCity(java.lang.String)
	 */
	@Override
	public void setCity(final String newCity)
	{
		this.city = newCity;
	}

	/**
	 * @see session.Address#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(final String newDescription)
	{
		this.description = newDescription;
	}

	/**
	 * @see session.Address#setState(java.lang.String)
	 */
	@Override
	public void setState(final String newState)
	{
		this.state = newState;
	}

	/**
	 * @see session.Address#setStreet(java.lang.String)
	 */
	@Override
	public void setStreet(final String newStreet)
	{
		this.street = newStreet;
	}

	/**
	 * @see session.Address#setType(java.lang.String)
	 */
	@Override
	public void setType(final String newType)
	{
		this.type = newType;
	}

	/**
	 * @see session.Address#setZipCode(java.lang.String)
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
