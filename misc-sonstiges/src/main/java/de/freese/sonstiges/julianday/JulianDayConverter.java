/**
 *
 */
package de.freese.sonstiges.julianday;

/**
 * UtilMethoden für den Julianischen Tag.
 *
 * @author Thomas Freese
 */
public final class JulianDayConverter
{
    /**
     * Berechnet den Wert des Julianischen Tages.
     *
     * @param day {@link Day}
     * @return int
     */
    public static int calculateJD(final Day day)
    {
        return calculateJD(day.getYear(), day.getMonth(), day.getDay());
    }

    /**
     * Berechnet den Wert des Julianischen Tages.
     *
     * @param year int
     * @param month int
     * @param day int
     * @return int
     */
    public static int calculateJD(final int year, final int month, final int day)
    {
        int y = ((240 * year) + (20 * month)) - 57;
        int a = (((((367 * y) / 240) * 4) - (7 * (y / 240))) + (4 * day)) / 4;
        int b = ((4 * a) - (3 * (y / 24000))) / 4;
        int jd = b + 1721115;

        return jd;
    }

    /**
     * Liefert einen lesbaren Wert des Jahres, Monats und Tag.
     *
     * @param day {@link Day}; 01.01.1970
     * @return int; 19700101
     */
    public static int calculateReadable(final Day day)
    {
        return (day.getYear() * 10000) + (day.getMonth() * 100) + day.getDay();
    }

    /**
     * Berechnet das Tagesobjekt aus dem Julianischen Wert.
     *
     * @param julianDay int
     * @return {@link Day}
     */
    public static Day createDayFromJD(final int julianDay)
    {
        int g = ((julianDay << 2) - 7468865) / 146097;
        int a = (julianDay + 1 + g) - (g >> 2);
        int b = a + 1524;
        int c = ((20 * b) - 2442) / 7305;
        int d = (1461 * c) >> 2;
        int e = (10000 * (b - d)) / 306001;
        int day = b - d - ((306001 * e) / 10000);
        int month = e < 14 ? e - 1 : e - 13;
        int year = month > 2 ? c - 4716 : c - 4715;

        return new Day(year, month, day);
    }

    /**
     * Berechnet das Tagesobjekt aus dem lesbaren Wert.
     *
     * @param readableDay int; 19700101
     * @return {@link Day}; 01.01.1970
     */
    public static Day createDayFromReadable(final int readableDay)
    {
        return new Day(readableDay / 10000, (readableDay % 10000) / 100, readableDay % 100);
    }

    /**
     * Erstellt ein neues {@link JulianDayConverter} Object.
     */
    private JulianDayConverter()
    {
        super();
    }
}
