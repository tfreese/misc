// Created: 01.06.2017
package de.freese.jsensors.sensor.cpu;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import de.freese.jsensors.sensor.AbstractSensor;
import de.freese.jsensors.utils.Utils;

/**
 * Sensor für die CPU Auslastung.
 *
 * @author Thomas Freese
 */
public class CpuUsageSensor extends AbstractSensor
{
    /**
     *
     */
    private CpuTimes cpuTimesPrevious;

    /**
     *
     */
    private final JavaSysMon monitor;

    /**
     * Erzeugt eine neue Instanz von {@link CpuUsageSensor}.
     *
     * @param name String
     */
    public CpuUsageSensor(final String name)
    {
        super(name);

        this.monitor = new JavaSysMon();

        this.cpuTimesPrevious = this.monitor.cpuTimes();
    }

    // /**
    // * Ausgabe von "wmic cpu get loadpercentage".<br>
    // * LoadPercentage<br>
    // * 4
    // *
    // * @return String
    // * @throws Exception Falls was schief geht.
    // */
    // private String getCpuUsageWindows() throws Exception
    // {
    // List<String> lines = Utils.executeCommand("wmic", "cpu", "get", "loadpercentage");
    //
    // String value = lines.stream().skip(1).findFirst().get();
    //
    // return value;
    // }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#scanValue()
     */
    @Override
    protected void scanValue() throws Exception
    {
        String value = null;

        CpuTimes cpuTimes = this.monitor.cpuTimes();
        float usage = cpuTimes.getCpuUsage(this.cpuTimesPrevious) * 100.0F;
        this.cpuTimesPrevious = cpuTimes;

        value = Utils.format(usage);

        // if (Utils.isWindows())
        // {
        // value = getCpuUsageWindows();
        // }
        // else
        // {
        // throw new IllegalStateException("unsupported operation system");
        // }

        save(value);
    }
}
