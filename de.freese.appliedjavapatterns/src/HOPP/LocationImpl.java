package HOPP;

/**
 * @author Thomas Freese
 */
public class LocationImpl implements Location
{
	/**
	 *
	 */
	private static final long serialVersionUID = 143261281765601433L;

	/**
     * 
     */
	private String location;

	/**
	 * Creates a new {@link LocationImpl} object.
	 */
	public LocationImpl()
	{
		super();
	}

	/**
	 * Creates a new {@link LocationImpl} object.
	 * 
	 * @param newLocation {@link String}
	 */
	public LocationImpl(final String newLocation)
	{
		super();

		this.location = newLocation;
	}

	/**
	 * @see HOPP.Location#getLocation()
	 */
	@Override
	public String getLocation()
	{
		return this.location;
	}

	/**
	 * @see HOPP.Location#setLocation(java.lang.String)
	 */
	@Override
	public void setLocation(final String newLocation)
	{
		this.location = newLocation;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.location;
	}
}
