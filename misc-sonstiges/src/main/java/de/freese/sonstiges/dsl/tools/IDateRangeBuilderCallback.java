/**
 * Created: 14.06.2012
 */

package de.freese.sonstiges.dsl.tools;

import java.util.Date;

/**
 * @author Thomas Freese
 */
public interface IDateRangeBuilderCallback
{
	/**
	 * @param from {@link Date}
	 * @param to {@link Date}
	 */
	public void setDateRange(Date from, Date to);
}
