// Created: 01.06.2017
package de.freese.jsensors.sensor.memory;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;

import de.freese.jsensors.Utils;
import de.freese.jsensors.sensor.AbstractSensor;

/**
 * Sensor für die Speicher Auslastung.
 *
 * @author Thomas Freese
 */
public class MemoryUsage extends AbstractSensor
{
    /**
     *
     */
    private JavaSysMon monitor = null;

    /**
     * Erzeugt eine neue Instanz von {@link MemoryUsage}.
     */
    public MemoryUsage()
    {
        super();
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#initialize()
     */
    @Override
    protected void initialize() throws Exception
    {
        super.initialize();

        this.monitor = new JavaSysMon();
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#scanValue()
     */
    @Override
    protected void scanValue() throws Exception
    {
        MemoryStats stats = this.monitor.physical();
        double free = stats.getFreeBytes();
        double total = stats.getTotalBytes();
        double usage = (1.0D - (free / total)) * 100.0D;

        String value = Utils.format(usage);

        save(value);
    }
}
