/**
 * Created: 14.06.2012
 */

package de.freese.sonstiges.dsl.tools;

import java.util.Date;

/**
 * @author Thomas Freese
 * @param <T> Konkreter Objekttyp
 */
public class DateRangeBuilder<T>
{
	/**
	 * 
	 */
	private final Date from;

	/**
	 * 
	 */
	private final T caller;

	/**
	 * 
	 */
	private final IDateRangeBuilderCallback callback;

	/**
	 * Erstellt ein neues {@link DateRangeBuilder} Object.
	 * 
	 * @param from {@link Date}
	 * @param caller Object
	 * @param callback {@link IDateRangeBuilderCallback}
	 */
	public DateRangeBuilder(final Date from, final T caller,
			final IDateRangeBuilderCallback callback)
	{
		super();

		this.from = from;
		this.caller = caller;
		this.callback = callback;
	}

	/**
	 * @param to {@link Date}
	 * @return Object
	 */
	public T to(final Date to)
	{
		this.callback.setDateRange(this.from, to);

		return this.caller;
	}
}