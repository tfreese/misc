package net.led.elements;

import java.awt.Color;

public class DefaultColorModel implements ColorModel
{

	public DefaultColorModel()
	{
		this(Color.white);
	}

	public DefaultColorModel(final Color color)
	{
		this.b = color;
	}

	public Color getColor()
	{
		return this.b;
	}

	public void setColor(final Color color)
	{
		this.b = color;
	}

	private Color b;
}
