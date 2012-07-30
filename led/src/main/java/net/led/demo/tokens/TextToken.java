package net.led.demo.tokens;

import net.led.elements.ColorModel;
import net.led.elements.Token;

/**
 * A token representing a line of text
 * 
 * @version 1.0 12/14/04
 */
public class TextToken extends Token
{

	/**
	 * The token's text
	 */
	private String text;

	/**
	 * Creates a TextToken with no display value
	 */
	public TextToken()
	{
		this("");
	}

	/**
	 * @param newValue the display text of the TextToken
	 */
	public TextToken(final String newValue)
	{
		this.text = newValue;
	}

	/**
	 * Creates a TextToken with a display value and a ColorModel
	 * 
	 * @param text the display value
	 * @param colorModel the ColorModel
	 */
	public TextToken(final String text, final ColorModel colorModel)
	{
		this(text);
		setColorModel(colorModel);
	}

	/**
	 * @see net.led.elements.Token#getDisplayValue()
	 */
	public String getDisplayValue()
	{
		return this.text;
	}

	/**
	 * @see net.led.elements.Token#setValue(java.lang.Object)
	 */
	public void setValue(final Object value)
	{
		if (value == null)
		{
			throw new NullPointerException("Given value cannot be null");
		}
		if (value instanceof String)
		{
			this.text = (String) value;
			return;
		}

		throw new IllegalArgumentException("Given value must be a String, not "
				+ value.getClass().getName());
	}
}