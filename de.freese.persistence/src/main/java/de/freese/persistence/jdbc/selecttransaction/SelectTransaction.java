package de.freese.persistence.jdbc.selecttransaction;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasse zum Abarbeiten der select-Methoden von Selectoren
 * 
 * @author Thomas Freese
 */
public class SelectTransaction
{
	// Enthaelt nur eindeutige Verbindungen, keine Redundanz

	/**
	 * 
	 */
	private final List<Connection> connections = new ArrayList<>();

	/**
	 * 
	 */
	private List<ISelector> selectors = new ArrayList<>();

	/**
	 * 
	 */
	private ISelectCallbackHandler sch = null;

	/**
	 * 
	 */
	private boolean autoRelease = false;

	/**
	 * Creates a new {@link SelectTransaction} object.
	 * 
	 * @param sch {@link ISelectCallbackHandler}
	 */
	public SelectTransaction(final ISelectCallbackHandler sch)
	{
		this(sch, true);
	}

	/**
	 * Erstellt ein neues {@link SelectTransaction} Object.
	 * 
	 * @param sch {@link ISelectCallbackHandler}
	 * @param autoRelease boolean
	 */
	public SelectTransaction(final ISelectCallbackHandler sch, final boolean autoRelease)
	{
		super();

		this.sch = sch;
		this.autoRelease = autoRelease;
	}

	/**
	 * Hinzufuegen eines Selectors in die interne Liste
	 * 
	 * @param iSelector {@link ISelector}
	 */
	public void addSelector(final ISelector iSelector)
	{
		this.selectors.add(iSelector);
	}

	/**
	 * Liefert die Liste der Selectoren
	 * 
	 * @return {@link List}
	 */
	public List<ISelector> getSelectors()
	{
		return this.selectors;
	}

	/**
	 * Automatischer Aufruf von release() nach start()-Methode ? Default = false
	 * 
	 * @return boolean
	 */
	public boolean isAutoRelease()
	{
		return this.autoRelease;
	}

	/**
	 * Schliessen aller ISelector-Verbindungen und loeschen aller Selectoren
	 */
	public void release()
	{
		// Release der Verbindungen aller Selectoren
		for (ISelector iSelector : this.selectors)
		{
			iSelector.release();
		}

		this.selectors.clear();
		this.selectors = null;

		// setAutoRelease(true);
		// _sch = null;
		// _connections.clear();
		// _connections = null;
		// _selectors.clear();
		// _selectors = null;
	}

	/**
	 * Setzen des autoRelease Attributes
	 * 
	 * @param release true=Aufruf automatisch; false=Aufruf manuell
	 */
	public void setAutoRelease(final boolean release)
	{
		this.autoRelease = release;
	}

	/**
	 * Ausfuehren aller select()-Methoden der registrierten Selectoren
	 * 
	 * @throws SelectTransactionException Falls was schief geht.
	 */
	public void start() throws SelectTransactionException
	{
		try
		{
			// Auslesen der Verbindungen aller Selectoren
			for (ISelector iSelector : this.selectors)
			{
				for (Connection connection : iSelector.getConnections())
				{
					// Die Connection wird nur in die Liste aufgenommen, wenn sie noch nicht in ihr
					// enthalten ist,
					// d.h. der Aufruf der commit()-Methode pro Connection erfolgt spaeter nur
					// einmal
					// !!!
					if (!this.connections.contains(connection))
					{
						connection.setAutoCommit(false);
						this.connections.add(connection);
					}
				}
			}

			// Ausfuehren der select()-Methode aller registrierten Selectoren
			for (ISelector iSelector : this.selectors)
			{
				iSelector.select();

				this.sch.selectorExcecuted(iSelector);
			}

			// commit aller eindeutigen Verbindungen
			for (Connection connection : this.connections)
			{
				connection.commit();
			}
		}
		catch (Exception e)
		{
			throw new SelectTransactionException("SelectTransaction failed: " + e.getMessage());
		}

		// Bei autoRelease = true, Aufruf release()-Methode aller Selectoren
		if (this.autoRelease)
		{
			release();
		}
	}
}
