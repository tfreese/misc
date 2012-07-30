package net.led.demo.elements;

import java.awt.Color;

import net.led.elements.ColorModel;

public class StockColorModel implements ColorModel
{

	private Color upColor;

	private Color neutralColor;

	private Color downColor;

	private Color color;

	public StockColorModel()
	{
		this.upColor = Color.green;
		this.neutralColor = Color.yellow;
		this.downColor = Color.red;
		this.color = this.neutralColor;
	}

	@Override
	public Color getColor()
	{
		return this.color;
	}

	public void setChangePercent(final double changePercent)
	{
		if (changePercent > 0)
		{
			this.color = this.upColor;
		}
		else if (changePercent < 0)
		{
			this.color = this.downColor;
		}
		else
		{
			this.color = this.neutralColor;
		}
	}

	@Override
	public void setColor(final Color color)
	{
		// Empty
	}

	public void setDownColor(final Color c)
	{
		if (this.color.equals(this.downColor))
		{
			this.color = c;
		}
		this.downColor = c;
	}

	public void setNeutralColor(final Color c)
	{
		if (this.color.equals(this.neutralColor))
		{
			this.color = c;
		}
		this.neutralColor = c;
	}

	public void setUpColor(final Color c)
	{
		if (this.color.equals(this.upColor))
		{
			this.color = c;
		}
		this.upColor = c;
	}
}