package net.ledticker;

public class LedTickerFactory
{

	public LedTickerFactory()
	{
	}

	public static LedTicker createLedTicker()
	{
		return new DefaultLedTicker();
	}
}
