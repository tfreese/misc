/**
 * Created: 26.04.2019
 */

package de.freese.jsensors.backend.async;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.backend.Backend;

/**
 * Asynchrone-implementierung eines {@link Backend}s.
 *
 * @author Thomas Freese
 */
public class ExecutorBackend extends AbstractBackend
{
    /**
     *
     */
    private Backend delegate = null;

    /**
     *
     */
    private ExecutorService executorService = null;

    /**
     * Erstellt ein neues {@link ExecutorBackend} Object.
     */
    public ExecutorBackend()
    {
        super();
    }

    /**
     * @return {@link Backend}
     */
    public Backend getDelegate()
    {
        return this.delegate;
    }

    /**
     * @return {@link ExecutorService}
     */
    public ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#saveValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void saveValue(final SensorValue sensorValue)
    {
        getExecutorService().execute(() -> {
            getLogger().debug("{}", sensorValue);

            if ((sensorValue.getValue() == null) || sensorValue.getValue().isEmpty())
            {
                return;
            }

            getDelegate().save(sensorValue);
        });
    }

    /**
     * @param delegate {@link Backend}
     */
    public void setDelegate(final Backend delegate)
    {
        this.delegate = Objects.requireNonNull(delegate, "delegate required");
    }

    /**
     * @param executorService {@link ExecutorService}
     */
    public void setExecutorService(final ExecutorService executorService)
    {
        this.executorService = Objects.requireNonNull(executorService, "executorService required");
    }

    /**
     * @see de.freese.jsensors.LifeCycle#start()
     */
    @Override
    public void start()
    {
        super.start();

        if (getDelegate() == null)
        {
            throw new NullPointerException("delegate Backend required");
        }

        if (getExecutorService() == null)
        {
            throw new NullPointerException("executorService required");
        }

        getDelegate().start();
    }

    /**
     * @see de.freese.jsensors.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        super.stop();

        getDelegate().stop();
    }
}
