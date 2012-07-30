package de.freese.persistence.jdbc.selecttransaction;

/**
 * Exception-Klasse des Frameworks Select-Transaction
 * 
 * @author Thomas Freese
 */
public class SelectTransactionException extends Exception
{
	/**
	 *
	 */
	private static final long serialVersionUID = 3024015314447024153L;

	/**
	 * Erstellt ein neues {@link SelectTransactionException} Object.
	 */
	public SelectTransactionException()
	{
		super();
	}

	/**
	 * Creates a new {@link SelectTransactionException} object.
	 * 
	 * @param message String
	 */
	public SelectTransactionException(final String message)
	{
		super(message);
	}

	/**
	 * Creates a new {@link SelectTransactionException} object.
	 * 
	 * @param message String
	 * @param cause {@link Throwable}
	 */
	public SelectTransactionException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Creates a new {@link SelectTransactionException} object.
	 * 
	 * @param cause {@link Throwable}
	 */
	public SelectTransactionException(final Throwable cause)
	{
		super(cause);
	}
}
