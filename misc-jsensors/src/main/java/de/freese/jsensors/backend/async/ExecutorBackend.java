/**
 * Created: 26.04.2019
 */

package de.freese.jsensors.backend.async;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.utils.LifeCycle;

/**
 * Asynchrone-implementierung eines {@link Backend}s.
 *
 * @author Thomas Freese
 */
public class ExecutorBackend extends AbstractBackend implements LifeCycle
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
     * @see de.freese.jsensors.backend.AbstractBackend#saveValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void saveValue(final SensorValue sensorValue) throws Exception
    {
        this.executorService.execute(() -> {
            if ((sensorValue.getValue() == null) || sensorValue.getValue().isEmpty())
            {
                return;
            }

            final Thread currentThread = Thread.currentThread();
            String oldName = currentThread.getName();
            currentThread.setName("task-" + sensorValue.getName());

            try
            {
                this.delegate.save(sensorValue);
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

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        if (this.delegate == null)
        {
            throw new NullPointerException("delegate backend required");
        }

        if (this.delegate == null)
        {
            throw new NullPointerException("executorService required");
        }

        if (this.delegate instanceof LifeCycle)
        {
            ((LifeCycle) this.delegate).start();
        }
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        if (this.delegate instanceof LifeCycle)
        {
            ((LifeCycle) this.delegate).stop();
        }
    }
}
