/**
 *
 */
package de.freese.jpa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Thomas Freese
 */
public class TestHSQL
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		// Hier wird ein laufender Server benoetigt
		// String URL = "jdbc:hsqldb:hsql://localhost:1234/fileDB";
		// String URL = "jdbc:hsqldb:hsql://localhost:1234/memoryDB";

		// Hier wird der Server automatisch gestartet.
		// String URL_FILE = "jdbc:hsqldb:file:D:/hsqldb/fileDB/fileDB";
		// String URL = "jdbc:hsqldb:file:hsqldb/hibernateJPA";

		//
		// Hier wird er Server automatisch gestartet.
		// Es erfolgt keine Festplattenzugriff, alle Daten sind im Speicher.
		String URL = "jdbc:hsqldb:mem:memoryDB";

		TestHSQL test = new TestHSQL();

		test.connect("org.hsqldb.jdbcDriver", URL, "sa", "");

		// test.clear();
		// test.createSequence();
		// test.createTable();
		// test.insert("Freese2", "Thomas2");

		// test.insert("Freesesss");
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
	 * Creates a new {@link TestHSQL} object.
	 */
	public TestHSQL()
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
			this.statement.execute("DROP TABLE DUAL");
			this.statement.close();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}

		try
		{
			this.statement = this.connection.createStatement();
			this.statement.execute("DROP SEQUENCE TEST_SEQ");
			this.statement.close();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}

		try
		{
			this.statement = this.connection.createStatement();
			this.statement.execute("DROP TABLE TEST");
			this.statement.close();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
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
			this.statement.execute("CREATE MEMORY TABLE DUAL(DUMMY VARCHAR)");
			this.statement.execute("INSERT INTO DUAL VALUES(NULL)");
			this.statement.close();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}

		try
		{
			this.statement = this.connection.createStatement();
			this.statement.execute("CREATE SEQUENCE TEST_SEQ AS BIGINT START WITH 1");
			this.statement.close();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
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
		catch (SQLException ex)
		{
			ex.printStackTrace();
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
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * @param name String
	 * @param vorname String
	 */
	public void insert(final String name, final String vorname)
	{
		try
		{
			this.statement = this.connection.createStatement();

			ResultSet result =
					this.statement
							.executeQuery("SELECT NEXT VALUE FOR PERSON_SEQ FROM DUAL_PERSON_SEQ");

			int pk = 0;

			while (result.next())
			{
				pk = result.getInt(1);
			}

			this.statement.execute("INSERT INTO PERSON (PERSON_PK,NAME,VORNAME) VALUES(" + pk
					+ ",'" + name + "','" + vorname + "')");
			this.statement.close();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
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

			ResultSet result = this.statement.executeQuery("SELECT * FROM PERSON");

			while (result.next())
			{
				System.out.print(result.getInt(1));
				System.out.println("; " + result.getString(2) + " " + result.getString(3));
			}

			this.statement.close();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
}
