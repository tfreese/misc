/*
 * Created on 29.06.2003 To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.freese.sonstiges.particle;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * @author Thomas Freese
 */
public class ParticleCanvas extends Canvas
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7875942028557880029L;

	/**
     *
     */
	private Particle[] particles = new Particle[0];

	/**
	 * Creates a new {@link ParticleCanvas} object.
	 * 
	 * @param size int
	 */
	public ParticleCanvas(final int size)
	{
		setSize(new Dimension(size, size));
	}

	/**
	 * @see java.awt.Canvas#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(final Graphics g)
	{ // override Canvas.paint

		Particle[] ps = getParticles();

		for (int i = 0; i < ps.length; ++i)
		{
			ps[i].draw(g);
		}
	}

	/**
	 * @return Particle[]
	 */
	protected synchronized Particle[] getParticles()
	{
		return this.particles;
	}

	/**
	 * Intended to be called by applet.
	 * 
	 * @param ps Particle[]
	 */
	synchronized void setParticles(final Particle[] ps)
	{
		if (ps == null)
		{
			throw new IllegalArgumentException("Cannot set null");
		}

		this.particles = ps;
	}
}
