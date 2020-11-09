// Created: 09.11.2020
package de.freese.jsensors.backend.batch;

import java.util.ArrayList;
import java.util.List;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.utils.LifeCycle;

/**
 * Basis-implementierung eines Backends mit Batch-Funktionalität.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBatchBackend extends AbstractBackend implements LifeCycle
{
    /**
    *
    */
    private int batchSize = 5;

    /**
    *
    */
    private List<SensorValue> buffer;

    /**
     * Erstellt ein neues {@link AbstractBatchBackend} Object.
     */
    public AbstractBatchBackend()
    {
        super();
    }

    /**
     * @return {@link List}
     */
    protected synchronized List<SensorValue> flush()
    {
        List<SensorValue> list = this.buffer;
        this.buffer = null;

        return list;
    }

    /**
     * @return int
     */
    protected int getBatchSize()
    {
        return this.batchSize;
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#saveValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void saveValue(final SensorValue sensorValue) throws Exception
    {
        if (sensorValue == null)
        {
            return;
        }

        if (this.buffer == null)
        {
            this.buffer = new ArrayList<>();
        }

        this.buffer.add(sensorValue);

        if (this.buffer.size() >= getBatchSize())
        {
            saveValues(flush());
        }
    }

    /**
     * @param values {@link List}
     * @throws Exception Falls was schief geht.
     */
    protected abstract void saveValues(final List<SensorValue> values) throws Exception;

    /**
     * @param batchSize int
     */
    public void setBatchSize(final int batchSize)
    {
        this.batchSize = batchSize;
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        if (getBatchSize() < 1)
        {
            throw new IllegalArgumentException("batchSize must be >= 1");
        }
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        try
        {
            saveValues(flush());
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }
}
