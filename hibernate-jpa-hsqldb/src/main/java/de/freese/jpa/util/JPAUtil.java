/**
 * 20.12.2006
 */
package de.freese.jpa.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Thomas Freese
 */
public final class JPAUtil
{
	/**
     * 
     */
	private static final EntityManagerFactory entityManagerFactory;

	static
	{
		try
		{
			URL url = ClassLoader.getSystemResource("hibernate.properties");
			InputStream inputStream = url.openStream();
			Properties configOverrides = new Properties();

			configOverrides.load(inputStream);

			entityManagerFactory =
					Persistence.createEntityManagerFactory("manager1", configOverrides);
		}
		catch (Throwable th)
		{
			System.err.println("SessionFactory creation failed: " + th);
			th.getCause().printStackTrace();

			throw new ExceptionInInitializerError(th);
		}
	}

	/**
	 * @return {@link EntityManagerFactory}
	 */
	public static EntityManagerFactory getEntityManagerFactory()
	{
		return entityManagerFactory;
	}
}
