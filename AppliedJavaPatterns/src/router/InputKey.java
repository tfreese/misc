package router;

/**
 * @author Thomas Freese
 */
public class InputKey implements InputChannel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8565281783364892998L;

	/**
	 * 
	 */
	private static int nextValue = 1;

	/**
	 * 
	 */
	private int hashVal = nextValue++;

	/**
	 * Erstellt ein neues {@link InputKey} Object.
	 */
	InputKey()
	{
		super();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof InputKey))
		{
			return false;
		}

		if (object.hashCode() != hashCode())
		{
			return false;
		}

		return true;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return this.hashVal;
	}
}
