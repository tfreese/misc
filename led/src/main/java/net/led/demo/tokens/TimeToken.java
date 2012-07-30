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
public class TimeToken extends Token
{
	/**
	 * The representation of the number.
	 */
	private String displayValue;

	/**
	 * The number representation's format
	 */
	private DateFormat timeFormatter;

	/**
	 * The represented value
	 */
	protected Date value;

	/**
	 * Creates a <tt>NumberToken</tt> with a default <tt>NumberFormat</tt> using the
	 * <tt>ENGLISH</tt> <tt>Locale</tt> and a two fraction digit representation
	 */
	public TimeToken()
	{
	}

	/**
	 * Creates a <tt>NumberToken</tt> with a specific ColorModel
	 * 
	 * @param colorModel the ColorModel
	 */
	public TimeToken(final ColorModel colorModel)
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
				(this.value instanceof Date) ? getTimeFormatter().format(this.value) : "N/A";
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
	 * @return Formatierer für Datumsangaben.
	 */
	private DateFormat getTimeFormatter()
	{
		if (this.timeFormatter == null)
		{
			this.timeFormatter = DateFormat.getTimeInstance(DateFormat.LONG, Locale.getDefault());
		}

		return this.timeFormatter;
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
