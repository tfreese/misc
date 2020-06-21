// Erzeugt: 02.09.2015
package de.freese.ga.genotype;

import java.util.Objects;
import de.freese.ga.algoritm.Algorithm;
import de.freese.ga.chromonome.Chromosome;
import de.freese.ga.gene.Gene;

/**
 * Basis-Implementierung eines {@link Genotype}.
 *
 * @author Thomas Freese
 * @param <G> Typ des Genoms
 */
public abstract class AbstractGenotype<G extends Gene<?>> implements Genotype<G>
{
    /**
     *
     */
    private final Algorithm<G> algorithm;

    /**
     *
     */
    private final Chromosome<G>[] chromosomes;

    /**
     * Erstellt ein neues {@link AbstractGenotype} Object.
     *
     * @param algorithm {@link Algorithm}
     */
    public AbstractGenotype(final Algorithm<G> algorithm)
    {
        this(algorithm, algorithm.getSizeGenotype());
    }

    /**
     * Erstellt ein neues {@link AbstractGenotype} Object.
     *
     * @param algorithm {@link Algorithm}
     * @param size int
     */
    @SuppressWarnings("unchecked")
    public AbstractGenotype(final Algorithm<G> algorithm, final int size)
    {
        super();

        this.algorithm = Objects.requireNonNull(algorithm, "algorithm required");
        this.chromosomes = new Chromosome[size];
    }

    /**
     * @see de.freese.ga.genotype.Genotype#getAlgorithm()
     */
    @Override
    public Algorithm<G> getAlgorithm()
    {
        return this.algorithm;
    }

    /**
     * @see de.freese.ga.genotype.Genotype#getChromosome(int)
     */
    @Override
    public Chromosome<G> getChromosome(final int index)
    {
        return this.chromosomes[index];
    }

    /**
     * @see de.freese.ga.genotype.Genotype#setChromosome(int, de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public void setChromosome(final int index, final Chromosome<G> chromosome)
    {
        this.chromosomes[index] = chromosome;
    }

    /**
     * @see de.freese.ga.genotype.Genotype#size()
     */
    @Override
    public int size()
    {
        return this.chromosomes.length;
    }
}
