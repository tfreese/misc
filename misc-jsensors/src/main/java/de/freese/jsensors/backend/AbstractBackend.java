// Created: 31.05.2017
package de.freese.jsensors.backend;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Basis-implementierung eines Backends.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBackend implements Backend, InitializingBean
{
    /**
     * @author Thomas Freese
     */
    private class QueueWorker implements Runnable
    {
        /**
         *
         */
        private final String name;

        /**
         * Erzeugt eine neue Instanz von {@link QueueWorker}.
         *
         * @param name String
         */
        private QueueWorker(final String name)
        {
            super();

            this.name = name;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            Thread.currentThread().setName(this.name);

            while (true)
            {
                try
                {
                    SensorValue sensorValue = getQueue().take();

                    getLogger().debug("{}", sensorValue);

                    save(sensorValue);
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);
                }
            }
        }
    }

    /**
    *
    */
    private Executor executor = null;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private final BlockingQueue<SensorValue> queue = new LinkedBlockingQueue<>();

    /**
     * Wert für feste Anzahl von WorkerThreads.<br>
     * Default: 0, dient nur zum Testen
     */
    private final int workerThreads = 0;

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
     * @see de.freese.jsensors.backend.Backend#save(java.lang.String, long, java.lang.String)
     */
    @Override
    public void save(final String value, final long timestamp, final String sensor)
    {
        final SensorValue sensorValue = new SensorValue(value, timestamp, sensor);

        if (this.workerThreads > 0)
        {
            getQueue().add(sensorValue);
        }
        else
        {
            getExecutor().execute(() ->
            {
                getLogger().debug("{}", sensorValue);

                if ((value == null) || value.isEmpty())
                {
                    return;
                }

                save(sensorValue);
            });
        }
    }

    /**
     * Setzt den {@link Executor} für das Backend.<br>
     *
     * @param executor {@link Executor}
     */
    public void setExecutor(final Executor executor)
    {
        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    /**
     * @return {@link BlockingQueue}
     */
    private BlockingQueue<SensorValue> getQueue()
    {
        return this.queue;
    }

    /**
     * Liefert den {@link Executor}.
     *
     * @return {@link Executor}
     */
    protected Executor getExecutor()
    {
        return this.executor;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Initialisierung des Backends.
     *
     * @throws Exception Falls was schief geht.
     */
    protected void initialize() throws Exception
    {
        Objects.requireNonNull(getExecutor(), "executor required");

        if (this.workerThreads > 0)
        {
            for (int i = 1; i <= this.workerThreads; i++)
            {
                Runnable worker = new QueueWorker(getClass().getSimpleName() + "-" + i);
                getExecutor().execute(worker);
            }
        }
    }

    /**
     * Speichert den Sensorwert.
     *
     * @param sensorValue {@link SensorValue}
     */
    protected abstract void save(SensorValue sensorValue);
}
