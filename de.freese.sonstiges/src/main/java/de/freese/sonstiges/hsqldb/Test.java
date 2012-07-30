/**
 *
 */
package de.freese.sonstiges.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Thomas Freese
 */
public class Test
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		// Hier wird ein laufender Server benoetigt
		// String URL_SERVER_FILE = "jdbc:hsqldb:hsql://localhost:1234/fileDB";
		// String URL_SERVER_MEMORY = "jdbc:hsqldb:hsql://localhost:1234/memoryDB";

		// Hier wird der Server automatisch gestartet.
		// Datenverzeichniss ist ./data.
		// String URL_FILE = "jdbc:hsqldb:file:./data/fileDB";

		//
		// Hier wird der Server automatisch gestartet.
		// Es erfolgt keine Festplattenzugriff, alle Daten sind im Speicher.
		String URL_MEMORY = "jdbc:hsqldb:mem:memoryDB";
		Test test = new Test();

		test.connect("org.hsqldb.jdbcDriver", URL_MEMORY, "sa", "");

		test.clear();
		test.createSequence();
		test.createTable();
		test.insert("Thomassss");
		test.insert("Freesesss");
		test.select();

		test.disconnect();
	}

	/**
     * 
     */
	private Connection connection = null;

	/**
     *
     */
	private Statement statement = null;

	/**
	 * Erstellt ein neues {@link Test} Object.
	 */
	public Test()
	{
		super();
	}

	/**
     * 
     */
	public void clear()
	{
		try
		{
			this.statement = this.connection.createStatement();
			this.statement.execute("DROP TABLE DUAL IF EXISTS");
			this.statement.close();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}

		try
		{
			this.statement = this.connection.createStatement();
			this.statement.execute("DROP SEQUENCE TEST_SEQ IF EXISTS");
			this.statement.close();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}

		try
		{
			this.statement = this.connection.createStatement();
			this.statement.execute("DROP TABLE TEST IF EXISTS");
			this.statement.close();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
	}

	/**
	 * @param driverName String
	 * @param url String
	 * @param user String
	 * @param psw String
	 */
	public void connect(final String driverName, final String url, final String user,
						final String psw)
	{
		// Abfragen der Verbindungsparameter
		System.out.println("Datenbankzugriff mit JDBC");
		System.out.println("=========================");

		// Treiber laden und Verbindung herstellen
		try
		{
			Class.forName(driverName);

			// Verbindung mit der Datenbank aufnehmen
			this.connection = DriverManager.getConnection(url, user, psw);
			System.out.println("\nVerbinden ...");
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			System.out.println("Fehler beim Verbindungsaufbau!");
			System.exit(0);
		}
		catch (ClassNotFoundException ex)
		{
			ex.printStackTrace();
			System.out.println("JDBC Treiber nicht gefunden!");
			System.exit(0);
		}

		System.out.println("Verbindungsaufbau erfolgreich\n");
	}

	/**
     * 
     */
	public void createSequence()
	{
		try
		{
			this.statement = this.connection.createStatement();
			this.statement.execute("CREATE MEMORY TABLE DUAL(DUMMY VARCHAR(1))");
			this.statement.execute("INSERT INTO DUAL VALUES(NULL)");
			this.statement.close();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}

		try
		{
			this.statement = this.connection.createStatement();
			this.statement.execute("CREATE SEQUENCE TEST_SEQ AS BIGINT START WITH 1");
			this.statement.close();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
	}

	/**
     * 
     */
	public void createTable()
	{
		try
		{
			this.statement = this.connection.createStatement();
			this.statement
					.execute("CREATE MEMORY TABLE TEST(ID BIGINT NOT NULL PRIMARY KEY, NAME VARCHAR(50) NOT NULL, CONSTRAINT TEST_UNQ UNIQUE(NAME))");
			this.statement.close();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
	}

	/**
     *
     */
	public void disconnect()
	{
		try
		{
			this.statement = this.connection.createStatement();
			this.statement.execute("SHUTDOWN COMPACT");
			this.statement.close();
			this.connection.close();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
	}

	/**
	 * @param name String
	 */
	public void insert(final String name)
	{
		try
		{
			this.statement = this.connection.createStatement();

			ResultSet result =
					this.statement.executeQuery("SELECT NEXT VALUE FOR TEST_SEQ FROM DUAL");

			int pk = 0;

			while (result.next())
			{
				pk = result.getInt(1);
			}

			this.statement.execute("INSERT INTO TEST (ID,NAME) VALUES(" + pk + ",'" + name + "')");
			this.statement.close();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
	}

	/**
     * 
     */
	public void select()
	{
		try
		{
			this.statement = this.connection.createStatement();

			ResultSet result = this.statement.executeQuery("SELECT * FROM TEST");

			while (result.next())
			{
				System.out.print(result.getInt(1));
				System.out.println("; " + result.getString(2));
			}

			this.statement.close();
		}
		catch (SQLException e1)
		{
			e1.printStackTrace();
		}
	}
}
