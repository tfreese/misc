// Created: 17.10.2009
/**
 * 17.10.2009
 */
package de.freese.simulationen.model;

import java.awt.Color;

/**
 * Beobachter fuer Farbaenderungen einer Zelle.
 * 
 * @author Thomas Freese
 */
public interface WorldListener
{
	/**
	 * @param world {@link AbstractWorld}
	 */
	public void worldChanged(AbstractWorld world);

	/**
	 * Wird vom Model aufgerufen, wenn an den Koordinaten eine neue Zelle gesetzt wird.
	 * 
	 * @param x int
	 * @param y int
	 * @param color {@link Color}
	 */
	public void cellColorChanged(int x, int y, Color color);
}
