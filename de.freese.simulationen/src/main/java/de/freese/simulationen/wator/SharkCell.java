// Created: 04.10.2009
/**
 * 04.10.2009
 */
package de.freese.simulationen.wator;

import java.awt.Color;

/**
 * HaiZelle der WaTor-Simulation.
 * 
 * @author Thomas Freese
 */
public class SharkCell extends AbstractWatorCell
{
	/**
	 * Erstellt ein neues {@link FishCell} Object.
	 */
	SharkCell()
	{
		super();

		setColor(Color.BLUE);
	}

	/**
	 * <ol>
	 * <li>Findet ein Hai keinen Fisch auf einem angrenzenden Feld, so schwimmt er zufaellig auf
	 * eines der vier Felder.
	 * <li>Fuer jeden Zyklus, waehrend dessen der Hai keinen Fisch findet, verliert er einen
	 * Energiepunkt.
	 * <li>Findet der Hai einen Fisch, wird seine Energie um den Energiewert des Fisches erhoeht.
	 * <li>Uebersteigt die Energie den Wert fuer die Erzeugung eines Nachkommen ("Breed Energy"), so
	 * wird ein neuer Hai auf einem angrenzenden freien Feld geboren. Die vorhandene Energie wird
	 * gleichmaessig zwischen altem und neuem Hai verteilt.
	 * </ol>
	 * 
	 * @see de.freese.simulationen.model.Cell#nextGeneration(java.lang.Object[])
	 */
	@Override
	public void nextGeneration(final Object...params)
	{
		if (isEdited())
		{
			return;
		}

		int[][][] cells = getNachbarn();
		int[][] freie = cells[0];
		int[][] fische = cells[1];

		int oldX = getX();
		int oldY = getY();

		if ((fische.length > 0) || (freie.length > 0))
		{
			if (fische.length > 0)
			{
				// Fressen
				int[] fisch = fische[getWorld().getRandom().nextInt(fische.length)];
				int fischX = fisch[0];
				int fischY = fisch[1];

				FishCell fishCell = (FishCell) getWorld().getCell(fischX, fischY);

				moveTo(fischX, fischY);

				setEnergy(fishCell.getEnergy() + getEnergy());

				try
				{
					getWorld().getObjectPoolFish().returnObject(fishCell);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			else
			{
				// Bewegen
				int[] frei = freie[getWorld().getRandom().nextInt(freie.length)];
				int freiX = frei[0];
				int freiY = frei[1];

				moveTo(freiX, freiY);

				decrementEnergy();
			}

			if (getEnergy() >= getWorld().getSharkBreedEnergy())
			{
				try
				{
					// Nachwuchs einfach auf den alten Platz setzen
					SharkCell child = (SharkCell) getWorld().getObjectPoolShark().borrowObject();
					child.setWorld(getWorld());
					child.setEnergy(getWorld().getSharkStartEnergy());

					child.moveTo(oldX, oldY);

					// Energie aufteilen
					child.setEnergy(getEnergy() / 2);
					setEnergy(getEnergy() - child.getEnergy());

					child.setEdited(true);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		else
		{
			decrementEnergy();
		}

		if (getEnergy() <= getWorld().getSharkStarveEnergy())
		{
			// Sterben
			getWorld().setCell(null, getX(), getY());

			try
			{
				getWorld().getObjectPoolShark().returnObject(this);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		setEdited(true);
	}
}
