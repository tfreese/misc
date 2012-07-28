// Created: 13.09.2009
/**
 * 13.09.2009
 */
package de.freese.simulationen.gameoflife;

import java.awt.Color;

import de.freese.simulationen.model.AbstractWorld;

/**
 * Model fuer die "Game of Life"-Simulation.
 * 
 * @author Thomas Freese
 */
public class GoFWorld extends AbstractWorld
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7569187325874082629L;

	/**
	 * Erstellt ein neues {@link GoFWorld} Object.
	 * 
	 * @param width int
	 * @param height int
	 */
	public GoFWorld(final int width, final int height)
	{
		super(width, height);
	}

	/**
	 * @see de.freese.simulationen.model.AbstractWorld#getNullCellColor()
	 */
	@Override
	public Color getNullCellColor()
	{
		return Color.WHITE;
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
				GoFCell cell = new GoFCell();
				cell.setWorld(this);
				cell.setAlive(getRandom().nextBoolean());
				cell.moveTo(x, y);
			}
		}

		// Gleiter
		// getCells()[21][20] = true;
		// getCells()[22][21] = true;
		// getCells()[20][20] = true;
		// getCells()[20][21] = true;
		// getCells()[20][22] = true;

		fireWorldChanged();
	}

	/**
	 * @see de.freese.simulationen.model.AbstractWorld#nextGeneration()
	 */
	@Override
	public synchronized void nextGeneration()
	{
		// Alte Generation sichern
		GoFCell[][] oldGeneration = new GoFCell[getWidth()][getHeight()];

		for (int x = 0; x < getWidth(); x++)
		{
			for (int y = 0; y < getHeight(); y++)
			{
				GoFCell cell = (GoFCell) getCell(x, y);

				oldGeneration[x][y] = cell.getCopy();
			}
		}

		for (int x = 0; x < getWidth(); x++)
		{
			for (int y = 0; y < getHeight(); y++)
			{
				getCell(x, y).nextGeneration((Object[]) oldGeneration);
			}
		}

		fireWorldChanged();
	}
}
