package callback;

import java.io.Serializable;

/**
 * @author Thomas Freese
 */
public class Command implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -2319638946563133854L;

	/**
     * 
     */
	public static final int GET_PROJECT = 1;

	/**
     * 
     */
	public static final int GET_TASK = 2;

	/**
     * 
     */
	public static final int CREATE_CONTACT = 4;

	/**
     * 
     */
	public static final int ADD_ADDRESS = 8;

	/**
     * 
     */
	public static final int REMOVE_ADDRESS = 16;

	/**
     * 
     */
	public static final int FINALIZE_CONTACT = 32;

	/**
     * 
     */
	private Object[] arguments;

	/**
     * 
     */
	private int command;

	/**
	 * Creates a new {@link Command} object.
	 * 
	 * @param name int
	 * @param argumentList Object[]
	 */
	public Command(final int name, final Object[] argumentList)
	{
		super();

		this.command = name;
		this.arguments = argumentList;
	}

	/**
	 * @return Object[]
	 */
	public Object[] getArguments()
	{
		return this.arguments;
	}

	/**
	 * @return int
	 */
	public int getCommand()
	{
		return this.command;
	}

	/**
	 * @param newArguments Object[]
	 */
	public void setArguments(final Object[] newArguments)
	{
		this.arguments = newArguments;
	}

	/**
	 * @param newCommand int
	 */
	public void setCommand(final int newCommand)
	{
		this.command = newCommand;
	}
}
