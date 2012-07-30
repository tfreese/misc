package de.freese.persistence.jdbc.state;

import de.freese.persistence.exception.PersistenceException;

/**
 * Created on 09.01.2004
 * 
 * @author Thomas Freese
 */
public class IllegalPersistenceObjectStateException extends PersistenceException
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4955180716438849708L;

	/**
	 * Erstellt ein neues {@link IllegalPersistenceObjectStateException} Object.
	 */
	public IllegalPersistenceObjectStateException()
	{
		super();
	}

	/**
	 * Erstellt ein neues {@link IllegalPersistenceObjectStateException} Object.
	 * 
	 * @param message String
	 */
	public IllegalPersistenceObjectStateException(final String message)
	{
		super(message);
	}

	/**
	 * Erstellt ein neues {@link IllegalPersistenceObjectStateException} Object.
	 * 
	 * @param message String
	 * @param cause {@link Throwable}
	 */
	public IllegalPersistenceObjectStateException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Erstellt ein neues {@link IllegalPersistenceObjectStateException} Object.
	 * 
	 * @param cause {@link Throwable}
	 */
	public IllegalPersistenceObjectStateException(final Throwable cause)
	{
		super(cause);
	}
}
