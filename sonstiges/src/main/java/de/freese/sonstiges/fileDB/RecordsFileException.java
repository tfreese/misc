package de.freese.sonstiges.fileDB;

/**
 * @author Thomas Freese
 */
public class RecordsFileException extends Exception
{
	/**
	 *
	 */
	private static final long serialVersionUID = -3689848156255124650L;

	/**
	 * Erstellt ein neues {@link RecordsFileException} Object.
	 * 
	 * @param msg String
	 */
	public RecordsFileException(final String msg)
	{
		super(msg);
	}
}
