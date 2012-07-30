package net.led.demo.elements.display;

import java.awt.Color;

import net.led.demo.elements.StockColorModel;
import net.led.demo.tokens.NumberToken;
import net.led.demo.tokens.TextToken;
import net.led.elements.ArrowToken;
import net.led.elements.DefaultColorModel;
import net.led.elements.Token;

/**
 * This is a sample element that extends <code>AbstractDisplayElement</code> - the abstrat class
 * that represents a display's text base element.
 * <p>
 * The purpose of this element is to hold stock quote information: name, last value, trend and
 * percent change. The trend is given by the percent change relative to 0.
 */
public class StockDisplayElement extends AbstractDisplayElement
{
	private TextToken symbol;

	private NumberToken last;

	private ArrowToken arrow;

	private NumberToken changePercent;

	private StockColorModel stockColorModel;

	/**
	 * Creates a <tt>StockDisplayElement</tt> with the given name
	 * 
	 * @param stock the stock's name
	 */
	public StockDisplayElement(final String stock)
	{
		super(new Token[4]);

		this.symbol = new TextToken(stock, new DefaultColorModel(new Color(0xffffff)));
		this.stockColorModel = new StockColorModel();
		this.last = new NumberToken(this.stockColorModel);
		this.arrow = new ArrowToken(this.stockColorModel);
		this.changePercent = new NumberToken(this.stockColorModel);

		this.tokens[0] = this.symbol;
		this.tokens[1] = this.last;
		this.tokens[2] = this.arrow;
		this.tokens[3] = this.changePercent;
	}

	/**
	 * Sets the last value of the stock
	 * 
	 * @param last the last value of the stock
	 */
	public void setLast(final Double lastValue)
	{
		this.last.setValue(lastValue);
	}

	/**
	 * Sets the percent change of the stock
	 * 
	 * @param change the percent change of the stock
	 */
	public void setChangePercent(final Double change)
	{
		this.changePercent.setValue(change);
		this.stockColorModel.setChangePercent(change.doubleValue());
		if (change.doubleValue() > 0)
		{
			this.arrow.setValue(ArrowToken.INCREASING);
		}
		else if (change.doubleValue() < 0)
		{
			this.arrow.setValue(ArrowToken.DECREASING);
		}
		else
		{
			this.arrow.setValue(ArrowToken.UNCHANGED);
		}
	}

	/**
	 * Gets the name of the stock
	 * 
	 * @return the name of the stock
	 */
	public String getSymbol()
	{
		return this.symbol.getDisplayValue();
	}

	/**
	 * Sets the color to use when representing tokens on an upward trend
	 * 
	 * @param c the new upward trend color
	 */
	public void setStockUpColor(final Color c)
	{
		this.stockColorModel.setUpColor(c);
	}

	/**
	 * Sets the color to use when representing tokens on a constant trend
	 * 
	 * @param c the new constant trend color
	 */
	public void setStockNeutralColor(final Color c)
	{
		this.stockColorModel.setNeutralColor(c);
	}

	/**
	 * Sets the color to use when representing tokens on a downward trend
	 * 
	 * @param c the new downward trend color
	 */
	public void setStockDownColor(final Color c)
	{
		this.stockColorModel.setDownColor(c);
	}

	/**
	 * Sets the color of the symbol
	 * 
	 * @param c the new symbol color
	 */
	public void setSymbolColor(final Color c)
	{
		this.symbol.getColorModel().setColor(c);
	}
}