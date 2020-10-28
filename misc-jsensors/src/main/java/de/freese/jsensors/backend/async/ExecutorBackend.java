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
    private Backend delegate;

    /**
     *
     */
    private ExecutorService executorService;

    /**
     * @see de.freese.jsensors.lifecycle.AbstractLifeCycle#onStart()
     */
    @Override
    protected void onStart() throws Exception
    {
        if (getDelegate() == null)
        {
            throw new NullPointerException("delegate backend required");
        }

        if (getExecutorService() == null)
        {
            throw new NullPointerException("executorService required");
        }

        getDelegate().start();
    }

    /**
     * @see de.freese.jsensors.lifecycle.AbstractLifeCycle#onStop()
     */
    @Override
    protected void onStop() throws Exception
    {
        getDelegate().stop();
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
            if ((sensorValue.getValue() == null) || sensorValue.getValue().isEmpty())
            {
                return;
            }

            final Thread currentThread = Thread.currentThread();
            String oldName = currentThread.getName();
            currentThread.setName("task-" + sensorValue.getName());

            try
            {
                getDelegate().save(sensorValue);
            }
            finally
            {
                currentThread.setName(oldName);
            }
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
}
