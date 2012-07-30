package transaction;

/**
 * @author Thomas Freese
 */
public class LocationImpl implements Location
{
	/**
	 *
	 */
	private static final long serialVersionUID = 7692632182165572710L;

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
	 * @see transaction.Location#getLocation()
	 */
	@Override
	public String getLocation()
	{
		return this.location;
	}

	/**
	 * @see transaction.Location#setLocation(java.lang.String)
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
