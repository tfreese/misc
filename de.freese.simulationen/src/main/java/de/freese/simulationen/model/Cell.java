// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.model;

import java.awt.Color;

/**
 * Einzelne Zelle einer Welt.
 * 
 * @author Thomas Freese
 */
public interface Cell
{
	/**
	 * @return {@link Color}
	 */
	public Color getColor();

	/**
	 * @return int
	 */
	public int getX();

	/**
	 * @return int
	 */
	public int getY();

	/**
	 * Setzt die Position ohne die Zelle zu verschieben.
	 * 
	 * @param x int
	 * @param y int
	 */
	public void setXY(int x, int y);

	/**
	 * Setzt die Position und verschiebt die Zelle. Die alte Position wird auf null gesetzt.
	 * 
	 * @param x int
	 * @param y int
	 */
	public void moveTo(int x, int y);

	/**
	 * Berechnet die naechste Generation.<br>
	 * 
	 * @param params Object[], optionale Parameter
	 */
	public abstract void nextGeneration(Object...params);
}
