package net.led.demo.elements.ticker;

import java.awt.Color;

import net.led.demo.tokens.TextToken;
import net.led.elements.Token;

public class TextTickerElement extends AbstractTickerElement
{

	private TextToken textToken;

	/**
	 * Creates a <tt>TextTickerElement</tt> with a given display value
	 * 
	 * @param text the display value
	 */
	public TextTickerElement(final String text)
	{
		super(new Token[1]);
		this.textToken = new TextToken(text);

		this.tokens[0] = this.textToken;
	}

	/**
	 * Sets the display value
	 * 
	 * @param text the new display value
	 */
	public void setText(final String text)
	{
		this.textToken.setValue(text);
	}

	/**
	 * Sets the color for the display value
	 * 
	 * @param color the new color for the display value
	 */
	public void setColor(final Color color)
	{
		this.textToken.getColorModel().setColor(color);
	}
}