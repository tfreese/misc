// Created: 31.05.2017
package de.freese.jsensors.sensor.disk;

import java.io.File;

/**
 * Liefert den freien Speicherplatz eines Laufwerks oder Partition.
 *
 * @author Thomas Freese
 */
public class DiskFreeSpace extends AbstractDiskSensor
{
    /**
     * Erzeugt eine neue Instanz von {@link DiskFreeSpace}.
     */
    public DiskFreeSpace()
    {
        super();
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#scanValue()
     */
    @Override
    protected void scanValue() throws Exception
    {
        // String driveName = getFileSystemView().getSystemDisplayName(f);
        // String driveType = getFileSystemView().getSystemTypeDescription(f);

        File file = getFile();
        long freeSpace = file.getFreeSpace();

        save(Long.toString(freeSpace));
    }
}