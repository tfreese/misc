package net.led.demo.tokens;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import net.led.elements.ColorModel;
import net.led.elements.Token;

/**
 * A token representing a number
 * 
 * @version 1.0 12/14/04
 */
public class DateToken extends Token
{
	/**
	 * The representation of the number.
	 */
	private String displayValue;

	/**
	 * The number representation's format
	 */
	private DateFormat dateFormatter;

	/**
	 * The represented value
	 */
	protected Date value;

	/**
	 * Creates a <tt>NumberToken</tt> with a default <tt>NumberFormat</tt> using the
	 * <tt>ENGLISH</tt> <tt>Locale</tt> and a two fraction digit representation
	 */
	public DateToken()
	{
	}

	/**
	 * Creates a <tt>NumberToken</tt> with a specific ColorModel
	 * 
	 * @param colorModel the ColorModel
	 */
	public DateToken(final ColorModel colorModel)
	{
		this();
		setColorModel(colorModel);
	}

	/**
	 * Formats the token's display value
	 */
	private void formatDisplayValue()
	{
		this.displayValue =
				(this.value instanceof Date) ? getDateFormatter().format(this.value) : "N/A";
	}

	/**
	 * @return Formatierer für Datumsangaben.
	 */
	private DateFormat getDateFormatter()
	{
		if (this.dateFormatter == null)
		{
			this.dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
		}

		return this.dateFormatter;
	}

	/**
	 * Gets the representation of the number
	 * 
	 * @see net.led.elements.Token#getDisplayValue()
	 */
	@Override
	public String getDisplayValue()
	{
		return this.displayValue;
	}

	/**
	 * Sets the value of the token
	 * 
	 * @see net.led.elements.Token#setValue(java.lang.Object)
	 * @throws IllegalArgumentException if the given value is not a <tt>Number</tt>
	 */
	@Override
	public void setValue(final Object newValue)
	{
		if (newValue instanceof Date)
		{
			this.value = (Date) newValue;
			formatDisplayValue();

			return;
		}

		throw new IllegalArgumentException("Given value must be a java.lang.Number, not "
				+ newValue.getClass().getName());
	}
}
