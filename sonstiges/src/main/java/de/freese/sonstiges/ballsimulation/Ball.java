package de.freese.sonstiges.ballsimulation;

import java.awt.Color;
import java.awt.Graphics;

/**
 * @author Thomas Freese
 */
public class Ball
{
	/**
     * 
     */
	private int derDurchmesser = 5;

	/**
     * 
     */
	private final Color dieFarbe;

	/**
     * 
     */
	private final Color dieHintergrundfarbe;

	/**
	 * in Pixeln, zur Umrechnung der y-Position
	 */
	private final int untererBildrand;

	/**
	 * aktueller Mittelpunkt
	 */
	private double x = 0;

	/**
	 * aktueller Mittelpunkt
	 */
	private double y = 0;

	/**
	 * Erstellt ein neues {@link Ball} Object.
	 * 
	 * @param x0 double
	 * @param y0 double
	 * @param offset int
	 * @param fg {@link Color}
	 * @param bg {@link Color}
	 */
	public Ball(final double x0, final double y0, final int offset, final Color fg, final Color bg)
	{
		super();

		this.x = x0;
		this.y = y0;
		this.untererBildrand = offset;
		this.dieFarbe = fg;
		this.dieHintergrundfarbe = bg;
	}

	/**
	 * Erstellt ein neues {@link Ball} Object.
	 * 
	 * @param x0 double
	 * @param y0 double
	 * @param offset int
	 * @param fg {@link Color}
	 * @param bg {@link Color}
	 * @param D int
	 */
	public Ball(final double x0, final double y0, final int offset, final Color fg, final Color bg,
			final int D)
	{
		this(x0, y0, offset, fg, bg);

		this.derDurchmesser = D;
	}

	/**
	 * @param g {@link Graphics}
	 * @param dx double
	 * @param dy double
	 */
	public void move(final Graphics g, final double dx, final double dy)
	{
		paint(g, this.dieHintergrundfarbe); // an alter Stelle loeschen
		this.x += dx;
		this.y += dy;

		if (this.y <= (this.derDurchmesser / 2))
		{
			this.y = this.derDurchmesser / 2;
		}

		paint(g, this.dieFarbe); // an neuer Stelle zeichnen
	}

	/**
	 * @param g {@link Graphics}
	 */
	public void paint(final Graphics g)
	{
		paint(g, this.dieFarbe);
	}

	/**
	 * @param g {@link Graphics}
	 * @param c {@link Color}
	 */
	public void paint(final Graphics g, final Color c)
	{
		g.setColor(c);

		// Mittelpunkt in Position oben links umrechnen
		int xm = (int) this.x - (this.derDurchmesser / 2);
		int ym = this.untererBildrand - (int) this.y - (this.derDurchmesser / 2);

		g.fillOval(xm, ym, this.derDurchmesser, this.derDurchmesser);
	}
}
