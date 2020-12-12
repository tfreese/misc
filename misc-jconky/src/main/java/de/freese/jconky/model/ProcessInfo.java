// Created: 07.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class ProcessInfo
{
    /**
     *
     */
    private final String command;

    /**
     *
     */
    private final double cpuUsage;

    /**
     *
     */
    private final String name;

    /**
     *
     */
    private final String owner;

    /**
     *
     */
    private final int parentPid;

    /**
     *
     */
    private final int pid;

    /**
     *
     */
    private final long residentBytes;

    /**
     *
     */
    private final String state;

    /**
     *
     */
    private final long totalBytes;

    /**
     * Erstellt ein neues {@link ProcessInfo} Object.
     *
     * @param pid int
     * @param parentPid int
     * @param state String
     * @param cpuUsage double
     * @param command String
     * @param name String
     * @param residentBytes long
     * @param totalBytes long
     * @param owner String
     */
    public ProcessInfo(final int pid, final int parentPid, final String state, final double cpuUsage, final String command, final String name,
            final long residentBytes, final long totalBytes, final String owner)
    {
        super();

        this.pid = pid;
        this.parentPid = parentPid;
        this.state = state;
        this.command = command;
        this.name = name;
        this.owner = owner;
        this.cpuUsage = cpuUsage;
        this.residentBytes = residentBytes;
        this.totalBytes = totalBytes;

        // CpuTimes cpuTimes = new CpuTimes(user, nice, system, idle, ioWait, irq, softIrq, steal, guest, guestNice)
    }

    /**
     * @return String
     */
    public String getCommand()
    {
        return this.command;
    }

    /**
     * @return double
     */
    public double getCpuUsage()
    {
        return this.cpuUsage;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return String
     */
    public String getOwner()
    {
        return this.owner;
    }

    /**
     * @return int
     */
    public int getParentPid()
    {
        return this.parentPid;
    }

    /**
     * @return int
     */
    public int getPid()
    {
        return this.pid;
    }

    /**
     * @return long
     */
    public long getResidentBytes()
    {
        return this.residentBytes;
    }

    /**
     * R Running<br>
     * S Sleeping in an interruptible wait<br>
     * D Waiting in uninterruptible disk sleep<br>
     * <br>
     * See: https://man7.org/linux/man-pages/man5/proc.5.html<br>
     *
     * @return String
     */
    public String getState()
    {
        return this.state;
    }

    /**
     * @return long
     */
    public long getTotalBytes()
    {
        return this.totalBytes;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append(" pid=").append(this.pid);
        builder.append(", name=").append(this.name);
        builder.append(", owner=").append(this.owner);
        builder.append(", cpuUsage=").append(this.cpuUsage);
        builder.append(", residentBytes=").append(this.residentBytes);
        builder.append(", totalBytes=").append(this.totalBytes);
        builder.append(", parentPid=").append(this.parentPid);
        builder.append(", command=").append(this.command);
        builder.append("]");

        return builder.toString();
    }
}
