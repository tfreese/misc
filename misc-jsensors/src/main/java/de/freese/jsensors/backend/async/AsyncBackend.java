/**
 * Created: 26.04.2019
 */

package de.freese.jsensors.backend.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.backend.Backend;

/**
 * Asynchrone-implementierung eines {@link Backend}s.
 *
 * @author Thomas Freese
 */
public class AsyncBackend extends AbstractBackend
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
            this.isShutdown = true;
            interrupt();
        }
    }

    /**
     *
     */
    private Backend delegate = null;

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
     * @see de.freese.jsensors.backend.AbstractBackend#saveImpl(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void saveImpl(final SensorValue sensorValue)
    {
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
        super.start();

        if (getDelegate() == null)
        {
            throw new NullPointerException("delegate Backend required");
        }

        getDelegate().start();

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
        super.stop();

        this.workers.forEach(w -> w.shutdown());

        // Save last SensorValues.
        if (!getQueue().isEmpty())
        {
            getLogger().info("save last sensorvalues");

            SensorValue sv = null;

            while ((sv = getQueue().poll()) != null)
            {
                getDelegate().save(sv);
            }
        }

        this.workers.clear();

        getDelegate().stop();
    }
}
