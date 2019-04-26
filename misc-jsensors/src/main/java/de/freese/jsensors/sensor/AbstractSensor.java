// Created: 31.05.2017
package de.freese.jsensors.sensor;

import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.SensorValue;

/**
 * Basis-implementierung eines Sensors.
 *
 * @author Thomas Freese
 */
public abstract class AbstractSensor implements Sensor, InitializingBean, BeanNameAware
{
    /**
     *
     */
    private List<Backend> backends = null;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private String name = null;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractSensor}.
     */
    public AbstractSensor()
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
     * @return {@link List}
     */
    protected List<Backend> getBackends()
    {
        return this.backends;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liefert den Namen des Sensors.
     *
     * @return String
     */
    protected String getName()
    {
        return this.name;
    }

    /**
     * Initialisierung des Sensors.
     *
     * @throws Exception Falls was schief geht.
     */
    protected void initialize() throws Exception
    {
        // Empty
    }

    /**
     * Speichert den Sensorwert in den Backends.
     *
     * @param value String
     */
    protected void save(final String value)
    {
        save(value, System.currentTimeMillis(), getName());
    }

    /**
     * Speichert den Sensorwert in den Backends.
     *
     * @param value String
     * @param timestamp long
     * @param sensor String
     */
    protected void save(final String value, final long timestamp, final String sensor)
    {
        final SensorValue sensorValue = new SensorValue(sensor, value, timestamp);

        for (Backend backend : getBackends())
        {
            backend.save(sensorValue);
        }
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#scan()
     */
    @Override
    public final void scan()
    {
        try
        {
            scanValue();
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * Messen des Wertes.
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract void scanValue() throws Exception;

    /**
     * @see de.freese.jsensors.sensor.Sensor#setBackends(java.util.List)
     */
    @Override
    public void setBackends(final List<Backend> backends)
    {
        this.backends = Objects.requireNonNull(backends, "backends required");
    }

    /**
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    @Override
    public final void setBeanName(final String name)
    {
        // Defaultname = Beanname
        setName(name);
    }

    /**
     * Setzt den Namen des Sensors.
     *
     * @param name String; optional; Default = BeanID
     */
    @Override
    public void setName(final String name)
    {
        Objects.requireNonNull(name, "name required");

        this.name = name;
    }
}
