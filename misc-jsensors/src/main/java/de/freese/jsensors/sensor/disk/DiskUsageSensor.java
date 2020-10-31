// Created: 31.05.2017
package de.freese.jsensors.sensor.disk;

import java.io.File;
import de.freese.jsensors.utils.Utils;

/**
 * Liefert die prozentuale Auslastung des Speicherplatz eines Laufwerks oder Partition.
 *
 * @author Thomas Freese
 */
public class DiskUsageSensor extends AbstractDiskSensor
{
    /**
     * Erstellt ein neues {@link DiskUsageSensor} Object.
     *
     * @param name String
     */
    public DiskUsageSensor(final String name)
    {
        super(name);
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#scanValue()
     */
    @Override
    protected void scanValue() throws Exception
    {
        File file = getFile();

        double free = file.getFreeSpace();
        double total = file.getTotalSpace();
        double usage = (1.0D - (free / total)) * 100.0D;

        String value = Utils.format(usage);

        save(value);
    }
}
