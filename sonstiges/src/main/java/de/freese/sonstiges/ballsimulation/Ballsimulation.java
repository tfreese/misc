package de.freese.sonstiges.ballsimulation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * @author Thomas Freese
 */
public class Ballsimulation extends JFrame
{
	/**
	 * @author Thomas Freese
	 */
	class myWindowListener extends WindowAdapter
	{
		/**
		 * Erstellt ein neues {@link myWindowListener} Object.
		 */
		public myWindowListener()
		{
			super();
		}

		/**
		 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosing(final WindowEvent event)
		{
			setVisible(false);
			dispose();
			System.exit(0);
		}
	}

	/**
	 * Use serialVersionUID for interoperability.
	 */
	private static final long serialVersionUID = 1726545935341310407L;

	/**
	 *
	 */
	private static final double GRAV = 9.80665;

	/**
	 * 
	 */
	private static final int XR = 10;

	/**
	 * 
	 */
	private static final int XL = 0;

	/**
	 * 
	 */
	private static final int YU = 5;

	/**
	 * 
	 */
	private static final int YO = 0;

	/**
	 * 
	 */
	private double dt;

	/**
	 * 
	 */
	private double durchmesser;

	/**
	 * 
	 */
	private double dx;

	/**
	 * 
	 */
	private double dy;

	/**
	 *
	 */
	private double daempfung;

	/**
	 * 
	 */
	private Ball einBall;

	/**
	 * 
	 */
	private int pause;

	/**
	 * 
	 */
	private double time;

	/**
	 * 
	 */
	private int timestep;

	/**
	 * 
	 */
	private boolean touchx = false;

	/**
	 *
	 */
	private boolean touchy = false;

	/**
	 * 
	 */
	private double vx;

	/**
	 * 
	 */
	private double vy;

	/**
	 * 
	 */
	private double vybreak;

	/**
	 * 
	 */
	private double x;

	/**
	 * 
	 */
	private double y;

	/**
	 * Erstellt ein neues {@link Ballsimulation} Object.
	 * 
	 * @param startx double
	 * @param starty double
	 * @param startvx double
	 * @param startvy double
	 * @param durchm double
	 * @param d double
	 * @param p double
	 */
	public Ballsimulation(final double startx, final double starty, final double startvx,
			final double startvy, final double durchm, final double d, final int p)
	{
		super("Ballsimulation");

		setSize(410, 240);
		setLocation(300, 300);
		setVisible(true);
		setResizable(false);
		addWindowListener(new myWindowListener());

		this.x = startx;
		this.y = starty;
		this.vx = startvx;
		this.vy = startvy;
		this.durchmesser = durchm;
		this.daempfung = d;
		this.pause = p;

		this.vybreak = 0.05;
		this.dx = this.dy = 0;
		this.timestep = this.pause;
		this.dt = ((double) this.timestep) / 1000;

		this.einBall =
				new Ball(this.x * 40, this.y * 40, YU * 40, Color.red, Color.blue,
						(int) (this.durchmesser * 40)); // 200=unterer Bildrand
	}

	/**
	 * @return double
	 */
	private double flytimeXL()
	{
		return ((XL - this.x) + (this.durchmesser / 2)) / this.vx;
	}

	/**
	 * @return double
	 */
	private double flytimeXR()
	{
		return (XR - this.x - (this.durchmesser / 2)) / this.vx;
	}

	/**
	 * @return double
	 */
	private double flytimeYO()
	{
		return java.lang.Math.abs((this.vy - java.lang.Math.sqrt((this.vy * this.vy)
				- (2 * GRAV * (YU - this.y - (this.durchmesser / 2)))))
				/ GRAV);
	}

	/**
	 * @return double
	 */
	private double flytimeYU()
	{
		return java.lang.Math.abs((this.vy + java.lang.Math.sqrt((this.vy * this.vy)
				- (2 * GRAV * ((YO - this.y) + (this.durchmesser / 2)))))
				/ GRAV);
	}

	/**
	 * @param g {@link Graphics}
	 */
	private void gitter(final Graphics g)
	{
		g.setColor(Color.black);

		for (int i = 40; i <= 360; i += 40)
		{
			g.drawLine(i, 0, i, 200);
		}

		for (int i = 40; i <= 160; i += 40)
		{
			g.drawLine(0, i, 400, i);
		}
	}

	/**
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(final Graphics g)
	{
		g.translate(5, 20);

		this.einBall.paint(g);

		while (!((this.vy > -this.vybreak) && (this.vy < this.vybreak) && (this.y == (this.durchmesser / 2))))
		{
			if (this.vy < 0)
			{
				this.time = flytimeYU();

				if (this.time < this.dt)
				{
					this.timestep = (int) (this.time * 1000);
					this.dt = this.time;
					this.touchy = true;
				}
			}

			if (this.vy > 0)
			{
				this.time = flytimeYO();

				if (this.time < this.dt)
				{
					this.timestep = (int) (this.time * 1000);
					this.dt = this.time;
					this.touchy = true;
				}
			}

			if (this.vx > 0)
			{
				this.time = flytimeXR();

				if (this.time < this.dt)
				{
					this.timestep = (int) (this.time * 1000);
					this.dt = this.time;
					this.touchx = true;
				}
			}

			if (this.vx < 0)
			{
				this.time = flytimeXL();

				if (this.time < this.dt)
				{
					this.timestep = (int) (this.time * 1000);
					this.dt = this.time;
					this.touchx = true;
				}
			}

			this.dx = this.vx * this.dt;
			this.vy -= (GRAV * this.dt);
			this.dy = (this.vy * this.dt) - ((GRAV * this.dt * this.dt) / 2);

			this.x += this.dx;
			this.y += this.dy;

			if (this.y <= (this.durchmesser / 2))
			{
				this.y = this.durchmesser / 2; // keine negativen Y-Koordinaten
			}

			this.einBall.move(g, this.dx * 40, this.dy * 40);
			gitter(g);

			if (this.touchy)
			{
				this.vy *= (-1 + this.daempfung);
				this.timestep = this.pause;
				this.dt = ((double) this.timestep) / 1000;
				this.touchy = false;
			}

			if (this.touchx)
			{
				this.vx *= (-1 + this.daempfung);
				this.timestep = this.pause;
				this.dt = ((double) this.timestep) / 1000;
				this.touchx = false;
			}

			try
			{
				Thread.sleep(this.timestep);
			}
			catch (InterruptedException ex)
			{
				// Ignore
			}
		}
	}
}
