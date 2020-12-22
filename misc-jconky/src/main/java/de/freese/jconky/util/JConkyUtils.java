// Created: 10.12.2020
package de.freese.jconky.util;

/**
 * @author Thomas Freese
 */
public final class JConkyUtils
{
    /**
    *
    */
    private static final String[] SIZE_UNITS = new String[]
    {
            "B", "K", "M", "G", "T"
    };

    /**
     * Periodendauer des Timer-Interrupts.<br>
     * Betriebssystem spezifischer Faktor, bei Linux in der Regel 100.<br>
     * ArchLinux: getconf CLK_TCK;<br>
     */
    public static final int USER_HZ = 100;

    /**
     * @param jiffie double
     * @return double
     */
    public static double jiffieToMillies(final double jiffie)
    {
        return jiffieToMillies(jiffie, USER_HZ);
    }

    /**
     * @param jiffie double
     * @param userHz int Betriebssystem spezifischer Faktor, bei Linux in der Regel 100.<br>
     *            ArchLinux: getconf CLK_TCK;<br>
     * @return double
     */
    public static double jiffieToMillies(final double jiffie, final int userHz)
    {
        double multiplier = 1000D / userHz;

        return jiffie * multiplier;
    }

    /**
     * @param jiffie double
     * @return double
     */
    public static double jiffieToSeconds(final double jiffie)
    {
        return jiffieToSeconds(jiffie, USER_HZ);
    }

    /**
     * @param jiffie double
     * @param userHz int Betriebssystem spezifischer Faktor, bei Linux in der Regel 100.<br>
     *            ArchLinux: getconf CLK_TCK;<br>
     * @return double
     */
    public static double jiffieToSeconds(final double jiffie, final int userHz)
    {
        return jiffie / userHz;
    }

    /**
     * @param size double
     * @return String, z.B. '___,___ MB'
     */
    public static String toHumanReadableSize(final double size)
    {
        return toHumanReadableSize(size, "%.1f %s");

        // double divider = 1D;
        // String unit = "";
        //
        // if (size <= 1024)
        // {
        // divider = 1D;
        // unit = "B";
        // }
        // else if (size <= 1_048_576)
        // {
        // divider = 1024D;
        // unit = "KB";
        // }
        // else if (size <= 1_073_741_824)
        // {
        // divider = 1_048_576D;
        // unit = "MB";
        // }
        // else if (size <= (1_048_576 * 1_048_576))
        // {
        // divider = 1_073_741_824D;
        // unit = "GB";
        // }
        //
        // String readableSize = String.format("%.1f %s", size / divider, unit);
        //
        // return readableSize;
    }

    /**
     * @param size double
     * @param format String
     * @return String, z.B. '___,___ MB'
     */
    public static String toHumanReadableSize(final double size, final String format)
    {
        int unitIndex = 0;

        if (size > 0)
        {
            unitIndex = (int) (Math.log10(size) / 3);
        }

        double unitValue = 1 << (unitIndex * 10);

        // // String readableSize = new DecimalFormat("#,##0.#").format(size / unitValue) + " " + SIZE_UNITS[unitIndex];
        // // String readableSize = String.format("%7.0f %s", size / unitValue, SIZE_UNITS[unitIndex]);
        String readableSize = String.format(format, size / unitValue, SIZE_UNITS[unitIndex]);

        return readableSize;
    }

    /**
     * Erstellt ein neues {@link JConkyUtils} Object.
     */
    private JConkyUtils()
    {
        super();
    }
}
