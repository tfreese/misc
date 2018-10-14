/**
 * 
 */
package de.freese.sonstiges.julianday;

import java.io.Serializable;

/**
 * Enthält die Daten des Tages (Year, Month, Day).
 * 
 * @author Thomas Freese
 */
public class Day implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4560714541840783730L;

	/**
	 * 
	 */
	private transient String asString = null;

	/**
	 * 
	 */
	private final int day;

	/**
	 * 
	 */
	private final int month;

	/**
	 * 
	 */
	private final int year;

	/**
	 * @param year int
	 * @param month int
	 * @param day int
	 */
	public Day(final int year, final int month, final int day)
	{
		super();

		this.year = year;
		this.month = month;
		this.day = day;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (obj == null)
		{
			return false;
		}

		if (!(obj instanceof Day))
		{
			return false;
		}

		Day other = (Day) obj;

		if (this.day != other.day)
		{
			return false;
		}

		if (this.month != other.month)
		{
			return false;
		}

		if (this.year != other.year)
		{
			return false;
		}

		return true;
	}

	/**
	 * @return int
	 */
	public int getDay()
	{
		return this.day;
	}

	/**
	 * @return int
	 */
	public int getMonth()
	{
		return this.month;
	}

	/**
	 * @return int
	 */
	public int getYear()
	{
		return this.year;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		// final int prime = 31;
		// int result = 1;
		// result = prime * result + this.day;
		// result = prime * result + this.month;
		// result = prime * result + this.year;
		//
		// return result;

		return (this.year * 400) + (this.month * 31) + this.day;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if (this.asString == null)
		{
			this.asString = String.format("%d-%02d-%02d", this.year, this.month, this.day);
		}

		return this.asString;
	}
}
