package session;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class ContactImpl implements Contact
{
	/**
	 *
	 */
	private static final long serialVersionUID = -8142767937190967844L;

	/**
	 * 
	 */
	private List<Address> addresses = new ArrayList<>();

	/**
	 * 
	 */
	private String firstName;

	/**
	 * 
	 */
	private String lastName;

	/**
	 * 
	 */
	private String organization;

	/**
	 * 
	 */
	private String title;

	/**
	 * Creates a new {@link ContactImpl} object.
	 */
	public ContactImpl()
	{
		super();
	}

	/**
	 * Creates a new {@link ContactImpl} object.
	 * 
	 * @param newFirstName String
	 * @param newLastName String
	 * @param newTitle String
	 * @param newOrganization String
	 * @param newAddresses {@link List}
	 */
	public ContactImpl(final String newFirstName, final String newLastName, final String newTitle,
			final String newOrganization, final List<Address> newAddresses)
	{
		super();

		this.firstName = newFirstName;
		this.lastName = newLastName;
		this.title = newTitle;
		this.organization = newOrganization;

		if (newAddresses != null)
		{
			this.addresses = newAddresses;
		}
	}

	/**
	 * @see session.Contact#addAddress(session.Address)
	 */
	@Override
	public void addAddress(final Address address)
	{
		if (!this.addresses.contains(address))
		{
			this.addresses.add(address);
		}
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

		if (!(obj instanceof ContactImpl))
		{
			return false;
		}

		ContactImpl other = (ContactImpl) obj;

		if (this.firstName == null)
		{
			if (other.firstName != null)
			{
				return false;
			}
		}
		else if (!this.firstName.equals(other.firstName))
		{
			return false;
		}

		if (this.lastName == null)
		{
			if (other.lastName != null)
			{
				return false;
			}
		}
		else if (!this.lastName.equals(other.lastName))
		{
			return false;
		}

		if (this.organization == null)
		{
			if (other.organization != null)
			{
				return false;
			}
		}
		else if (!this.organization.equals(other.organization))
		{
			return false;
		}

		if (this.title == null)
		{
			if (other.title != null)
			{
				return false;
			}
		}
		else if (!this.title.equals(other.title))
		{
			return false;
		}

		return true;
	}

	/**
	 * @see session.Contact#getAddresses()
	 */
	@Override
	public List<Address> getAddresses()
	{
		return this.addresses;
	}

	/**
	 * @see session.Contact#getFirstName()
	 */
	@Override
	public String getFirstName()
	{
		return this.firstName;
	}

	/**
	 * @see session.Contact#getLastName()
	 */
	@Override
	public String getLastName()
	{
		return this.lastName;
	}

	/**
	 * @see session.Contact#getOrganization()
	 */
	@Override
	public String getOrganization()
	{
		return this.organization;
	}

	/**
	 * @see session.Contact#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.firstName == null) ? 0 : this.firstName.hashCode());
		result = (prime * result) + ((this.lastName == null) ? 0 : this.lastName.hashCode());
		result =
				(prime * result) + ((this.organization == null) ? 0 : this.organization.hashCode());
		result = (prime * result) + ((this.title == null) ? 0 : this.title.hashCode());

		return result;
	}

	/**
	 * @see session.Contact#removeAddress(session.Address)
	 */
	@Override
	public void removeAddress(final Address address)
	{
		this.addresses.remove(address);
	}

	/**
	 * @see session.Contact#setFirstName(java.lang.String)
	 */
	@Override
	public void setFirstName(final String newFirstName)
	{
		this.firstName = newFirstName;
	}

	/**
	 * @see session.Contact#setLastName(java.lang.String)
	 */
	@Override
	public void setLastName(final String newLastName)
	{
		this.lastName = newLastName;
	}

	/**
	 * @see session.Contact#setOrganization(java.lang.String)
	 */
	@Override
	public void setOrganization(final String newOrganization)
	{
		this.organization = newOrganization;
	}

	/**
	 * @see session.Contact#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(final String newTitle)
	{
		this.title = newTitle;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.firstName + SPACE + this.lastName + EOL_STRING + this.addresses;
	}
}
