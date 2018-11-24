// Erzeugt: 01.09.2015
package de.freese.ga.examples.pattern;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.freese.ga.algoritm.AbstractAlgorithm;
import de.freese.ga.chromonome.Chromosome;
import de.freese.ga.gene.ByteGene;
import de.freese.ga.gene.Gene;

/**
 * @author Thomas Freese
 */
public class PatternAlgorithm extends AbstractAlgorithm<Gene<Byte>>
{
    /**
     *
     */
    private Byte[] solution = null;

    /**
     * Erzeugt eine neue Instanz von {@link PatternAlgorithm}.
     */
    public PatternAlgorithm()
    {
        super(0, 0);
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#calcFitnessValue(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public double calcFitnessValue(final Chromosome<Gene<Byte>> chromosome)
    {
        double fitness = 0.0D;

        for (int i = 0; (i < chromosome.size()) && (i < this.solution.length); i++)
        {
            if (chromosome.getGene(i).getValue() == this.solution[i])
            {
                fitness++;
            }
        }

        return fitness;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#getMaxFitness()
     */
    @Override
    public double getMaxFitness()
    {
        // Max. Wert, wenn alle Gene richtig sind.
        return getSizeChromosome();
    }

    // /**
    // * @see de.freese.ga.algoritm.Algorithm#mutate(de.freese.ga.chromonome.Chromosome)
    // */
    // @Override
    // public void mutate(final Chromosome<Gene<Byte>> chromosome)
    // {
    // for (int i = 0; i < chromosome.size(); i++)
    // {
    // if (Math.random() <= getMutationRate())
    // {
    // chromosome.setGene(i, new ByteGene((byte) Math.round(Math.random())));
    // }
    // }
    // }

    /**
     * @see de.freese.ga.algoritm.Algorithm#pupulateChromosome(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public void pupulateChromosome(final Chromosome<Gene<Byte>> chromosome)
    {
        for (int i = 0; i < getSizeChromosome(); i++)
        {
            chromosome.setGene(i, new ByteGene((byte) Math.round(Math.random())));
        }
    }

    /**
     * Setzt die Lösung.
     *
     * @param pattern String
     */
    public void setSolution(final String pattern)
    {
        this.solution = new Byte[pattern.length()];

        for (int i = 0; i < pattern.length(); i++)
        {
            String character = pattern.substring(i, i + 1);

            if (character.contains("0") || character.contains("1"))
            {
                this.solution[i] = Byte.parseByte(character);
            }
            else
            {
                this.solution[i] = 0;
            }
        }
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#toString(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public String toString(final Chromosome<Gene<Byte>> chromosome)
    {
        String s = null;

        // @formatter:off
        s = Stream.of(chromosome.getGenes())
                 .map(Gene::getValue)
                 .map(Object::toString)
                 //.map(Optional::ofNullable)
                 //.map(o -> o.map(Object::toString).orElse("null"))
                 .collect(Collectors.joining());
        // @formatter:on

        return s;
    }
}
