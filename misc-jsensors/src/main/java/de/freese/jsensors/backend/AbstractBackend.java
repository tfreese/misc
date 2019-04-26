// Created: 31.05.2017
package de.freese.jsensors.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Basis-implementierung eines {@link Backend}s.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBackend implements Backend, InitializingBean
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

                    saveImpl(sensorValue);
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
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private final BlockingQueue<SensorValue> queue = new LinkedBlockingQueue<>();

    /**
     *
     */
    private final List<QueueWorker> workers = new ArrayList<>();

    /**
     * Wert für feste Anzahl von WorkerThreads.<br>
     */
    private final int workerThreads = 1;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractBackend}.
     */
    public AbstractBackend()
    {
        super();
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public final void afterPropertiesSet() throws Exception
    {
        initialize();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link BlockingQueue}
     */
    private BlockingQueue<SensorValue> getQueue()
    {
        return this.queue;
    }

    /**
     * Initialisierung des Backends.
     *
     * @throws Exception Falls was schief geht.
     */
    protected void initialize() throws Exception
    {
        if (this.workerThreads > 0)
        {
            for (int i = 1; i <= this.workerThreads; i++)
            {
                QueueWorker worker = new QueueWorker();
                worker.setName(getClass().getSimpleName().replace("Backend", "Worker") + "-" + i);
                worker.setDaemon(true);

                this.workers.add(worker);
                worker.start();
            }
        }
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

        getQueue().add(sensorValue);
    }

    /**
     * Speichert den Sensorwert.
     *
     * @param sensorValue {@link SensorValue}
     */
    protected abstract void saveImpl(SensorValue sensorValue);
}
