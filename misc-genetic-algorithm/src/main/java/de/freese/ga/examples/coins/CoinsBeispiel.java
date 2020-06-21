// Erzeugt: 26.08.2015
package de.freese.ga.examples.coins;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import de.freese.ga.chromonome.Chromosome;
import de.freese.ga.gene.Gene;
import de.freese.ga.genotype.Genotype;

/**
 * @author Thomas Freese
 */
public class CoinsBeispiel
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        List<Integer> existingCoins = new ArrayList<>();
        // existingCoins.add(50);
        existingCoins.add(50);
        existingCoins.add(20);
        existingCoins.add(20);
        existingCoins.add(10);
        existingCoins.add(10);
        existingCoins.add(5);
        existingCoins.add(5);
        existingCoins.add(2);
        existingCoins.add(2);
        existingCoins.add(1);
        existingCoins.add(1);

        CoinsAlgorithm algorithm = new CoinsAlgorithm();
        // algorithm.setElitism(false);
        algorithm.setSizeGenotype(50); // Anzahl Chromosomen/Lösungen
        algorithm.setExistingCoins(existingCoins);
        algorithm.setTargetCents(63); // max. 99 Cent

        // Create an initial population
        Genotype<Gene<Integer>> population = algorithm.createInitialGenotype();
        Chromosome<Gene<Integer>> fittest = population.getFittest();

        // for (int i = 0; i < algorithm.getSizeGenotype(); i++)
        for (int i = 0; fittest.calcFitnessValue() < algorithm.getMaxFitness(); i++)
        {
            // %8.3f = 8 Stellen, 3 davon nach dem Komma.
            System.out.printf("Generation: %2d; Fittest: %8.3f; %s = %d Cent%n", i, fittest.calcFitnessValue(), fittest,
                    Stream.of(fittest.getGenes()).mapToInt(Gene::getValue).sum());
            population = algorithm.evolvePopulation(population);

            fittest = population.getFittest();
        }

        System.out.println("Solution found!");
        System.out.printf("Genes: %s = %d Cent%n", fittest, algorithm.getTargetCents());
    }
}
