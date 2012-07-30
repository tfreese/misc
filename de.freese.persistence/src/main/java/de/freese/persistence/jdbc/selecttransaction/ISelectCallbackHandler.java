package de.freese.persistence.jdbc.selecttransaction;

/**
 * Abstrakte Grundklasse fuer Select-Objekte des Frameworks Select-Transaction.
 * 
 * @author Thomas Freese
 */
public interface ISelectCallbackHandler
{
	/**
	 * @param i Index Holt Ergebnis eines Selectors aus dem CallbackHandler
	 * @return Object
	 */
	public Object getSelectorResult(int i);

	/**
	 * @param iSelector {@link ISelector} Speichern des Selectors in den CallbackHandler nach Abfrage
	 */
	public void selectorExcecuted(ISelector iSelector);
}
