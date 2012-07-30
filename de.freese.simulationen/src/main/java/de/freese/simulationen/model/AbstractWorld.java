// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * BasisModel fuer die Simulationen.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractWorld implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 2648983872881266870L;

	/**
	 *
	 */
	private Cell[][] cells = null;

	/**
	 *
	 */
	private final int height;

	/**
	 *
	 */
	private Random random = new Random();

	/**
	 *
	 */
	private final int width;

	/**
	 *
	 */
	private final List<WorldListener> worldListener = Collections
			.synchronizedList(new ArrayList<WorldListener>());

	/**
	 * Erstellt ein neues {@link AbstractWorld} Object.
	 * 
	 * @param width int
	 * @param height int
	 */
	public AbstractWorld(final int width, final int height)
	{
		super();

		this.width = width;
		this.height = height;
		this.cells = new Cell[width][height];
	}

	/**
	 * @param worldListener {@link WorldListener}
	 */
	public void addWorldListener(final WorldListener worldListener)
	{
		this.worldListener.add(worldListener);
	}

	/**
	 * Feuert das Event, wenn sich die Position oder Farbe einer Zelle geaendert hat.
	 * 
	 * @param x int
	 * @param y int
	 * @param cell {@link Cell}
	 */
	public void fireCellColorChanged(final int x, final int y, final Cell cell)
	{
		Color color = cell != null ? cell.getColor() : getNullCellColor();

		for (WorldListener listener : this.worldListener)
		{
			listener.cellColorChanged(x, y, color);
		}
	}

	/**
	 * Feuert das Event, wenn sich der Zustand der Welt geaendert hat.
	 */
	protected void fireWorldChanged()
	{
		for (WorldListener listener : this.worldListener)
		{
			listener.worldChanged(this);
		}
	}

	/**
	 * @param x int
	 * @param y int
	 * @return {@link Cell}
	 */
	public Cell getCell(final int x, final int y)
	{
		return this.cells[x][y];
	}

	/**
	 * @return int
	 */
	public int getHeight()
	{
		return this.height;
	}

	/**
	 * Farbe fuer nicht vorhandene Zellen.
	 * 
	 * @return {@link Color}
	 */
	public abstract Color getNullCellColor();

	/**
	 * @return {@link Random}
	 */
	public Random getRandom()
	{
		return this.random;
	}

	/**
	 * Liefert die entsprechende Torus-Koordinate.
	 * 
	 * @param size int, Groesse des Simulationsfeldes
	 * @param pos int, Aktuelle Position
	 * @param offSet int, Positionsaenderung
	 * @return int
	 */
	protected int getTorusKoord(final int size, final int pos, final int offSet)
	{
		if ((pos == 0) && (offSet < 0))
		{
			return size + offSet;
		}

		return ((size + 1) * (pos + offSet)) % size;
	}

	/**
	 * @return int
	 */
	public int getWidth()
	{
		return this.width;
	}

	/**
	 * Liefert die entsprechende Torus-Koordinate.
	 * 
	 * @param pos int, Aktuelle Position
	 * @param offSet int, Positionsaenderung
	 * @return int
	 */
	public int getXTorusKoord(final int pos, final int offSet)
	{
		return getTorusKoord(getWidth(), pos, offSet);
	}

	/**
	 * Liefert die entsprechende Torus-Koordinate.
	 * 
	 * @param pos int, Aktuelle Position
	 * @param offSet int, Positionsaenderung
	 * @return int
	 */
	public int getYTorusKoord(final int pos, final int offSet)
	{
		return getTorusKoord(getHeight(), pos, offSet);
	}

	/**
	 * Initialisierung des Simulationsfeldes.
	 */
	public abstract void initialize();

	/**
	 * Berechnet die naechste Generation.<br>
	 */
	public abstract void nextGeneration();

	/**
	 * @param cell {@link Cell}
	 * @param x int
	 * @param y int
	 */
	public void setCell(final Cell cell, final int x, final int y)
	{
		this.cells[x][y] = cell;

		fireCellColorChanged(x, y, cell);
	}
}
