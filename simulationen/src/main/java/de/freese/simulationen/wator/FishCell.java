// Created: 04.10.2009
/**
 * 04.10.2009
 */
package de.freese.simulationen.wator;

import java.awt.Color;

/**
 * FischZelle der WaTor-Simulation.
 * 
 * @author Thomas Freese
 */
public class FishCell extends AbstractWatorCell
{
	/**
	 * Erstellt ein neues {@link FishCell} Object.
	 */
	FishCell()
	{
		super();

		setColor(Color.GREEN);
	}

	/**
	 * <ol>
	 * <li>Jeder Fisch schwimmt zufaellig auf eines der vier angrenzenden Felder, sofern es leer
	 * ist.
	 * <li>Mit jedem Durchgang gewinnt der Fisch einen Energiepunkt.
	 * <li>Uebersteigt die Energie den Wert fuer die Erzeugung eines Nachkommen ("Breed Energy"), so
	 * wird ein neuer Fisch auf einem angrenzenden freien Feld geboren. Die vorhandene Energie wird
	 * gleichmaessig zwischen altem und neuem Fisch verteilt.
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

		incrementEnergy();

		if (freie.length > 0)
		{
			// Bewegen
			int oldX = getX();
			int oldY = getY();

			int[] frei = freie[getWorld().getRandom().nextInt(freie.length)];
			int freiX = frei[0];
			int freiY = frei[1];
			moveTo(freiX, freiY);

			if (getEnergy() >= getWorld().getFishBreedEnergy())
			{
				try
				{
					// Nachwuchs einfach auf den alten Platz setzen
					FishCell child = (FishCell) getWorld().getObjectPoolFish().borrowObject();
					child.setWorld(getWorld());
					child.setEnergy(getWorld().getFishStartEnergy());

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

		setEdited(true);
	}
}
