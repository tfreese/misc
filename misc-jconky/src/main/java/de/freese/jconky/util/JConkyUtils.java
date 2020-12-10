// Created: 10.12.2020
package de.freese.jconky.util;

/**
 * @author Thomas Freese
 */
public final class JConkyUtils
{
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
     * Erstellt ein neues {@link JConkyUtils} Object.
     */
    private JConkyUtils()
    {
        super();
    }
}
