/**
 * Created on Feb 13, 2006 $Id: CommonsConfigurationFactoryBean.java,v 1.3 2006/12/05 16:20:14 costin Exp $ $Revision: 1.3 $
 */
package de.freese.sonstiges.configuration;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * FactoryBean which wraps a Commons CompositeConfiguration object for usage with PropertiesLoaderSupport. This allows the configuration object to behave like a
 * normal java.util.Properties object which can be passed on to setProperties() method allowing PropertyOverrideConfigurer and PropertyPlaceholderConfigurer to
 * take advantage of Commons Configuration.
 * <p/>
 * Internally a CompositeConfiguration object is used for merging multiple Configuration objects.
 *
 * @see java.util.Properties
 * @see org.springframework.core.io.support.PropertiesLoaderSupport
 * @author Costin Leau
 * @author Thomas Freese
 */
public class CommonsConfigurationFactoryBean implements InitializingBean, FactoryBean<Properties>
{
    /**
     *
     */
    private CompositeConfiguration configuration = null;

    /**
     *
     */
    private Configuration[] configurations = null;

    /**
     *
     */
    private Resource[] locations = null;

    /**
     *
     */
    private Properties properties = null;

    /**
     *
     */
    private boolean throwExceptionOnMissing = true;

    /**
     * Erstellt ein neues {@link CommonsConfigurationFactoryBean} Objekt.
     */
    public CommonsConfigurationFactoryBean()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link CommonsConfigurationFactoryBean} Objekt.
     *
     * @param configuration {@link Configuration}
     */
    public CommonsConfigurationFactoryBean(final Configuration configuration)
    {
        super();

        Objects.requireNonNull(configuration, "configuration required");

        this.configuration = new CompositeConfiguration(configuration);
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        if ((this.configuration == null) && ArrayUtils.isEmpty(this.configurations) && ArrayUtils.isEmpty(this.locations))
        {
            throw new IllegalArgumentException("no configuration object or location specified");
        }

        if (this.configuration == null)
        {
            this.configuration = new CompositeConfiguration();
        }

        this.configuration.setThrowExceptionOnMissing(this.throwExceptionOnMissing);

        if (this.configurations != null)
        {
            for (Configuration config : this.configurations)
            {
                this.configuration.addConfiguration(config);
            }
        }

        if (this.locations != null)
        {
            for (Resource resource : this.locations)
            {
                PropertiesConfiguration config = new PropertiesConfiguration();

                try (Reader reader = new InputStreamReader(resource.getInputStream()))
                {
                    config.read(reader);
                }

                this.configuration.addConfiguration(config);
            }
        }

        if (this.properties != null)
        {
            for (Entry<Object, Object> entry : this.properties.entrySet())
            {
                String key = entry.getKey().toString();
                Object value = entry.getValue();

                this.configuration.addProperty(key, value);
            }
        }
    }

    /**
     * Getter for the underlying CompositeConfiguration object.
     *
     * @return {@link CompositeConfiguration}
     */
    public CompositeConfiguration getConfiguration()
    {
        return this.configuration;
    }

    /**
     * Getter for the commons configurations objects which will be used as properties.
     *
     * @return Returns the configurations.
     */
    public Configuration[] getConfigurations()
    {
        return this.configurations;
    }

    /**
     * @return {@link Resource}[]
     */
    public Resource[] getLocations()
    {
        return this.locations;
    }

    /**
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @Override
    public Properties getObject() throws Exception
    {
        return (this.configuration != null) ? ConfigurationConverter.getProperties(this.configuration) : null;
    }

    /**
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @Override
    public Class<?> getObjectType()
    {
        return Properties.class;
    }

    /**
     * @return {@link Properties}
     */
    public Properties getProperties()
    {
        return this.properties;
    }

    /**
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    @Override
    public boolean isSingleton()
    {
        return true;
    }

    /**
     * Getter for the underlying Commons CompositeConfiguration throwExceptionOnMissing flag.
     *
     * @return boolean
     */
    public boolean isThrowExceptionOnMissing()
    {
        return this.throwExceptionOnMissing;
    }

    /**
     * Set the commons configurations objects which will be used as properties.
     *
     * @param configurations {@link Configuration}[]
     */
    public void setConfigurations(final Configuration...configurations)
    {
        this.configurations = configurations;
    }

    /**
     * Shortcut for loading configuration from Spring resources. It will internally create a PropertiesConfiguration object based on the URL retrieved from the
     * given Resources.
     *
     * @param locations {@link Resource}[]
     */
    public void setLocations(final Resource...locations)
    {
        this.locations = locations;
    }

    /**
     * @param properties {@link Properties}
     */
    public void setProperties(final Properties properties)
    {
        this.properties = properties;
    }

    /**
     * Set the underlying Commons CompositeConfiguration throwExceptionOnMissing flag.
     *
     * @param throwExceptionOnMissing boolean
     */
    public void setThrowExceptionOnMissing(final boolean throwExceptionOnMissing)
    {
        this.throwExceptionOnMissing = throwExceptionOnMissing;
    }
}
