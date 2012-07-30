// Created: 04.10.2009
/**
 * 04.10.2009
 */
package de.freese.simulationen.wator;

import java.util.Arrays;

import de.freese.simulationen.model.AbstractCell;
import de.freese.simulationen.model.Cell;

/**
 * Zelle der WaTor-Simulation.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractWatorCell extends AbstractCell<WaTorWorld>
{
	/**
	 * 
	 */
	private boolean edited = false;

	/**
	 *
	 */
	private int energy = 0;

	/**
	 * Erstellt ein neues {@link AbstractWatorCell} Object.
	 */
	public AbstractWatorCell()
	{
		super();
	}

	/**
	 * Erniedrigt den Energiewert um 1.
	 */
	void decrementEnergy()
	{
		this.energy--;
	}

	/**
	 * Liefert den Energiewert der Zelle.
	 * 
	 * @return int
	 */
	int getEnergy()
	{
		return this.energy;
	}

	/**
	 * Liefert die Nachbarn dieser Zelle. Es werden nur vertikale und horizontale Nachbarn
	 * beruecksichtigt.
	 * 
	 * @return int[][][]; 0=Index, 1=x, 2=y
	 */
	int[][][] getNachbarn()
	{
		int xM1 = getWorld().getXTorusKoord(getX(), -1);
		int xP1 = getWorld().getXTorusKoord(getX(), +1);
		int yM1 = getWorld().getYTorusKoord(getY(), -1);
		int yP1 = getWorld().getYTorusKoord(getY(), +1);

		int[][] cells = new int[4][2];

		int[][] freie = new int[0][2];
		int[][] fische = new int[0][2];
		// int[][] haie = new int[0][2];

		// Norden
		cells[0][0] = getX();
		cells[0][1] = yM1;

		// Osten
		cells[1][0] = xP1;
		cells[1][1] = getY();

		// Sueden
		cells[2][0] = getX();
		cells[2][1] = yP1;

		// Westen
		cells[3][0] = xM1;
		cells[3][1] = getY();

		for (int[] cell2 : cells)
		{
			int x = cell2[0];
			int y = cell2[1];

			Cell cell = getWorld().getCell(x, y);

			if (cell == null)
			{
				freie = Arrays.copyOf(freie, freie.length + 1);
				freie[freie.length - 1] = cell2;
			}
			else if (cell instanceof FishCell)
			{
				fische = Arrays.copyOf(fische, fische.length + 1);
				fische[fische.length - 1] = cell2;
			}
			// else if (cell instanceof SharkCell)
			// {
			// haie = Arrays.copyOf(haie, haie.length + 1);
			// haie[haie.length - 1] = cell2;
			// }
		}

		return new int[][][]
		{
				freie, fische
		// , haie
		};
	}

	/**
	 * Erhoeht den Energiewert um 1.
	 */
	void incrementEnergy()
	{
		this.energy++;
	}

	/**
	 * True, wenn diese Zelle in einem Zyklus schon mal verarbeitet wurde.
	 * 
	 * @return boolean
	 */
	boolean isEdited()
	{
		return this.edited;
	}

	/**
	 * True, wenn diese Zelle in einem Zyklus schon mal verarbeitet wurde.
	 * 
	 * @param edited boolean
	 */
	void setEdited(final boolean edited)
	{
		this.edited = edited;
	}

	/**
	 * @param energy int
	 */
	void setEnergy(final int energy)
	{
		this.energy = energy;
	}
}
