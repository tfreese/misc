// Created: 31.10.2020
package de.freese.jsensors.spring.config;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import de.freese.jsensors.SensorBackendRegistry;
import de.freese.jsensors.backend.Backend;

/**
 * @author Thomas Freese
 */
public class SensorBackendRegistryFactoryBean implements FactoryBean<SensorBackendRegistry>, InitializingBean// , DisposableBean
{
    /**
     *
     */
    private Map<String, List<Backend>> registry;

    /**
     *
     */
    private SensorBackendRegistry sensorBackendRegistry;

    /**
     * Erstellt ein neues {@link SensorBackendRegistryFactoryBean} Object.
     */
    public SensorBackendRegistryFactoryBean()
    {
        super();
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(this.registry, "registry required");

        this.sensorBackendRegistry = new SensorBackendRegistry();

        this.registry.forEach((sensorName, backends) -> backends.forEach(backend -> this.sensorBackendRegistry.register(sensorName, backend)));
    }

    // /**
    // * @see org.springframework.beans.factory.DisposableBean#destroy()
    // */
    // @Override
    // public void destroy() throws Exception
    // {
    // this.sensorBackendRegistry.stop();
    // }

    /**
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @Override
    public SensorBackendRegistry getObject() throws Exception
    {
        return this.sensorBackendRegistry;
    }

    /**
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<?> getObjectType()
    {
        return SensorBackendRegistry.class;
    }

    /**
     * @param map {@link Map}
     */
    public void setRegistry(final Map<String, List<Backend>> map)
    {
        this.registry = map;
    }
}
