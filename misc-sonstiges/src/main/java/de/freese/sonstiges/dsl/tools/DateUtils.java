//Created: 14.06.2012
package de.freese.sonstiges.dsl.tools;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Thomas Freese
 */
public final class DateUtils
{
    /**
     * @param year  int
     * @param month int
     * @param day   int
     *
     * @return {@link Date}
     */
    private static Date createDate(final int year, final int month, final int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    /**
     * @return {@link Date}
     */
    public static Date infinity()
    {
        return createDate(9999, 11, 31);
    }

    /**
     * @return {@link Date}
     */
    public static Date today()
    {
        return new Date();
    }

    /**
     * @return {@link Date}
     */
    public static Date tomorrow()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        return calendar.getTime();
    }
}
