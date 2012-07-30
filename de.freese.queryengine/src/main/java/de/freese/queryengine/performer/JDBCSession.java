/**
 * 22.04.2007
 */
package de.freese.queryengine.performer;

import java.sql.Connection;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handelt die Connection in einer {@link ThreadLocal} und die Transactionsteuerung.
 * 
 * @author Thomas Freese
 */
public final class JDBCSession
{
	/**
     * 
     */
	public static final Logger LOGGER = LoggerFactory.getLogger(JDBCSession.class);

	/**
	 * 
	 */
	private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

	/**
	 * 
	 */
	private static DataSource dataSource = null;

	/**
	 * Oeffnen der Transaktion.
	 * 
	 * @throws Exception Falls was schief geht.
	 */
	public static void beginTransaction() throws Exception
	{
		Connection connection = getCurrentConnection();

		if (connection == null)
		{
			connection = dataSource.getConnection();

			connection.setAutoCommit(false);

			connectionHolder.set(connection);
		}

		if (connection.isClosed())
		{
			LOGGER.error("Connection ist bereits geschlossen !");

			return;
		}
	}

	/**
	 * Comitt der Transaktion.
	 * 
	 * @throws Exception Falls was schief geht.
	 * @throws IllegalStateException Falls was schief geht.
	 */
	public static void commitTransaction() throws Exception
	{
		Connection connection = getCurrentConnection();

		if (connection == null)
		{
			LOGGER.error("Connection existiert nicht !");

			throw new IllegalStateException("Connection existiert nicht !");
		}

		if (connection.getAutoCommit())
		{
			LOGGER.error("Connection ist im auto-commit Modus !");

			return;
		}

		if (connection.isClosed())
		{
			LOGGER.error("Connection ist bereits geschlossen !");

			return;
		}

		connection.commit();
		connection.close();

		connectionHolder.set(null);
	}

	/**
	 * Liefert die aktuelle Connection.
	 * 
	 * @return {@link Connection}
	 */
	public static Connection getCurrentConnection()
	{
		return connectionHolder.get();
	}

	/**
	 * Rollback der Transaktion.
	 * 
	 * @throws Exception Falls was schief geht.
	 */
	public static void rollbackTransaction() throws Exception
	{
		Connection connection = getCurrentConnection();

		if (connection == null)
		{
			LOGGER.error("Connection existiert nicht !");

			return;
		}

		if (connection.getAutoCommit())
		{
			LOGGER.error("Connection ist im auto-commit Modus !");

			return;
		}

		if (connection.isClosed())
		{
			LOGGER.error("Connection ist bereits geschlossen !");

			return;
		}

		connection.rollback();
		connection.close();

		connectionHolder.set(null);
	}

	/**
	 * Setzt die zu verwendende DataSource.
	 * 
	 * @param dataSource {@link DataSource}
	 * @throws IllegalArgumentException Falls was schief geht.
	 */
	public static void setDataSource(final DataSource dataSource)
	{
		if (dataSource == null)
		{
			throw new IllegalArgumentException("DataSource ist null !");
		}

		JDBCSession.dataSource = dataSource;
	}

	/**
	 * Setzt den JndiNamen der {@link DataSource}, es erfolgt ein Naming Lookup.
	 * 
	 * @param jndiName String
	 * @throws NamingException Falls was schief geht.
	 * @throws IllegalArgumentException Falls was schief geht.
	 */
	public static void setJndiName(final String jndiName) throws NamingException
	{
		if (jndiName == null)
		{
			throw new IllegalArgumentException("JndiName ist null !");
		}

		InitialContext context = new InitialContext();
		JDBCSession.dataSource = (DataSource) context.lookup(jndiName);
	}
}
