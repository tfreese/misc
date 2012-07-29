/**
 * 20.12.2006
 */
package de.freese.jpa.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * @author Thomas Freese
 */
public final class HibernateUtil
{
	/** 
     * 
     */
	private static final SessionFactory sessionFactory;

	static
	{
		try
		{
			URL url = ClassLoader.getSystemResource("hibernate.properties");
			InputStream inputStream = url.openStream();
			Properties properties = new Properties();

			properties.load(inputStream);

			Configuration configuration = new Configuration();

			configuration.setProperties(properties);
			configuration.configure();

			ServiceRegistry serviceRegistry =
					new ServiceRegistryBuilder().applySettings(properties).buildServiceRegistry();

			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		}
		catch (Throwable th)
		{
			System.err.println("SessionFactory creation failed: " + th);

			throw new ExceptionInInitializerError(th);
		}
	}

	/**
	 * !
	 * 
	 * @return {@link SessionFactory}
	 */
	public static SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}
}
