// Erzeugt: 01.09.2015
package de.freese.ga.chromonome;

import java.util.Objects;
import de.freese.ga.algoritm.Algorithm;
import de.freese.ga.gene.Gene;

/**
 * Basis-Implementierung eines {@link Chromosome}.<br>
 * Manages an individuals (Chromosome/Solution).
 *
 * @author Thomas Freese
 * @param <G> Konkreter Typ des Genoms.
 */
public abstract class AbstractChromosome<G extends Gene<?>> implements Chromosome<G>
{
    /**
     *
     */
    private final Algorithm<G> algorithm;

    /**
     *
     */
    private G[] genes = null;

    /**
     * @param algorithm {@link Algorithm}
     */
    @SuppressWarnings("unchecked")
    public AbstractChromosome(final Algorithm<G> algorithm)
    {
        super();

        this.algorithm = Objects.requireNonNull(algorithm, "algorithm required");
        this.genes = (G[]) new Gene[algorithm.getSizeChromosome()];
    }

    /**
     * @see de.freese.ga.chromonome.Chromosome#calcFitnessValue()
     */
    @Override
    public double calcFitnessValue()
    {
        return getAlgorithm().calcFitnessValue(this);
    }

    /**
     * @see de.freese.ga.chromonome.Chromosome#contains(de.freese.ga.gene.Gene)
     */
    @Override
    public boolean contains(final G gene)
    {
        boolean contains = false;

        for (G g : getGenes())
        {
            if (g == null)
            {
                continue;
            }

            if (g.equals(gene))
            {
                contains = true;
                break;
            }
        }

        return contains;
    }

    /**
     * @return {@link Algorithm}<G>
     */
    protected Algorithm<G> getAlgorithm()
    {
        return this.algorithm;
    }

    /**
     * @see de.freese.ga.chromonome.Chromosome#getGene(int)
     */
    @Override
    public G getGene(final int index)
    {
        return this.genes[index];
    }

    /**
     * @see de.freese.ga.chromonome.Chromosome#getGenes()
     */
    @Override
    public G[] getGenes()
    {
        return this.genes;
    }

    /**
     * @see de.freese.ga.chromonome.Chromosome#populateChromosome()
     */
    @Override
    public void populateChromosome()
    {
        getAlgorithm().populateChromosome(this);
    }

    /**
     * @see de.freese.ga.chromonome.Chromosome#setGene(int, de.freese.ga.gene.Gene)
     */
    @Override
    public void setGene(final int index, final G gene)
    {
        this.genes[index] = gene;
    }

    /**
     * @see de.freese.ga.chromonome.Chromosome#setGenes(de.freese.ga.gene.Gene[])
     */
    @Override
    public void setGenes(final G[] genes)
    {
        this.genes = genes;
    }

    /**
     * @see de.freese.ga.chromonome.Chromosome#size()
     */
    @Override
    public int size()
    {
        return this.genes.length;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getAlgorithm().toString(this);
    }
}
