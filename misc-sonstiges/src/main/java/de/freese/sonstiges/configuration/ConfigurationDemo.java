/**
 * Created: 22.08.2012
 */
package de.freese.sonstiges.configuration;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;

/**
 * @author Thomas Freese
 */
public class ConfigurationDemo
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        CompositeConfiguration configuration = new CompositeConfiguration();
        configuration.addConfiguration(new EnvironmentConfiguration());
        configuration.addConfiguration(new SystemConfiguration());

        // try (Reader reader = new FileReader("src/main/resources/simplelogger.properties"))
        try (Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/simplelogger.properties")))
        {
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.read(reader);

            configuration.addConfiguration(config);
        }

        Configuration manualConfig = new BaseConfiguration();
        manualConfig.addProperty("foo1", "bar1");
        configuration.addConfiguration(manualConfig);

        configuration.addProperty("foo2", "bar2");

        for (Iterator<String> iterator = configuration.getKeys(); iterator.hasNext();)
        {
            String key = iterator.next();
            String value = configuration.getString(key);

            System.out.printf("Key = %s,\t\tValue = %s%nn", key, value);
        }

        System.out.println();

        System.out.printf("Key = %s,\t\tValue = %s%nn", "org.slf4j.simpleLogger.showDateTime",
                configuration.getBoolean("org.slf4j.simpleLogger.showDateTime", null));

        System.out.println();

        // Properties properties = ConfigurationConverter.getProperties(configuration);
        // properties.list(System.out);
    }

    /**
     * Erstellt ein neues {@link ConfigurationDemo} Object.
     */
    public ConfigurationDemo()
    {
        super();
    }
}
