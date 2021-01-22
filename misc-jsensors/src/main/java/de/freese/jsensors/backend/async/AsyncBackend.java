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
                    SensorValue sensorValue = getQueue().take();

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
    private Backend delegate;

    /**
     *
     */
    private int numberOfWorkers = 1;

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
     * @param numberOfWorkers int
     */
    public AsyncBackend(final Backend delegate, final int numberOfWorkers)
    {
        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        if (numberOfWorkers <= 0)
        {
            throw new IllegalArgumentException("numberOfWorkers must be > 0: " + numberOfWorkers);
        }

        this.numberOfWorkers = numberOfWorkers;
    }

    /**
     * @return {@link BlockingQueue}
     */
    private BlockingQueue<SensorValue> getQueue()
    {
        return this.queue;
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        if (this.delegate instanceof LifeCycle)
        {
            ((LifeCycle) this.delegate).start();
        }

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
        if (!getQueue().isEmpty())
        {
            getLogger().info("save last sensorvalues");

            SensorValue sv = null;

            while ((sv = getQueue().poll()) != null)
            {
                this.delegate.store(sv);
            }
        }

        this.workers.clear();

        if (this.delegate instanceof LifeCycle)
        {
            ((LifeCycle) this.delegate).stop();
        }
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#storeValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void storeValue(final SensorValue sensorValue) throws Exception
    {
        getQueue().add(sensorValue);
    }
}
