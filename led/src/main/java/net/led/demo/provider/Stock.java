package net.led.demo.provider;

public class Stock
{

	private String id;

	private Double last;

	private Double changePercent;

	public Stock(final String id, final Double last, final Double changePercent)
	{
		this.id = id;
		this.last = last;
		this.changePercent = changePercent;
	}

	public String getID()
	{
		return this.id;
	}

	public Double getLast()
	{
		return this.last;
	}

	public void setLast(final Double newValue)
	{
		this.last = newValue;
	}

	public Double getChangePercent()
	{
		return this.changePercent;
	}

	public void setChangePercent(final Double newValue)
	{
		this.changePercent = newValue;
	}
}