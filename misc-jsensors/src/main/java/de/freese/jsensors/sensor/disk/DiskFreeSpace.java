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
     * Erstellt ein neues {@link DiskFreeSpace} Object.
     *
     * @param name String
     */
    public DiskFreeSpace(final String name)
    {
        super(name);
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
