// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.gameoflife;

import java.awt.Color;

import de.freese.simulationen.model.AbstractCell;

/**
 * Zelle fuer die "Spiel des Lebens" Simulation.
 * 
 * @author Thomas Freese
 */
public class GoFCell extends AbstractCell<GoFWorld>
{
	/**
	 *
	 */
	private boolean alive = true;

	/**
	 * Erstellt ein neues {@link GoFCell} Object.
	 */
	public GoFCell()
	{
		super();
	}

	/**
	 * @see de.freese.simulationen.model.Cell#getColor()
	 */
	@Override
	public Color getColor()
	{
		return isAlive() ? Color.BLACK : Color.WHITE;
	}

	/**
	 * Erstellt eine Kopie dieser Zelle.
	 * 
	 * @return {@link GoFCell}
	 */
	public GoFCell getCopy()
	{
		GoFCell cell = new GoFCell();
		cell.setWorld(getWorld());
		cell.setXY(getX(), getY());
		cell.alive = this.alive;

		return cell;
	}

	/**
	 * Liefert die Anzahl der lebenden Nachbarn.<br>
	 * Quadrat von 3x3 Zellen pruefen, mit dieser Zelle in der Mitte.
	 * 
	 * @param oldGeneration GoFCell[][], die alte Generation
	 * @return byte
	 */
	private byte getLebendeBachbarn(final GoFCell[][] oldGeneration)
	{
		byte alive = 0;

		// if ((getX() == 0) && (getY() == 0))
		// {
		// System.out.println();
		// }
		//
		// if ((getX() == 1) && (getY() == 1))
		// {
		// System.out.println();
		// }

		int x = getWorld().getXTorusKoord(getX(), -1);

		for (int xIndex = 0; xIndex < 3; xIndex++)
		{
			int y = getWorld().getYTorusKoord(getY(), -1);

			for (int yIndex = 0; yIndex < 3; yIndex++)
			{
				// String text = String.format("[%d,%d] - %d,%d", getX(), getY(), x, y);
				// System.out.println(text);

				// Diese Zelle ausnehmen
				if (!((x == getX()) && (y == getY())))
				{
					if (oldGeneration[x][y].isAlive())
					{
						alive++;
					}
				}

				y = getWorld().getYTorusKoord(y, +1);
			}

			x = getWorld().getXTorusKoord(x, +1);
		}

		return alive;
	}

	/**
	 * @return boolean
	 */
	boolean isAlive()
	{
		return this.alive;
	}

	/**
	 * <ol>
	 * <li>Eine tote Zelle mit genau drei lebenden Nachbarn wird in der nächsten Generation neu
	 * geboren.
	 * <li>Lebende Zellen mit weniger als zwei lebenden Nachbarn sterben in der nächsten Generation
	 * an Einsamkeit.
	 * <li>Eine lebende Zelle mit zwei oder drei lebenden Nachbarn bleibt in der nächsten Generation
	 * lebend.
	 * <li>Lebende Zellen mit mehr als drei lebenden Nachbarn sterben in der nächsten Generation an
	 * Ueberbevoelkerung.
	 * </ol>
	 * 
	 * @see de.freese.simulationen.model.Cell#nextGeneration(java.lang.Object[])
	 */
	@Override
	public void nextGeneration(final Object...params)
	{
		GoFCell[][] oldGeneration = (GoFCell[][]) params;

		boolean alive = oldGeneration[getX()][getY()].isAlive();
		byte lebendeNachbarn = getLebendeBachbarn(oldGeneration);

		if (!alive && (lebendeNachbarn == 3))
		{
			// 1.
			setAlive(true);
		}
		else if (alive && (lebendeNachbarn < 2))
		{
			// 2.
			setAlive(false);
		}
		else if (alive && ((lebendeNachbarn == 2) || (lebendeNachbarn == 3)))
		{
			// 3.
			setAlive(true);
		}
		else if (alive && (lebendeNachbarn > 3))
		{
			// 4.
			setAlive(false);
		}

		getWorld().fireCellColorChanged(getX(), getY(), this);
	}

	/**
	 * @param alive boolean
	 */
	void setAlive(final boolean alive)
	{
		this.alive = alive;
	}
}
