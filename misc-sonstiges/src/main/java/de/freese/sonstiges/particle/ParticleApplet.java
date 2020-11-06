package de.freese.sonstiges.particle;

import java.applet.Applet;

/**
 * @author Thomas Freese
 */
@SuppressWarnings("deprecation")
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
     * Erstellt ein neues {@link ParticleApplet} Object.
     */
    public ParticleApplet()
    {
        super();
    }

    /**
     * @see java.applet.Applet#destroy()
     */
    @SuppressWarnings("javadoc")
    @Override
    public void destroy()
    {
        this.canvas.shutdown();
    }

    /**
     * @see java.applet.Applet#init()
     */
    @SuppressWarnings("javadoc")
    @Override
    public void init()
    {
        add(this.canvas);
    }

    /**
     * @see java.applet.Applet#start()
     */
    @SuppressWarnings("javadoc")
    @Override
    public synchronized void start()
    {
        this.canvas.start(10);
    }

    /**
     * @see java.applet.Applet#stop()
     */
    @SuppressWarnings("javadoc")
    @Override
    public synchronized void stop()
    {
        this.canvas.stop();
    }
}
