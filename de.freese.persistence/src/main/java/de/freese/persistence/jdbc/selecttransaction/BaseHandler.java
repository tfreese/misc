package de.freese.persistence.jdbc.selecttransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Grundklasse fuer CallbackHandler des Frameworks Select-Transaction
 * 
 * @author Thomas Freese
 */
public class BaseHandler implements ISelectCallbackHandler
{
	/**
	 *
	 */
	private List<ISelector> iSelectors = null;

	// /**
	// *
	// */
	// private boolean isExcecuted = false;

	/**
	 * Holt Ergebnis eines Selectors aus dem CallbackHandler
	 * 
	 * @param i Index des Results
	 * @return Object
	 * @see ISelectCallbackHandler#getSelectorResult(int i)
	 */
	@Override
	public Object getSelectorResult(final int i)
	{
		ISelector s = this.iSelectors.get(i);

		return s.getSelectorResult();
	}

	/**
	 * Speichern des Selectors in den CallbackHandler nach Abfrage
	 * 
	 * @param iSelector {@link ISelector}
	 * @see ISelectCallbackHandler#selectorExcecuted(ISelector selector)
	 */
	@Override
	public void selectorExcecuted(final ISelector iSelector)
	{
		if (this.iSelectors == null)
		{
			this.iSelectors = new ArrayList<>();
		}

		this.iSelectors.add(iSelector);
	}
}
