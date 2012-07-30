package de.freese.persistence.jdbc.manager;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 07.03.2004
 * 
 * @author Thomas Freese
 */
public class PersistenceManagerFactory
{
	/**
	 *
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(PersistenceManagerFactory.class);

	/**
     * 
     */
	private static final Map<String, AbstractPersistenceManager<?>> _managerMap = new TreeMap<>();

	/**
	 * @return {@link Map}
	 */
	public static final Map<String, AbstractPersistenceManager<?>> getManagerMap()
	{
		return _managerMap;
	}

	/**
	 * @param <T> Konkreter Typ des persistenten Objektes
	 * @param pmClass Class
	 * @param connection {@link Connection}
	 * @return {@link AbstractPersistenceManager}
	 */
	public static final synchronized <T extends AbstractPersistenceManager<?>> AbstractPersistenceManager<?> getPMInstance(	final Class<T> pmClass,
																															final Connection connection)
	{
		if (getManagerMap().get(pmClass.toString() + connection.toString()) == null)
		{
			AbstractPersistenceManager<?> pm = null;

			try
			{
				Constructor<T> constructor = pmClass.getConstructor(new Class[]
				{
					Connection.class
				});

				pm = constructor.newInstance(new Object[]
				{
					connection
				});

				getManagerMap().put(pmClass.toString() + connection.toString(), pm);

				return pm;
			}
			catch (Throwable th)
			{
				LOGGER.error("", th);

				return null;
			}
		}

		return getManagerMap().get(pmClass.toString() + connection.toString());
	}
}
