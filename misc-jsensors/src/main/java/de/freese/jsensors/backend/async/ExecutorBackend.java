/**
 * Created: 26.04.2019
 */

package de.freese.jsensors.backend.async;

import java.util.Objects;
import java.util.concurrent.Executor;
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
    private final Backend delegate;

    /**
     *
     */
    private final Executor executor;

    /**
     * Erstellt ein neues {@link ExecutorBackend} Object.
     *
     * @param delegate {@link Backend}
     * @param executor {@link Executor}
     */
    public ExecutorBackend(final Backend delegate, final Executor executor)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");
        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#storeValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void storeValue(final SensorValue sensorValue) throws Exception
    {
        this.executor.execute(() -> {
            if ((sensorValue.getValue() == null) || sensorValue.getValue().isEmpty())
            {
                return;
            }

            // final Thread currentThread = Thread.currentThread();
            // String oldName = currentThread.getName();
            // currentThread.setName("task-" + sensorValue.getName());

            try
            {
                this.delegate.store(sensorValue);
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);
            }
            finally
            {
                // currentThread.setName(oldName);
            }
        });
    }
}
