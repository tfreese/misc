/**
 * Created: 11.12.2011
 */

package de.freese.spring.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Thomas Freese
 */
@Configuration
public class SpringConfigDB
{
	// /**
	// *
	// */
	// @Resource
	// private Environment environment = null;
	/**
	 * 
	 */
	@Value("${jdbc.url}")
	private String dbURL = null;

	/**
	 * 
	 */
	@Value("${jdbc.username}")
	private String dbUser = null;

	/**
	 * 
	 */
	@Value("${jdbc.password}")
	// @Value("#{propertiesDB['jdbc.password']}")
	private String dbPassword = null;

	/**
	 * Erstellt ein neues {@link SpringConfigDB} Object.
	 */
	public SpringConfigDB()
	{
		super();
	}

	/**
	 * @return {@link DataSource}
	 */
	@Bean(destroyMethod = "close")
	public DataSource dataSource()
	{
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		// dataSource.setUrl(this.environment.getProperty("jdbc.url"));
		// dataSource.setUsername(this.environment.getProperty("jdbc.username"));
		// dataSource.setPassword(this.environment.getProperty("jdbc.password"));
		dataSource.setMaxActive(1);
		dataSource.setMaxIdle(1);
		dataSource.setMinIdle(1);
		dataSource.setUrl(this.dbURL);
		dataSource.setUsername(this.dbUser);
		dataSource.setPassword(this.dbPassword);
		dataSource.setValidationQuery("values(1)");

		return dataSource;
	}

	/**
	 * @return {@link PlatformTransactionManager}
	 */
	@Bean
	public PlatformTransactionManager transactionManager()
	{
		return new DataSourceTransactionManager(dataSource());
	}
}
