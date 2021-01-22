// Created: 01.06.2017
package de.freese.jsensors.sensor.memory;

import java.util.List;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;
import de.freese.jsensors.sensor.AbstractSensor;
import de.freese.jsensors.utils.Utils;

/**
 * Sensor für die Speicher Auslastung.
 *
 * @author Thomas Freese
 */
public class MemorySensor extends AbstractSensor
{
    /**
     *
     */
    private final JavaSysMon monitor;

    /**
     * Erzeugt eine neue Instanz von {@link MemorySensor}.
     */
    public MemorySensor()
    {
        super();

        this.monitor = new JavaSysMon();
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#getNames()
     */
    @Override
    public List<String> getNames()
    {
        return List.of("memory.free", "memory.usage");
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#measureImpl()
     */
    @Override
    protected void measureImpl() throws Exception
    {
        MemoryStats stats = this.monitor.physical();
        double free = stats.getFreeBytes();
        double total = stats.getTotalBytes();
        double usage = (1.0D - (free / total)) * 100.0D;

        long timeStamp = System.currentTimeMillis();

        store("memory.free", Utils.format(free), timeStamp);
        store("memory.usage", Utils.format(usage), timeStamp);
    }
}
