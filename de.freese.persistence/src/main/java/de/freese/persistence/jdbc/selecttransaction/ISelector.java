package de.freese.persistence.jdbc.selecttransaction;

import java.sql.Connection;
import java.util.List;

/**
 * Interface fuer Select-Objekte des Frameworks Select-Transaction.
 * 
 * @author Thomas Freese
 */
public interface ISelector
{
	/**
	 * @return Liste aller Connections des Selectors
	 */
	public List<Connection> getConnections();

	/**
	 * Speichern des Ergebnisses der select-Abfrage
	 * 
	 * @param result Object
	 */
	public void setSelectorResult(Object result);

	/**
	 * @return Ergebnis der select-Abfrage
	 */
	public Object getSelectorResult();

	/**
	 * @param connection {@link Connection}
	 */
	public void addConnection(Connection connection);

	/**
	 * Loeschen aller Verbindungen
	 */
	public void release();

	/**
	 * Durchfuehrung der Abfrage
	 * 
	 * @throws Exception Falls was schief geht.
	 */
	public void select() throws Exception;
}
