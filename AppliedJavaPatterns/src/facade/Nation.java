package facade;

import java.text.NumberFormat;

/**
 * @author Thomas Freese
 */
public class Nation
{
	/**
     * 
     */
	private String dialingPrefix;

	/**
     * 
     */
	private String name;

	/**
     * 
     */
	private NumberFormat numberFormat;

	/**
     * 
     */
	private String propertyFileName;

	/**
     * 
     */
	private char symbol;

	/**
	 * Creates a new {@link Nation} object.
	 * 
	 * @param newName String
	 * @param newSymbol char
	 * @param newDialingPrefix String
	 * @param newPropertyFileName String
	 * @param newNumberFormat {@link NumberFormat}
	 */
	public Nation(final String newName, final char newSymbol, final String newDialingPrefix,
			final String newPropertyFileName, final NumberFormat newNumberFormat)
	{
		super();

		this.name = newName;
		this.symbol = newSymbol;
		this.dialingPrefix = newDialingPrefix;
		this.propertyFileName = newPropertyFileName;
		this.numberFormat = newNumberFormat;
	}

	/**
	 * @return String
	 */
	public String getDialingPrefix()
	{
		return this.dialingPrefix;
	}

	/**
	 * @return String
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return {@link NumberFormat}
	 */
	public NumberFormat getNumberFormat()
	{
		return this.numberFormat;
	}

	/**
	 * @return String
	 */
	public String getPropertyFileName()
	{
		return this.propertyFileName;
	}

	/**
	 * @return char
	 */
	public char getSymbol()
	{
		return this.symbol;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.name;
	}
}
