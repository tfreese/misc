/**
 * Created: 26.04.2019
 */

package de.freese.jsensors.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.jsensors.LifeCycle;

/**
 * Asynchrone-implementierung eines {@link Backend}s.
 *
 * @author Thomas Freese
 */
public class AsyncBackend implements Backend, LifeCycle
{
    /**
     * @author Thomas Freese
     */
    private class QueueWorker extends Thread
    {
        /**
         *
         */
        private boolean isShutdown = false;

        /**
         * Erzeugt eine neue Instanz von {@link QueueWorker}.
         */
        private QueueWorker()
        {
            super();
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            while (!Thread.interrupted())
            {
                try
                {
                    SensorValue sensorValue = getQueue().take();

                    getLogger().debug("{}", sensorValue);

                    if ((sensorValue.getValue() == null) || sensorValue.getValue().isEmpty())
                    {
                        return;
                    }

                    getDelegate().save(sensorValue);
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);
                }

                if (this.isShutdown)
                {
                    break;
                }
            }

            getLogger().debug("terminated");
        }

        /**
         *
         */
        void shutdown()
        {
            interrupt();
            this.isShutdown = true;
        }
    }

    /**
     *
     */
    private Backend delegate = null;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

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
     */
    public AsyncBackend()
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
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liefert die Anzahl der Worker-Threads.
     *
     * @return int
     */
    public int getNumberOfWorkers()
    {
        return this.numberOfWorkers;
    }

    /**
     * @return {@link BlockingQueue}
     */
    private BlockingQueue<SensorValue> getQueue()
    {
        return this.queue;
    }

    /**
     * @see de.freese.jsensors.LifeCycle#isStarted()
     */
    @Override
    public boolean isStarted()
    {
        return !this.workers.isEmpty();
    }

    /**
     * @see de.freese.jsensors.backend.Backend#save(de.freese.jsensors.backend.SensorValue)
     */
    @Override
    public void save(final SensorValue sensorValue)
    {
        if (sensorValue == null)
        {
            return;
        }

        if (!isStarted())
        {
            getLogger().error("Backend stopped, SensorValue discarted");
        }

        getQueue().add(sensorValue);
    }

    /**
     * @param delegate {@link Backend}
     */
    public void setDelegate(final Backend delegate)
    {
        this.delegate = delegate;
    }

    /**
     * Setzt die Anzahl der Worker-Threads.
     *
     * @param numberOfWorkers int
     */
    public void setNumberOfWorkers(final int numberOfWorkers)
    {
        if (numberOfWorkers <= 0)
        {
            throw new IllegalArgumentException("numberOfWorkers <= 0: " + numberOfWorkers);
        }

        if (isStarted())
        {
            throw new IllegalStateException("AsyncBackend already started");
        }

        this.numberOfWorkers = numberOfWorkers;
    }

    /**
     * @see de.freese.jsensors.LifeCycle#start()
     */
    @Override
    public void start()
    {
        if (getDelegate() == null)
        {
            throw new NullPointerException("delegate Backend required");
        }

        for (int i = 1; i <= getNumberOfWorkers(); i++)
        {
            QueueWorker worker = new QueueWorker();
            worker.setName(getClass().getSimpleName().replace("Backend", "Worker") + "-" + i);
            worker.setDaemon(true);

            this.workers.add(worker);
            worker.start();
        }
    }

    /**
     * @see de.freese.jsensors.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        this.workers.forEach(w -> w.shutdown());
    }
}
