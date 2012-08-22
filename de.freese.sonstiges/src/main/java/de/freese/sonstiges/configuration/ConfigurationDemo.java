/**
 * Created: 22.08.2012
 */

package de.freese.sonstiges.configuration;

import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;

/**
 * @author Thomas Freese
 */
public class ConfigurationDemo
{
	/**
	 * @param args String[]
	 * @throws ConfigurationException Falls was schief geht.
	 */
	public static void main(final String[] args) throws ConfigurationException
	{
		CompositeConfiguration configuration = new CompositeConfiguration();
		configuration.addConfiguration(new EnvironmentConfiguration());
		configuration.addConfiguration(new SystemConfiguration());
		configuration.addConfiguration(new PropertiesConfiguration("bundles/statusbar.properties"));

		Configuration manualConfig = new BaseConfiguration();
		manualConfig.addProperty("foo1", "bar1");
		configuration.addConfiguration(manualConfig);

		configuration.addProperty("foo2", "bar2");

		for (Iterator<String> iterator = configuration.getKeys(); iterator.hasNext();)
		{
			String key = iterator.next();
			String value = configuration.getString(key);

			System.out.printf("Key = %s,\t\tValue = %s\n", key, value);
		}

		System.out.println();

		System.out.printf("Key = %s,\t\tValue = %s\n", "statusbar.animation.rate",
				configuration.getInteger("statusbar.animation.rate", null));

		System.out.println();

		Properties properties = ConfigurationConverter.getProperties(configuration);
		properties.list(System.out);
	}

	/**
	 * Erstellt ein neues {@link ConfigurationDemo} Object.
	 */
	public ConfigurationDemo()
	{
		super();
	}
}
