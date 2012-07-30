package net.leddisplay;

public class LedDisplayFactory
{

	public LedDisplayFactory()
	{
	}

	public static LedDisplay createLedDisplay()
	{
		return new DefaultLedDisplay();
	}
}
