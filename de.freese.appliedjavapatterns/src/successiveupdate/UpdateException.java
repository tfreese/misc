package successiveupdate;

/**
 * @author Thomas Freese
 */
public class UpdateException extends Exception
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7583094463331148231L;

	/**
     * 
     */
	public static final int TASK_UNCHANGED = 1;

	/**
     * 
     */
	public static final int TASK_OUT_OF_DATE = 2;

	/**
     * 
     */
	private int errorCode;

	/**
	 * Creates a new {@link UpdateException} object.
	 * 
	 * @param cause String
	 */
	public UpdateException(final String cause)
	{
		super(cause);
	}

	/**
	 * Creates a new {@link UpdateException} object.
	 * 
	 * @param cause String
	 * @param newErrorCode int
	 */
	public UpdateException(final String cause, final int newErrorCode)
	{
		super(cause);
		this.errorCode = newErrorCode;
	}

	/**
	 * @return int
	 */
	public int getErrorCode()
	{
		return this.errorCode;
	}
}
