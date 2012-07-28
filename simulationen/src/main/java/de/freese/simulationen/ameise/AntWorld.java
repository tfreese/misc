// Created: 13.09.2009
/**
 * 13.09.2009
 */
package de.freese.simulationen.ameise;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingConstants;

import de.freese.simulationen.model.AbstractWorld;

/**
 * Model der Ameisen-Simulation.
 * 
 * @author Thomas Freese
 */
public class AntWorld extends AbstractWorld
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7569187325874082629L;

	/**
	 *
	 */
	private final Set<AntCell> antCells = new HashSet<>();

	/**
	 * Erstellt ein neues {@link AntWorld} Object.
	 * 
	 * @param width int
	 * @param height int
	 */
	public AntWorld(final int width, final int height)
	{
		super(width, height);

		setNumberOfAnts(5);
	}

	/**
	 * @return {@link Set}<AntCell>
	 */
	public Set<AntCell> getAntCells()
	{
		return this.antCells;
	}

	/**
	 * @see de.freese.simulationen.model.AbstractWorld#getNullCellColor()
	 */
	@Override
	public Color getNullCellColor()
	{
		return Color.LIGHT_GRAY;
	}

	/**
	 * @see de.freese.simulationen.model.AbstractWorld#initialize()
	 */
	@Override
	public void initialize()
	{
		for (int x = 0; x < getWidth(); x++)
		{
			for (int y = 0; y < getHeight(); y++)
			{
				setCell(null, x, y);
			}
		}

		// Zufaellige Startpositionen
		int[] orientation =
				new int[]
				{
						SwingConstants.NORTH,
						SwingConstants.EAST,
						SwingConstants.SOUTH,
						SwingConstants.WEST
				};

		// Etwas zentriert ansiedeln
		int minX = (getWidth() / 2) - 5;
		int minY = (getHeight() / 2) - 5;

		for (AntCell cell : this.antCells)
		{
			int x = getRandom().nextInt(10) + minX;
			int y = getRandom().nextInt(10) + minY;

			cell.setXY(x, y);
			cell.setOrientation(orientation[getRandom().nextInt(4)]);
		}

		fireWorldChanged();
	}

	/**
	 * @see de.freese.simulationen.model.AbstractWorld#nextGeneration()
	 */
	@Override
	public synchronized void nextGeneration()
	{
		for (AntCell cell : this.antCells)
		{
			cell.nextGeneration();
		}

		fireWorldChanged();
	}

	/**
	 * @param ants int
	 */
	public void setNumberOfAnts(final int ants)
	{
		if (ants < 1)
		{
			throw new IllegalArgumentException("Anzahl < 1");
		}

		this.antCells.clear();

		for (int i = 0; i < ants; i++)
		{
			AntCell cell = new AntCell();
			cell.setWorld(this);

			this.antCells.add(cell);
		}
	}
}
