/**
 * Created: 18.12.2011
 */

package de.freese.spring.config;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Thomas Freese
 */
@Configuration
@Profile("prod")
public class SpringConfigPropertiesProd
{
	/**
	 * Erstellt ein neues {@link SpringConfigPropertiesProd} Object.
	 */
	public SpringConfigPropertiesProd()
	{
		super();
	}

	/**
	 * @return {@link Properties}
	 */
	@Bean
	public Properties propertiesDB()
	{
		String db = "d:/javaDB/derby/prod";

		Properties bean = new Properties();
		bean.put("jdbc.dbName", db);
		bean.put("jdbc.url", "jdbc:derby:" + db + ";create=true");
		bean.put("jdbc.username", "tommy");
		bean.put("jdbc.password", "tommy");

		return bean;
	}

	/**
	 * @return {@link PropertyPlaceholderConfigurer}
	 * @throws Exception Falls was schief geht.
	 */
	@Bean
	public PropertyPlaceholderConfigurer propertyConfigurer() throws Exception
	{
		PropertyPlaceholderConfigurer bean = new PropertyPlaceholderConfigurer();
		bean.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK);
		bean.setSearchSystemEnvironment(true);
		bean.setProperties(propertiesDB());

		return bean;
	}
}
