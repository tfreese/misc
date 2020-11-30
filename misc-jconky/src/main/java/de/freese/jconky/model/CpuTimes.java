// Created: 30.11.2020
package de.freese.jconky.model;

/**
 * cat /proc/stat<br>
 * cpu 247721 450 70350 2534219 43469 8434 2372 0 0 0<br>
 * cpu0 32043 74 7737 310311 11885 570 673 0 0 0<br>
 * cpu1 26344 48 11872 312233 6875 5683 232 0 0 0<br>
 * [...]<br>
 * user nice system idle iowait irq softirq steal guest guest_nice<br>
 * <br>
 * Diese Zahlen sind soggenannte Jiffies.<br>
 * Ein Jiffie ist der Anteil eines CPU-Zyklus, der für die Ausführung eines Befehls benötigt wurde.<br>
 * Oder auch benannt als: Periodendauer des Timer-Interrupts<br>
 *
 * @author Thomas Freese
 */
public class CpuTimes
{
    /**
     * @param jiffie long
     * @return long
     */
    public static long jiffieToMillies(final long jiffie)
    {
        return jiffieToMillies(jiffie, 100);
    }

    /**
     * @param jiffie long
     * @param userHz int Betriebssystem spezifischer Faktor, bei Linux in der Regel 100.<br>
     *            ArchLinux: getconf CLK_TCK;<br>
     * @return long
     */
    public static long jiffieToMillies(final long jiffie, final int userHz)
    {
        int multiplier = 1000 / userHz;

        return jiffie * multiplier;
    }

    /**
     *
     */
    private long guest;

    /**
     *
     */
    private long guestNice;

    /**
     *
     */
    private long idle;

    /**
     *
     */
    private long ioWait;

    /**
     *
     */
    private long irq;

    /**
     *
     */
    private long nice;

    /**
     *
     */
    private long softIrq;

    /**
     *
     */
    private long steal;

    /**
     *
     */
    private long system;

    /**
     *
     */
    private long user;

    /**
     * Erstellt ein neues {@link CpuTimes} Object.
     */
    public CpuTimes()
    {
        super();
    }

    /**
     * Liefert die CPU-Auslastung in %.
     *
     * @param previous {@link CpuTimes}
     * @return double
     */
    public double getCpuUsage(final CpuTimes previous)
    {
        double totalDiff = (double) getTotal() - previous.getTotal();
        double idleDiff = (double) getTotalIdle() - previous.getTotalIdle();

        double percent = 1D - (idleDiff / totalDiff);

        return percent * 100D;
    }

    /**
     * @return long
     */
    public long getGuest()
    {
        return this.guest;
    }

    /**
     * @return long
     */
    public long getGuestNice()
    {
        return this.guestNice;
    }

    /**
     * @return long
     */
    public long getIdle()
    {
        return this.idle;
    }

    /**
     * @return long
     */
    public long getIoWait()
    {
        return this.ioWait;
    }

    /**
     * @return long
     */
    public long getIrq()
    {
        return this.irq;
    }

    /**
     * @return long
     */
    public long getNice()
    {
        return this.nice;
    }

    /**
     * @return long
     */
    public long getSoftIrq()
    {
        return this.softIrq;
    }

    /**
     * @return long
     */
    public long getSteal()
    {
        return this.steal;
    }

    /**
     * @return long
     */
    public long getSystem()
    {
        return this.system;
    }

    /**
     * @return long
     */
    public long getTotal()
    {
        return getTotalIdle() + getTotalNonIdle();
    }

    /**
     * @return long
     */
    public long getTotalIdle()
    {
        return getIdle() + getIoWait();
    }

    /**
     * @return long
     */
    public long getTotalNonIdle()
    {
        return getUser() + getNice() + getSystem() + getIrq() + getSoftIrq() + getSteal();
    }

    /**
     * @return long
     */
    public long getUser()
    {
        return this.user;
    }

    /**
     * @param guest long
     */
    public void setGuest(final long guest)
    {
        this.guest = guest;
    }

    /**
     * @param guestNice long
     */
    public void setGuestNice(final long guestNice)
    {
        this.guestNice = guestNice;
    }

    /**
     * @param idle long
     */
    public void setIdle(final long idle)
    {
        this.idle = idle;
    }

    /**
     * @param ioWait long
     */
    public void setIoWait(final long ioWait)
    {
        this.ioWait = ioWait;
    }

    /**
     * @param irq long
     */
    public void setIrq(final long irq)
    {
        this.irq = irq;
    }

    /**
     * @param nice long
     */
    public void setNice(final long nice)
    {
        this.nice = nice;
    }

    /**
     * @param softIrq long
     */
    public void setSoftIrq(final long softIrq)
    {
        this.softIrq = softIrq;
    }

    /**
     * @param steal long
     */
    public void setSteal(final long steal)
    {
        this.steal = steal;
    }

    /**
     * @param system long
     */
    public void setSystem(final long system)
    {
        this.system = system;
    }

    /**
     * @param user long
     */
    public void setUser(final long user)
    {
        this.user = user;
    }
}
