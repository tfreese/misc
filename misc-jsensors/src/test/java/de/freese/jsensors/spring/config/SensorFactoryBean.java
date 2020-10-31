// Created: 31.10.2020
package de.freese.jsensors.spring.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.LifeCycle;

/**
 * @author Thomas Freese
 */
public class SensorFactoryBean implements FactoryBean<Sensor>, BeanNameAware, InitializingBean, DisposableBean
{
    /**
     *
     */
    private Backend backend;

    /**
     *
     */
    private String beanName;

    /**
     *
     */
    private Map<String, Object> propertyMap;

    /**
     *
     */
    private Sensor sensor;

    /**
     *
     */
    private Class<Sensor> sensorClazz;

    /**
     *
     */
    private String sensorName;

    /**
     * Erstellt ein neues {@link SensorFactoryBean} Object.
     */
    public SensorFactoryBean()
    {
        super();
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(this.sensorClazz, "sensorClazz required");
        Assert.notNull(this.backend, "backend required");

        Constructor<Sensor> constructor = this.sensorClazz.getConstructor(String.class);
        Sensor s = constructor.newInstance((this.sensorName == null) || this.sensorName.isEmpty() ? this.beanName : this.sensorName);

        s.setBackend(this.backend);

        if (this.propertyMap != null)
        {
            for (String propertyName : this.propertyMap.keySet())
            {
                Object propertyValue = this.propertyMap.get(propertyName);

                this.sensorClazz.getSuperclass().getDeclaredFields();

                Field field = getField(this.sensorClazz, propertyName);
                field.setAccessible(true);

                field.set(s, propertyValue);
            }
        }

        this.sensor = s;

        if (this.sensor instanceof LifeCycle)
        {
            ((LifeCycle) this.sensor).start();
        }
    }

    /**
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception
    {
        if (this.sensor instanceof LifeCycle)
        {
            ((LifeCycle) this.sensor).stop();
        }
    }

    /**
     * @param clazz Class
     * @param propertyName String
     * @return {@link Field}
     */
    private Field getField(final Class<? super Sensor> clazz, final String propertyName)
    {
        try
        {
            return clazz.getDeclaredField(propertyName);
        }
        catch (Exception ex)
        {
            return getField(clazz.getSuperclass(), propertyName);
        }
    }

    // /**
    // * @param clazz Class
    // * @param propertyName String
    // * @return {@link Field}
    // */
    // private Method getMethod(final Class<? super Sensor> clazz, final String propertyName)
    // {
    // try
    // {
    // PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, clazz);
    //
    // return propertyDescriptor.getWriteMethod();
    //
    // // return clazz.getDeclaredMethod("set"+propertyName);
    // }
    // catch (Exception ex)
    // {
    // return getMethod(clazz.getSuperclass(), propertyName);
    // }
    // }

    /**
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @Override
    public Sensor getObject() throws Exception
    {
        return this.sensor;
    }

    /**
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<?> getObjectType()
    {
        return Sensor.class;
    }

    /**
     * @param backend {@link Backend}
     */
    public void setBackend(final Backend backend)
    {
        this.backend = backend;
    }

    /**
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    @Override
    public void setBeanName(final String name)
    {
        this.beanName = name;
    }

    /**
     * @param propertyMap {@link Map}<String,Object>
     */
    public void setPropertyMap(final Map<String, Object> propertyMap)
    {
        this.propertyMap = propertyMap;
    }

    /**
     * @param sensorClazz Class<Sensor>
     */
    public void setSensorClazz(final Class<Sensor> sensorClazz)
    {
        this.sensorClazz = sensorClazz;
    }

    /**
     * Default: BeanName
     *
     * @param sensorName String
     */
    public void setSensorName(final String sensorName)
    {
        this.sensorName = sensorName;
    }
}
