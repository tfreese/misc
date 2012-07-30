package transaction;

/**
 * @author Thomas Freese
 */
public class TransactionException extends Exception
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1413134260560632465L;

	/**
	 * Creates a new {@link TransactionException} object.
	 * 
	 * @param msg String
	 */
	public TransactionException(final String msg)
	{
		super(msg);
	}
}
