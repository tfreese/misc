// Erzeugt: 26.08.2015
package de.freese.ga.examples.pattern;

import de.freese.ga.chromonome.Chromosome;
import de.freese.ga.gene.Gene;
import de.freese.ga.genotype.Genotype;

/**
 * http://www.theprojectspot.com/tutorial-post/creating-a-genetic-algorithm-for-beginners/3
 *
 * @author Thomas Freese
 */
public class PatternBeispiel
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        String pattern = "11110000000000100000000001000000000010000000000100000000001111";

        PatternAlgorithm algorithm = new PatternAlgorithm();
        // algorithm.setElitism(false);
        algorithm.setSizeGenotype(50); // Anzahl Chromosomen/Lösungen/Pattern
        algorithm.setSizeChromosome(pattern.length()); // Anzahl Gene im Chromosom/Pattern
        algorithm.setSolution(pattern);

        // Create an initial population
        Genotype<Gene<Byte>> population = algorithm.createInitialGenotype();
        Chromosome<Gene<Byte>> fittest = population.getFittest();

        // for (int i = 0; i < algorithm.getSizeGenotype(); i++)
        for (int i = 0; fittest.calcFitnessValue() < algorithm.getMaxFitness(); i++)
        {
            fittest = population.getFittest();

            System.out.printf("Generation: %2d; Fittest: %2.0f; %s%n", i, fittest.calcFitnessValue(), fittest);
            population = algorithm.evolvePopulation(population);
        }

        System.out.println("Solution found!");
        System.out.printf("Genes: %s%n", fittest);
    }
}
