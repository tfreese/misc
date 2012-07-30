package session;

/**
 * @author Thomas Freese
 */
public class SessionException extends Exception
{
	/**
	 *
	 */
	private static final long serialVersionUID = -4702150832796509608L;

	/**
     * 
     */
	public static final int CONTACT_BEING_EDITED = 1;

	/**
     * 
     */
	public static final int SESSION_ID_REQUIRED = 2;

	/**
     * 
     */
	public static final int CONTACT_SELECT_REQUIRED = 3;

	/**
     * 
     */
	public static final int ADDRESS_DOES_NOT_EXIST = 4;

	/**
     * 
     */
	private int errorCode = 0;

	/**
	 * Creates a new {@link SessionException} object.
	 * 
	 * @param cause String
	 */
	public SessionException(final String cause)
	{
		super(cause);
	}

	/**
	 * Creates a new {@link SessionException} object.
	 * 
	 * @param cause String
	 * @param newErrorCode int
	 */
	public SessionException(final String cause, final int newErrorCode)
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
