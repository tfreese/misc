package builder;

/**
 * @author Thomas Freese
 */
public class LocationImpl implements Location
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7468177924339125783L;

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
	 * @param newLocation String
	 */
	public LocationImpl(final String newLocation)
	{
		super();

		this.location = newLocation;
	}

	/**
	 * @see builder.Location#getLocation()
	 */
	@Override
	public String getLocation()
	{
		return this.location;
	}

	/**
	 * @see builder.Location#setLocation(java.lang.String)
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
