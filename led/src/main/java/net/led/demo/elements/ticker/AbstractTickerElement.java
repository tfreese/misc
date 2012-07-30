package net.led.demo.elements.ticker;

import net.led.elements.Element;
import net.led.elements.Token;

/**
 * Abstract implementation of the {@overload.ledticker.elements.TickerElement
 * 
 * } interface
 */
public abstract class AbstractTickerElement implements Element
{

	/**
	 * The element's tokens
	 */
	protected Token[] tokens;

	/**
	 * Creates an element without tokens
	 */
	protected AbstractTickerElement(final Token[] newValue)
	{
		if (newValue == null)
		{
			throw new IllegalArgumentException("tokens array is null");
		}
		this.tokens = newValue;
	}

	/**
	 * Gets the element's tokens
	 * 
	 * @return the element's tokens
	 */
	public Token[] getTokens()
	{
		return this.tokens;
	}
}