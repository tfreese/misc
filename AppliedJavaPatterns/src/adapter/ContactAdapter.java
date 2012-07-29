package adapter;

/**
 * @author Thomas Freese
 */
public class ContactAdapter implements Contact
{
	/**
     * 
     */
	private static final long serialVersionUID = -2345413044092908052L;

	/**
     * 
     */
	private Chovnatlh contact;

	/**
	 * Creates a new ContactAdapter object.
	 */
	public ContactAdapter()
	{
		this.contact = new ChovnatlhImpl();
	}

	/**
	 * Creates a new ContactAdapter object.
	 * 
	 * @param newContact {@link Chovnatlh}
	 */
	public ContactAdapter(final Chovnatlh newContact)
	{
		this.contact = newContact;
	}

	/**
	 * @see adapter.Contact#getFirstName()
	 */
	@Override
	public String getFirstName()
	{
		return this.contact.tlhapWa$DIchPong();
	}

	/**
	 * @see adapter.Contact#getLastName()
	 */
	@Override
	public String getLastName()
	{
		return this.contact.tlhapQavPong();
	}

	/**
	 * @see adapter.Contact#getOrganization()
	 */
	@Override
	public String getOrganization()
	{
		return this.contact.tlhapGhom();
	}

	/**
	 * @see adapter.Contact#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return this.contact.tlhapPatlh();
	}

	/**
	 * @param newContact {@link Chovnatlh}
	 */
	public void setContact(final Chovnatlh newContact)
	{
		this.contact = newContact;
	}

	/**
	 * @see adapter.Contact#setFirstName(java.lang.String)
	 */
	@Override
	public void setFirstName(final String newFirstName)
	{
		this.contact.cherWa$DIchPong(newFirstName);
	}

	/**
	 * @see adapter.Contact#setLastName(java.lang.String)
	 */
	@Override
	public void setLastName(final String newLastName)
	{
		this.contact.cherQavPong(newLastName);
	}

	/**
	 * @see adapter.Contact#setOrganization(java.lang.String)
	 */
	@Override
	public void setOrganization(final String newOrganization)
	{
		this.contact.cherGhom(newOrganization);
	}

	/**
	 * @see adapter.Contact#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(final String newTitle)
	{
		this.contact.cherPatlh(newTitle);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.contact.toString();
	}
}
