/**
 * 05.04.2008
 */
package de.freese.queryengine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * Erzeugt die DataSource zum Testen.
 * 
 * @author Thomas Freese
 */
public final class JDBCUtils
{
	/**
	 * 
	 */
	private static DataSource dataSource = null;

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final String URL_SERVER = "jdbc:hsqldb:hsql://localhost:1234/queryEngine";

	/**
	 * 
	 */
	private static final String URL_FILE = "jdbc:hsqldb:file:hsqldb/queryEngine;shutdown=true";

	static
	{
		final BasicDataSource basicDataSource = new BasicDataSource();

		JDBCUtils.dataSource = basicDataSource;

		basicDataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		basicDataSource.setUrl(URL_FILE);
		basicDataSource.setUsername("SA");
		basicDataSource.setPassword("");
		basicDataSource.setMaxActive(2);
		basicDataSource.setMaxIdle(1);
		basicDataSource.setTestOnBorrow(true);
		basicDataSource.setValidationQuery("select CURDATE() as date from dual");

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			/**
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run()
			{
				try
				{
					System.out.println("Close DataSource: " + basicDataSource.getUrl() + " with "
							+ basicDataSource.getNumIdle() + " idle and "
							+ basicDataSource.getNumActive() + " active Connections.");
					basicDataSource.close();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
	}

	/**
	 * Liefert die {@link DataSource} zum Testen.
	 * 
	 * @return DataSource
	 */
	public static DataSource getDataSource()
	{
		return dataSource;
	}

	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		shutdownServer();
	}

	/**
	 * 
	 */
	public static void shutdownServer()
	{
		Connection connection = null;

		try
		{
			connection = getDataSource().getConnection();

			Statement statement = connection.createStatement();

			statement.executeQuery("SHUTDOWN COMPACT");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Liefert die naechste SequenceID.
	 * 
	 * @return long
	 */
	public long getNextSequenceID()
	{
		long sequenceID = 0;

		Connection connection = null;

		try
		{
			connection = getDataSource().getConnection();

			Statement statement = connection.createStatement();

			ResultSet result = statement.executeQuery("SELECT NEXT VALUE FOR TEST_SEQ FROM DUAL");

			while (result.next())
			{
				sequenceID = result.getLong(1);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}

		return sequenceID;
	}
}
