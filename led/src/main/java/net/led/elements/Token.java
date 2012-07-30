package net.led.elements;

public abstract class Token
{

	public Token()
	{
		this(((new DefaultColorModel())));
	}

	public Token(final ColorModel colormodel)
	{
		setColorModel(colormodel);
	}

	public ColorModel getColorModel()
	{
		return this.b;
	}

	public void setColorModel(ColorModel colormodel)
	{
		if (colormodel == null)
		{
			colormodel = new DefaultColorModel();
		}
		this.b = colormodel;
	}

	public abstract String getDisplayValue();

	public abstract void setValue(Object obj);

	private ColorModel b;
}
