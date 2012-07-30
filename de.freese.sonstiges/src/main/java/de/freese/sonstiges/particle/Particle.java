package de.freese.sonstiges.particle;

import java.awt.Graphics;
import java.util.Random;

/**
 * @author Thomas Freese
 */
public class Particle
{
	/**
     *
     */
	protected final Random rng = new Random();

	/**
     *
     */
	protected int x;

	/**
     *
     */
	protected int y;

	/**
	 * Erstellt ein neues {@link Particle} Object.
	 * 
	 * @param initialX int
	 * @param initialY int
	 */
	public Particle(final int initialX, final int initialY)
	{
		super();

		this.x = initialX;
		this.y = initialY;
	}

	/**
	 * @param g {@link Graphics}
	 */
	public void draw(final Graphics g)
	{
		int lx;
		int ly;

		synchronized (this)
		{
			lx = this.x;
			ly = this.y;
		}

		g.drawRect(lx, ly, 10, 10);
	}

	/**
	 * 
	 */
	public synchronized void move()
	{
		this.x += (this.rng.nextInt(10) - 5);
		this.y += (this.rng.nextInt(10) - 5);
	}
}
