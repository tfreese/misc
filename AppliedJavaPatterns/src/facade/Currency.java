package facade;

import java.text.NumberFormat;

/**
 * @author Thomas Freese
 */
public class Currency
{
	/**
     * 
     */
	private char currencySymbol;

	/**
     * 
     */
	private NumberFormat numberFormat;

	/**
	 * Erstellt ein neues {@link Currency} Object.
	 */
	Currency()
	{
		super();
	}

	/**
	 * @return char
	 */
	public char getCurrencySymbol()
	{
		return this.currencySymbol;
	}

	/**
	 * @return {@link NumberFormat}
	 */
	public NumberFormat getNumberFormat()
	{
		return this.numberFormat;
	}

	/**
	 * @param newCurrencySymbol char
	 */
	public void setCurrencySymbol(final char newCurrencySymbol)
	{
		this.currencySymbol = newCurrencySymbol;
	}

	/**
	 * @param newNumberFormat {@link NumberFormat}
	 */
	public void setNumberFormat(final NumberFormat newNumberFormat)
	{
		this.numberFormat = newNumberFormat;
	}
}
