/**
 * Created: 26.04.2019
 */

package de.freese.jsensors.backend.async;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.utils.LifeCycle;

/**
 * Asynchrone-implementierung eines {@link Backend}s.
 *
 * @author Thomas Freese
 */
public class AsyncBackend extends AbstractBackend implements LifeCycle
{
    /**
     * @author Thomas Freese
     */
    private class QueueWorker extends Thread
    {
        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            while (!Thread.interrupted())
            // while (!isStopped())
            {
                try
                {
                    SensorValue sensorValue = AsyncBackend.this.queue.take();

                    if ((sensorValue.getValue() == null) || sensorValue.getValue().isEmpty())
                    {
                        continue;
                    }

                    AsyncBackend.this.delegate.store(sensorValue);
                }
                catch (InterruptedException iex)
                {
                    break;
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);
                }
            }

            getLogger().debug("terminated");
        }
    }

    /**
     *
     */
    private final Backend delegate;

    /**
     *
     */
    private final int numberOfWorkers;

    /**
    *
    */
    private final BlockingQueue<SensorValue> queue = new LinkedBlockingQueue<>();

    /**
     *
     */
    private final List<QueueWorker> workers = new ArrayList<>();

    /**
     * Erstellt ein neues {@link AsyncBackend} Object.
     *
     * @param delegate {@link Backend}
     */
    public AsyncBackend(final Backend delegate)
    {
        this(delegate, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Erstellt ein neues {@link AsyncBackend} Object.
     *
     * @param delegate {@link Backend}
     * @param numberOfWorkers int
     */
    public AsyncBackend(final Backend delegate, final int numberOfWorkers)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        if (numberOfWorkers < 1)
        {
            throw new IllegalArgumentException("numberOfWorkers must be >= 1: " + numberOfWorkers);
        }

        this.numberOfWorkers = numberOfWorkers;
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        for (int i = 1; i <= this.numberOfWorkers; i++)
        {
            QueueWorker worker = new QueueWorker();
            worker.setName(this.delegate.getClass().getSimpleName().replace("Backend", "Worker") + "-" + i);
            worker.setDaemon(true);

            this.workers.add(worker);
            worker.start();
        }
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        this.workers.forEach(QueueWorker::interrupt);

        // Save last SensorValues.
        if (!this.queue.isEmpty())
        {
            getLogger().info("save last sensorvalues");

            SensorValue sv = null;

            while ((sv = this.queue.poll()) != null)
            {
                this.delegate.store(sv);
            }
        }

        this.workers.clear();
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#storeValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void storeValue(final SensorValue sensorValue) throws Exception
    {
        this.queue.add(sensorValue);
    }
}
