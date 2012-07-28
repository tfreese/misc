// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.ameise;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import de.freese.simulationen.model.AbstractCell;
import de.freese.simulationen.model.Cell;
import de.freese.simulationen.model.EmptyCell;

/**
 * Zelle der Ameisen-Simulation.
 * 
 * @author Thomas Freese
 */
public class AntCell extends AbstractCell<AntWorld>
{
	/**
	 * @author Thomas Freese
	 */
	private static enum Direction
	{
		/**
		 *
		 */
		EAST(0, -1, 0, +1),
		/**
		 *
		 */
		NORTH(-1, 0, +1, 0),
		/**
		 *
		 */
		SOUTH(+1, 0, -1, 0),
		/**
		 *
		 */
		WEST(0, +1, 0, -1);

		/**
		 *
		 */
		private static final List<Direction> DIRECTIONS;

		static
		{
			DIRECTIONS = new ArrayList<>();
			DIRECTIONS.add(NORTH);
			DIRECTIONS.add(EAST);
			DIRECTIONS.add(SOUTH);
			DIRECTIONS.add(WEST);
		}

		/**
		 *
		 */
		private final int offsetXLeft;

		/**
		 *
		 */
		private final int offsetXRight;

		/**
		 *
		 */
		private final int offsetYLeft;

		/**
		 *
		 */
		private final int offsetYRight;

		/**
		 * Erstellt ein neues {@link Direction} Object.
		 * 
		 * @param offsetXLeft int
		 * @param offsetYLeft int
		 * @param offsetXRight int
		 * @param offsetYRight int
		 */
		private Direction(final int offsetXLeft, final int offsetYLeft, final int offsetXRight,
				final int offsetYRight)
		{
			this.offsetXLeft = offsetXLeft;
			this.offsetYLeft = offsetYLeft;
			this.offsetXRight = offsetXRight;
			this.offsetYRight = offsetYRight;
		}

		/**
		 * BerechnungsOperatoren fuer die naechste Position nach Drehung.
		 * 
		 * @return int[]
		 */
		public int[] getLeftOffsets()
		{
			return new int[]
			{
					this.offsetXLeft, this.offsetYLeft
			};
		}

		/**
		 * BerechnungsOperatoren fuer die naechste Position nach Drehung.
		 * 
		 * @return int[]
		 */
		public int[] getRightOffsets()
		{
			return new int[]
			{
					this.offsetXRight, this.offsetYRight
			};
		}

		/**
		 * Dreht die Richtung nach links.
		 * 
		 * @return {@link Direction}
		 */
		public Direction turnLeft()
		{
			int index = DIRECTIONS.indexOf(this);

			index = index == 0 ? DIRECTIONS.size() - 1 : --index;

			return DIRECTIONS.get(index);
		}

		/**
		 * Dreht die Richtung nach rechts.
		 * 
		 * @return {@link Direction}
		 */
		public Direction turnRight()
		{
			int index = DIRECTIONS.indexOf(this);
			index = index == (DIRECTIONS.size() - 1) ? 0 : ++index;

			return DIRECTIONS.get(index);
		}
	}

	/**
	 *
	 */
	private Direction direction = Direction.NORTH;

	/**
	 * Erstellt ein neues {@link AntCell} Object.
	 */
	public AntCell()
	{
		super();

		setColor(Color.RED);
	}

	/**
	 * <ol>
	 * <li>Ist das Feld weiss, so faerbt sie es schwarz und dreht sich um 90 Grad nach rechts.
	 * <li>Ist das Feld schwarz, so faerbt sie es weiss und dreht sich um 90 Grad nach links.
	 * </ol>
	 * 
	 * @see de.freese.simulationen.model.Cell#nextGeneration(java.lang.Object[])
	 */
	@Override
	public void nextGeneration(final Object...params)
	{
		Cell cell = getWorld().getCell(getX(), getY());
		int[] offsets = null;

		if ((cell == null) || cell.getColor().equals(Color.WHITE))
		{
			EmptyCell<AntWorld> emptyCell = new EmptyCell<>(Color.BLACK);
			emptyCell.setWorld(getWorld());
			emptyCell.moveTo(getX(), getY());

			offsets = this.direction.getRightOffsets();
			this.direction = this.direction.turnRight();
		}
		else
		{
			EmptyCell<AntWorld> emptyCell = new EmptyCell<>(Color.WHITE);
			emptyCell.setWorld(getWorld());
			emptyCell.moveTo(getX(), getY());

			offsets = this.direction.getLeftOffsets();
			this.direction = this.direction.turnLeft();
		}

		int x = getWorld().getXTorusKoord(getX(), offsets[0]);
		int y = getWorld().getYTorusKoord(getY(), offsets[1]);

		setXY(x, y);
	}

	/**
	 * Setzt die Ausrichtung.
	 * 
	 * @param orientation SwingConstants.NORTH, EAST, SOUTH, WEST
	 */
	public void setOrientation(final int orientation)
	{
		switch (orientation)
		{
			case SwingConstants.NORTH:
				this.direction = Direction.NORTH;
				break;
			case SwingConstants.EAST:
				this.direction = Direction.EAST;
				break;
			case SwingConstants.SOUTH:
				this.direction = Direction.SOUTH;
				break;
			case SwingConstants.WEST:
				this.direction = Direction.WEST;
				break;

			default:
				break;
		}
	}
}
