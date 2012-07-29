/**
 * Created: 11.12.2011
 */

package de.freese.spring.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.freese.spring.config.dao.IUserDAO;
import de.freese.spring.config.dao.UserDAO;
import de.freese.spring.config.service.IUserService;
import de.freese.spring.config.service.UserService;

/**
 * @author Thomas Freese
 */
@Configuration
@Import(
{
		SpringConfigPropertiesTest.class, SpringConfigPropertiesProd.class, SpringConfigDB.class
})
@EnableTransactionManagement
public class SpringConfigApplication
{
	/**
	 * 
	 */
	@Resource
	private SpringConfigDB configDB = null;

	// /**
	// *
	// */
	// @Resource(name = "propertiesDB")
	// private Properties propertiesDB = null;

	/**
	 * 
	 */
	@Value("#{propertiesDB['jdbc.dbName']}")
	private String dbName = null;

	/**
	 * Erstellt ein neues {@link SpringConfigApplication} Object.
	 */
	public SpringConfigApplication()
	{
		super();
	}

	/**
	 * @return {@link IUserDAO}
	 */
	@Bean(initMethod = "startup", destroyMethod = "shutdown")
	public IUserDAO userDAO()
	{
		return new UserDAO(this.configDB.dataSource(), this.dbName);
	}

	/**
	 * @return {@link IUserService}
	 */
	@Bean
	public IUserService userService()
	{
		UserService userService = new UserService();
		userService.setUserDAO(userDAO());

		return userService;
	}
}
