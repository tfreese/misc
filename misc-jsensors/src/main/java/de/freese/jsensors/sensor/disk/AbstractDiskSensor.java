// Created: 31.05.2017
package de.freese.jsensors.sensor.disk;

import java.io.File;
import java.util.Objects;
import javax.swing.filechooser.FileSystemView;
import de.freese.jsensors.sensor.AbstractSensor;
import de.freese.jsensors.utils.LifeCycle;

/**
 * Basis-implementierung eines Festplatten-Sensors.
 *
 * @author Thomas Freese
 */
public abstract class AbstractDiskSensor extends AbstractSensor implements LifeCycle
{
    /**
     *
     */
    private String disk;

    /**
    *
    */
    private File file;

    /**
     *
     */
    private FileSystemView fileSystemView;

    /**
     * Erstellt ein neues {@link AbstractDiskSensor} Object.
     *
     * @param name String
     */
    public AbstractDiskSensor(final String name)
    {
        super(name);
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
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        if ((this.disk == null) || this.disk.isEmpty())
        {
            throw new IllegalStateException("disk required");
        }

        this.fileSystemView = FileSystemView.getFileSystemView();

        this.file = new File(this.disk);
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        // Empty
    }
}
