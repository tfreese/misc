package net.led.elements;

public final class ArrowToken extends Token
{

	public ArrowToken()
	{
		setValue(UNCHANGED);
	}

	public ArrowToken(final ColorModel colormodel)
	{
		this();
		setColorModel(colormodel);
	}

	public Object getArrowType()
	{
		return this.g;
	}

	public String getDisplayValue()
	{
		return "";
	}

	public void setValue(final Object obj)
	{
		this.g = obj;
	}

	public static final Object INCREASING = new Object();

	public static final Object DECREASING = new Object();

	public static final Object UNCHANGED = new Object();

	private Object g;

}
