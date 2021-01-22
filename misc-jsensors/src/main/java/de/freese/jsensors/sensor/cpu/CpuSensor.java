// Created: 01.06.2017
package de.freese.jsensors.sensor.cpu;

import java.util.List;
import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;
import de.freese.jsensors.sensor.AbstractSensor;
import de.freese.jsensors.utils.Utils;

/**
 * Sensor für die CPU Auslastung.
 *
 * @author Thomas Freese
 */
public class CpuSensor extends AbstractSensor
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
     * Erzeugt eine neue Instanz von {@link CpuSensor}.
     */
    public CpuSensor()
    {
        super();

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
     * @see de.freese.jsensors.sensor.Sensor#getNames()
     */
    @Override
    public List<String> getNames()
    {
        return List.of("cpu.usage");
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#measureImpl()
     */
    @Override
    protected void measureImpl() throws Exception
    {
        CpuTimes cpuTimes = this.monitor.cpuTimes();
        float usage = cpuTimes.getCpuUsage(this.cpuTimesPrevious) * 100.0F;
        this.cpuTimesPrevious = cpuTimes;

        // String value = Utils.format(usage);
        //
        // if (Utils.isWindows())
        // {
        // value = getCpuUsageWindows();
        // }
        // else
        // {
        // throw new IllegalStateException("unsupported operation system");
        // }

        store("cpu.usage", Utils.format(usage));
    }
}
