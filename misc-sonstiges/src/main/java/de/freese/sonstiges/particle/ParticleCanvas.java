/*
 * Created on 29.06.2003 To change the template for this generated file go to Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.freese.sonstiges.particle;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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
    private final ExecutorService executorService;

    /**
     *
     */
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     *
     */
    private List<Particle> particles = Collections.emptyList();

    /**
    *
    */
    protected final Random random = new Random();

    /**
     *
     */
    private boolean stop;

    /**
     * Creates a new {@link ParticleCanvas} object.
     */
    public ParticleCanvas()
    {
        this(800);
    }

    /**
     * Creates a new {@link ParticleCanvas} object.
     *
     * @param size int
     */
    public ParticleCanvas(final int size)
    {
        setSize(new Dimension(size, size));

        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * @param particle {@link Particle}
     * @return {@link Thread}
     */
    private Runnable makeRunnable(final Particle particle)
    {
        Runnable runnable = () -> {
            try
            {
                while (!this.stop)
                {
                    particle.move();
                    this.repaint();
                    Thread.sleep(this.random.nextInt(100));
                }
            }
            catch (InterruptedException ex)
            {
                return;
            }
            finally
            {
                removeParticle(particle);
            }
        };

        return runnable;
    }

    /**
     * @see java.awt.Canvas#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        this.particles.forEach(p -> p.draw(g));
    }

    /**
     * @param particle {@link Particle}
     */
    private void removeParticle(final Particle particle)
    {
        this.lock.lock();

        try
        {
            System.out.println("ParticleCanvas.removeParticle()");
            this.particles.remove(particle);
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * @param particles {@link List}
     */
    void setParticles(final List<Particle> particles)
    {
        this.particles = Objects.requireNonNull(particles, "particles required");
    }

    /**
     *
     */
    public synchronized void shutdown()
    {
        System.out.println("ParticleCanvas.shutdown() ...");
        this.stop = true;

        this.executorService.shutdown();

        try
        {
            // Wait a while for existing tasks to terminate.
            if (!this.executorService.awaitTermination(10, TimeUnit.SECONDS))
            {
                this.executorService.shutdownNow(); // Cancel currently executing tasks

                // Wait a while for tasks to respond to being cancelled
                if (!this.executorService.awaitTermination(5, TimeUnit.SECONDS))
                {
                    System.err.println("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException iex)
        {
            // (Re-)Cancel if current thread also interrupted
            this.executorService.shutdownNow();

            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }

        System.out.println("ParticleCanvas.shutdown() ... finished");
    }

    /**
     * @param numOfParticles int
     */
    public synchronized void start(final int numOfParticles)
    {
        stop();

        System.out.println("ParticleCanvas.start()");

        this.particles = new ArrayList<>(numOfParticles);

        for (int i = 0; i < numOfParticles; ++i)
        {
            this.particles.add(new Particle(this.random, 400, 300));
        }

        this.particles.forEach(p -> this.executorService.execute(makeRunnable(p)));
    }

    /**
     *
     */
    public synchronized void stop()
    {
        System.out.println("ParticleCanvas.stop()");

        this.stop = true;

        while (!this.particles.isEmpty())
        {
            try
            {
                Thread.sleep(100);
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }

        this.stop = false;
    }
}
