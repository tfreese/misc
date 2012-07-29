package router;

import java.io.Serializable;

/**
 * @author Thomas Freese
 */
public class Message implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6670974108035897043L;

	/**
	 * 
	 */
	private String message;

	/**
	 * 
	 */
	private InputChannel source;

	/**
	 * Creates a new Message object.
	 * 
	 * @param source {@link InputChannel}
	 * @param message String
	 */
	public Message(final InputChannel source, final String message)
	{
		super();

		this.source = source;
		this.message = message;
	}

	/**
	 * @return String
	 */
	public String getMessage()
	{
		return this.message;
	}

	/**
	 * @return {@link InputChannel}
	 */
	public InputChannel getSource()
	{
		return this.source;
	}
}
