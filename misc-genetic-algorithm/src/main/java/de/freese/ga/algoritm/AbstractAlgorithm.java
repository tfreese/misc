// Erzeugt: 01.09.2015
package de.freese.ga.algoritm;

import java.security.SecureRandom;
import java.util.Random;
import de.freese.ga.gene.Gene;

/**
 * Basisklasse eines {@link Algorithm}.
 *
 * @author Thomas Freese
 * @param <G> Konkreter Typ des Chromosoms.
 */
public abstract class AbstractAlgorithm<G extends Gene<?>> implements Algorithm<G>
{
    /**
     * 50 %
     */
    private double crossoverRate = 0.5D;

    /**
     *
     */
    private boolean elitism = true;

    /**
     * 1,5%
     */
    private double mutationRate = 0.015D;

    /**
     *
     */
    private final Random random;

    /**
     *
     */
    private int sizeChromosome = 0;

    /**
     *
     */
    private int sizeGenotype = 0;

    /**
     *
     */
    private int tournamentSize = 5;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractAlgorithm}.
     *
     * @param sizeGenotype int
     * @param sizeChromosome int
     */
    public AbstractAlgorithm(final int sizeGenotype, final int sizeChromosome)
    {
        super();

        this.sizeGenotype = sizeGenotype;
        this.sizeChromosome = sizeChromosome;
        // this.random = new Random();
        this.random = new SecureRandom();
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#getCrossoverRate()
     */
    @Override
    public double getCrossoverRate()
    {
        return this.crossoverRate;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#getMutationRate()
     */
    @Override
    public double getMutationRate()
    {
        return this.mutationRate;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#getRandom()
     */
    @Override
    public Random getRandom()
    {
        return this.random;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#getSizeChromosome()
     */
    @Override
    public int getSizeChromosome()
    {
        return this.sizeChromosome;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#getSizeGenotype()
     */
    @Override
    public int getSizeGenotype()
    {
        return this.sizeGenotype;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#getTournamentSize()
     */
    @Override
    public int getTournamentSize()
    {
        return this.tournamentSize;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#isElitism()
     */
    @Override
    public boolean isElitism()
    {
        return this.elitism;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#setCrossoverRate(double)
     */
    @Override
    public void setCrossoverRate(final double rate)
    {
        this.crossoverRate = rate;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#setElitism(boolean)
     */
    @Override
    public void setElitism(final boolean elitism)
    {
        this.elitism = elitism;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#setMutationRate(double)
     */
    @Override
    public void setMutationRate(final double rate)
    {
        this.mutationRate = rate;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#setSizeChromosome(int)
     */
    @Override
    public void setSizeChromosome(final int size)
    {
        this.sizeChromosome = size;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#setSizeGenotype(int)
     */
    @Override
    public void setSizeGenotype(final int size)
    {
        this.sizeGenotype = size;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#setTournamentSize(int)
     */
    @Override
    public void setTournamentSize(final int size)
    {
        this.tournamentSize = size;
    }
}
