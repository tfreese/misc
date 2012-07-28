package de.freese.sonstiges.particle;

import java.applet.Applet;
import java.awt.HeadlessException;
import java.util.Random;

/**
 * @author Thomas Freese
 */
public class ParticleApplet extends Applet
{
	/**
	 *
	 */
	private static final long serialVersionUID = 686416428039038024L;

	/**
     *
     */
	protected final ParticleCanvas canvas = new ParticleCanvas(800);

	/**
     *
     */
	protected final Random rng = new Random();

	/**
     *
     */
	protected Thread[] threads; // null when not running

	/**
	 * Erstellt ein neues {@link ParticleApplet} Object.
	 * 
	 * @throws HeadlessException Falls was schief geht.
	 */
	public ParticleApplet() throws HeadlessException
	{
		super();
	}

	/**
	 * @see java.applet.Applet#init()
	 */
	@Override
	public void init()
	{
		add(this.canvas);
	}

	/**
	 * @see java.applet.Applet#start()
	 */
	@Override
	public synchronized void start()
	{
		int n = 50; // just for demo

		if (this.threads == null)
		{ // bypass if already started

			Particle[] particles = new Particle[n];

			for (int i = 0; i < n; ++i)
			{
				particles[i] = new Particle(400, 300);
			}

			this.canvas.setParticles(particles);

			this.threads = new Thread[n];

			for (int i = 0; i < n; ++i)
			{
				this.threads[i] = makeThread(particles[i]);
				this.threads[i].start();
			}
		}
	}

	/**
	 * @see java.applet.Applet#stop()
	 */
	@Override
	public synchronized void stop()
	{
		if (this.threads != null)
		{ // bypass if already stopped

			for (int i = 0; i < this.threads.length; ++i)
			{
				this.threads[i].interrupt();
			}

			this.threads = null;
		}
	}

	/**
	 * @param p {@link Particle}
	 * @return {@link Thread}
	 */
	protected Thread makeThread(final Particle p)
	{ // utility

		Runnable runloop = new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				try
				{
					for (;;)
					{
						p.move();
						ParticleApplet.this.canvas.repaint();
						Thread.sleep(ParticleApplet.this.rng.nextInt(100));
					}
				}
				catch (InterruptedException e)
				{
					return;
				}
			}
		};

		return new Thread(runloop);
	}
}
