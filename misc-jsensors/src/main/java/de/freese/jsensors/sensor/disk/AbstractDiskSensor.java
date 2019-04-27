// Created: 31.05.2017
package de.freese.jsensors.sensor.disk;

import java.io.File;
import java.util.Objects;
import javax.swing.filechooser.FileSystemView;
import de.freese.jsensors.sensor.AbstractSensor;

/**
 * Basis-implementierung eines Festplatten-Sensors.
 *
 * @author Thomas Freese
 */
public abstract class AbstractDiskSensor extends AbstractSensor
{
    /**
     *
     */
    private String disk = null;

    /**
    *
    */
    private File file = null;

    /**
     *
     */
    private FileSystemView fileSystemView = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractDiskSensor}.
     */
    public AbstractDiskSensor()
    {
        super();
    }

    /**
     * @return {@link File}
     */
    protected File getFile()
    {
        return this.file;
    }

    /**
     * @return {@link FileSystemView}
     */
    protected FileSystemView getFileSystemView()
    {
        return this.fileSystemView;
    }

    /**
     * @param disk String
     */
    public void setDisk(final String disk)
    {
        this.disk = Objects.requireNonNull(disk, "disk required");
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#start()
     */
    @Override
    public void start()
    {
        super.start();

        if ((this.disk == null) || this.disk.isEmpty())
        {
            throw new IllegalStateException("disk required");
        }

        this.fileSystemView = FileSystemView.getFileSystemView();

        this.file = new File(this.disk);
    }
}
