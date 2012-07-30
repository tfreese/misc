package de.freese.persistence.jdbc.selecttransaction;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * BaseSelector
 * 
 * @author Thomas Freese
 */
public abstract class BaseSelector implements ISelector
{
	/**
	 * Haelt die Verbindungen
	 */
	private List<Connection> connections = null;

	/**
	 * Haelt das select-Ergebnis
	 */
	private Object result = null;

	/**
	 * Hinzufuegen einer Connection zum ISelector
	 * 
	 * @param connection {@link Connection}
	 */
	@Override
	public void addConnection(final Connection connection)
	{
		if (this.connections == null)
		{
			this.connections = new ArrayList<>();
		}

		this.connections.add(connection);
	}

	/**
	 * Liefert eine Liste aller Connections des Selectors
	 * 
	 * @return list {@link List}
	 * @see ISelector#getConnections()
	 */
	@Override
	public List<Connection> getConnections()
	{
		return this.connections;
	}

	/**
	 * Liefert das Ergebnis der select-Abfrage
	 * 
	 * @return Object Ergebnis der select-Abfrage
	 * @see ISelector#getSelectorResult()
	 */
	@Override
	public Object getSelectorResult()
	{
		return this.result;
	}

	/**
	 * Schliessen und Loeschen aller Verbindungen
	 * 
	 * @see ISelector#release()
	 */
	@Override
	public void release()
	{
		// Auslesen der Verbindungen dieses Selectors
		for (int i = 0; i < this.connections.size(); i++)
		{
			Connection connection = this.connections.get(i);

			try
			{
				connection.commit();
				connection.close();
			}
			catch (Exception ex)
			{
				// Ignore
			}
		}

		this.connections.clear();
		this.connections = null;

		try
		{
			finalize();
		}
		catch (Throwable th)
		{
			// Ignore
		}
	}

	/**
	 * Ausfuehrung der Abfrage
	 * 
	 * @see ISelector#select()
	 */
	@Override
	public abstract void select() throws Exception;

	/**
	 * Speichern des Ergebnisses der select-Abfrage
	 * 
	 * @param result Object
	 */
	@Override
	public void setSelectorResult(final Object result)
	{
		this.result = result;
	}
}
