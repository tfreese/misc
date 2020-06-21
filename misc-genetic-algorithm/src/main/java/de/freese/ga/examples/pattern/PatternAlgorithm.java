// Erzeugt: 01.09.2015
package de.freese.ga.examples.pattern;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import de.freese.ga.algoritm.AbstractAlgorithm;
import de.freese.ga.chromonome.Chromosome;
import de.freese.ga.gene.BooleanGene;
import de.freese.ga.gene.Gene;

/**
 * @author Thomas Freese
 */
public class PatternAlgorithm extends AbstractAlgorithm<Gene<Boolean>>
{
    /**
     *
     */
    private boolean[] solution = null;

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
    public double calcFitnessValue(final Chromosome<Gene<Boolean>> chromosome)
    {
        double fitness = 0.0D;

        for (int i = 0; (i < chromosome.size()) && (i < this.solution.length); i++)
        {
            if (chromosome.getGene(i).getValue().equals(this.solution[i]))
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

    /**
     * @see de.freese.ga.algoritm.Algorithm#populateChromosome(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public void populateChromosome(final Chromosome<Gene<Boolean>> chromosome)
    {
        for (int i = 0; i < getSizeChromosome(); i++)
        {
            chromosome.setGene(i, new BooleanGene(getRandom().nextBoolean()));
        }
    }

    /**
     * Setzt die Lösung.
     *
     * @param pattern String
     */
    public void setSolution(final String pattern)
    {
        this.solution = new boolean[pattern.length()];

        for (int i = 0; i < pattern.length(); i++)
        {
            char character = pattern.charAt(i);

            if (character == '1')
            {
                this.solution[i] = true;
            }
            else
            {
                this.solution[i] = false;
            }
        }
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#toString(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public String toString(final Chromosome<Gene<Boolean>> chromosome)
    {
        String s = null;

        // @formatter:off
        s = Stream.of(chromosome.getGenes())
                 .map(Gene::getValue)
                 .map(v -> Boolean.TRUE.equals(v) ? '1': '0')
                 .map(Object::toString)
                 //.map(Optional::ofNullable)
                 //.map(o -> o.map(Object::toString).orElse("null"))
                 .collect(Collectors.joining());
        // @formatter:on

        return s;
    }
}
