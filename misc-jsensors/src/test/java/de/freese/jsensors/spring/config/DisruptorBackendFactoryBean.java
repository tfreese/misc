// Created: 31.10.2020
package de.freese.jsensors.spring.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.disruptor.DisruptorBackend;

/**
 * @author Thomas Freese
 */
public class DisruptorBackendFactoryBean implements FactoryBean<DisruptorBackend>, InitializingBean, DisposableBean
{
    /**
     *
     */
    private DisruptorBackend disruptorBackend;

    /**
    *
    */
    private final Map<String, List<Backend>> registry = new HashMap<>();

    /**
     *
     */
    private int ringBufferSize = 128;

    /**
     * Erstellt ein neues {@link DisruptorBackendFactoryBean} Object.
     */
    public DisruptorBackendFactoryBean()
    {
        super();
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.isTrue(this.ringBufferSize >= 1, "ringBufferSize must be >= 1");

        DisruptorBackend backend = new DisruptorBackend();
        backend.setRingBufferSize(this.ringBufferSize);

        this.registry.forEach((sensorName, backends) -> backends.forEach(b -> backend.register(sensorName, b)));

        backend.start();

        this.disruptorBackend = backend;
    }

    /**
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception
    {
        this.disruptorBackend.stop();
    }

    /**
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @Override
    public DisruptorBackend getObject() throws Exception
    {
        return this.disruptorBackend;
    }

    /**
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<?> getObjectType()
    {
        return DisruptorBackend.class;
    }

    /**
     * @param map {@link Map}
     */
    public void setRegistry(final Map<String, List<Backend>> map)
    {
        this.registry.putAll(map);
    }

    /**
     * @param ringBufferSize int
     */
    public void setRingBufferSize(final int ringBufferSize)
    {
        this.ringBufferSize = ringBufferSize;
    }
}
