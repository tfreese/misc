// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.model;

import java.awt.Color;

/**
 * Basisklasse einer Zelle.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter Typ der Welt
 */
public abstract class AbstractCell<T extends AbstractWorld> implements Cell
{
	/**
	 *
	 */
	private Color color = Color.MAGENTA;

	/**
	 *
	 */
	private T world = null;

	/**
	 *
	 */
	private int x = -1;

	/**
	 *
	 */
	private int y = -1;

	/**
	 * Erstellt ein neues {@link AbstractCell} Object.
	 */
	public AbstractCell()
	{
		super();
	}

	/**
	 * @see de.freese.simulationen.model.Cell#getColor()
	 */
	@Override
	public Color getColor()
	{
		return this.color;
	}

	/**
	 * @return {@link AbstractWorld}
	 */
	protected T getWorld()
	{
		return this.world;
	}

	/**
	 * @see de.freese.simulationen.model.Cell#getX()
	 */
	@Override
	public int getX()
	{
		return this.x;
	}

	/**
	 * @see de.freese.simulationen.model.Cell#getY()
	 */
	@Override
	public int getY()
	{
		return this.y;
	}

	/**
	 * @see de.freese.simulationen.model.Cell#moveTo(int, int)
	 */
	@Override
	public void moveTo(final int x, final int y)
	{
		// Alte Position auf null setzen
		if ((getX() >= 0) && (getY() >= 0))
		{
			getWorld().setCell(null, getX(), getY());
		}

		setXY(x, y);
		getWorld().setCell(this, x, y);
	}

	/**
	 * @param color {@link Color}
	 */
	protected void setColor(final Color color)
	{
		this.color = color;
	}

	/**
	 * @param world {@link AbstractWorld}
	 */
	public void setWorld(final T world)
	{
		this.world = world;
	}

	/**
	 * @see de.freese.simulationen.model.Cell#setXY(int, int)
	 */
	@Override
	public void setXY(final int x, final int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(" ");
		sb.append("[x=").append(getX()).append(",y=").append(getY()).append("]");

		return sb.toString();
	}
}
