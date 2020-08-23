/**
 *
 */
package de.freese.sonstiges.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
class TestConfiguration
{
    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testCommonsConfigurationFactoryBean() throws Exception
    {
        AbstractConfiguration manualConfiguration = new BaseConfiguration();
        manualConfiguration.addProperty("foo1", "bar1");

        Properties properties = new Properties();
        properties.put("foo2", "bar2");

        CommonsConfigurationFactoryBean factoryBean = new CommonsConfigurationFactoryBean();
        // factoryBean.setConfigurations(new PropertiesConfiguration("client.properties"));
        factoryBean.setConfigurations(new EnvironmentConfiguration(), new SystemConfiguration(), manualConfiguration);
        factoryBean.setThrowExceptionOnMissing(false);
        factoryBean.setProperties(properties);

        factoryBean.afterPropertiesSet();

        AbstractConfiguration config = factoryBean.getConfiguration();

        assertEquals("bar1", config.getString("foo1"));
        assertEquals("bar2", config.getString("foo2"));

        properties = factoryBean.getObject();
        assertEquals("bar1", properties.get("foo1"));
        assertEquals("bar2", properties.get("foo2"));
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testCompositeConfiguration() throws Exception
    {
        CompositeConfiguration config = new CompositeConfiguration();
        config.setThrowExceptionOnMissing(false);

        config.addConfiguration(new EnvironmentConfiguration());
        config.addConfiguration(new SystemConfiguration());
        // config.addConfiguration(new PropertiesConfiguration("client.properties"));

        AbstractConfiguration manualConfiguration = new BaseConfiguration();
        manualConfiguration.addProperty("foo1", "bar1");
        config.addConfiguration(manualConfiguration);

        config.addProperty("foo2", "bar2");

        assertEquals("bar1", config.getString("foo1"));
        assertEquals("bar2", config.getString("foo2"));

        // for (Iterator<String> iterator = config.getKeys(); iterator.hasNext();)
        // {
        // String key = iterator.next();
        //
        // System.out.printf("Key = %s,\t\t Value = %s\n", key, config.getString(key));
        // }
        // System.out.println();
        Properties properties = ConfigurationConverter.getProperties(config);
        assertEquals("bar1", properties.get("foo1"));
        assertEquals("bar2", properties.get("foo2"));

        // properties.list(System.out);
    }
}
