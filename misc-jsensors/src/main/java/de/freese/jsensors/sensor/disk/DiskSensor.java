// Created: 31.05.2017
package de.freese.jsensors.sensor.disk;

import java.io.File;
import java.util.List;
import java.util.Objects;
import de.freese.jsensors.sensor.AbstractSensor;
import de.freese.jsensors.utils.Utils;

/**
 * Basis-implementierung eines Festplatten-Sensors.
 *
 * @author Thomas Freese
 */
public class DiskSensor extends AbstractSensor
{
    /**
    *
    */
    private final File file;

    /**
     *
     */
    private final String nameForDisk;

    /**
     * Erstellt ein neues {@link DiskSensor} Object.
     *
     * @param file {@link File}
     * @param nameForDisk String
     */
    public DiskSensor(final File file, final String nameForDisk)
    {
        super();

        this.file = Objects.requireNonNull(file, "file required");

        String name = Objects.requireNonNull(nameForDisk, "nameForDisk required");
        name = name.replace("-", ".");
        name = name.replace(" ", ".");
        name = name.replace("/", ".");
        name = name.replace("\\", ".");
        this.nameForDisk = name;
    }

    /**
     * @return {@link File}
     */
    protected File getFile()
    {
        return this.file;
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#getNames()
     */
    @Override
    public List<String> getNames()
    {
        return List.of("disk.free." + this.nameForDisk, "disk.usage." + this.nameForDisk);
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#measureImpl()
     */
    @Override
    protected void measureImpl() throws Exception
    {
        double free = getFile().getFreeSpace();
        double total = getFile().getTotalSpace();
        double usage = (1.0D - (free / total)) * 100.0D;

        long timeStamp = System.currentTimeMillis();

        store("disk.free." + this.nameForDisk, Utils.format(free), timeStamp);
        store("disk.usage." + this.nameForDisk, Utils.format(usage), timeStamp);
    }
}
