// Created: 01.06.2017
package de.freese.jsensors.sensor.swap;

import java.util.List;
import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;
import de.freese.jsensors.sensor.AbstractSensor;
import de.freese.jsensors.utils.Utils;

/**
 * Sensor für die Swap Auslastung.
 *
 * @author Thomas Freese
 */
public class SwapSensor extends AbstractSensor
{
    /**
     *
     */
    private final JavaSysMon monitor;

    /**
     * Erzeugt eine neue Instanz von {@link SwapSensor}.
     */
    public SwapSensor()
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
        return List.of("swap.free", "swap.usage");
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#measureImpl()
     */
    @Override
    protected void measureImpl() throws Exception
    {
        MemoryStats stats = this.monitor.swap();
        double free = stats.getFreeBytes();
        double total = stats.getTotalBytes();
        double usage = (1.0D - (free / total)) * 100.0D;

        long timeStamp = System.currentTimeMillis();

        store("swap.free", Utils.format(free), timeStamp);
        store("swap.usage", Utils.format(usage), timeStamp);
    }
}
