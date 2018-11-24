// Erzeugt: 26.08.2015
package de.freese.ga.examples.travelling_salesman;

import java.util.ArrayList;
import java.util.List;

import de.freese.ga.chromonome.Chromosome;
import de.freese.ga.gene.Gene;
import de.freese.ga.genotype.Genotype;

/**
 * http://www.theprojectspot.com/tutorial-post/applying-a-genetic-algorithm-to-the-travelling-salesman-problem/5
 *
 * @author Thomas Freese
 */
public class TspBeispiel
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        List<City> cities = new ArrayList<>(20);
        cities.add(new City("A", 60, 200));
        cities.add(new City("B", 180, 200));
        cities.add(new City("C", 80, 180));
        cities.add(new City("D", 140, 180));
        cities.add(new City("E", 20, 160));
        cities.add(new City("F", 100, 160));
        cities.add(new City("G", 200, 160));
        cities.add(new City("H", 140, 140));
        cities.add(new City("I", 40, 120));
        cities.add(new City("J", 100, 120));
        cities.add(new City("K", 180, 100));
        cities.add(new City("L", 60, 80));
        cities.add(new City("M", 120, 80));
        cities.add(new City("N", 180, 60));
        cities.add(new City("O", 20, 40));
        cities.add(new City("P", 100, 40));
        cities.add(new City("Q", 200, 40));
        cities.add(new City("R", 20, 20));
        cities.add(new City("S", 60, 20));
        cities.add(new City("T", 160, 20));

        TspAlgorithm algorithm = new TspAlgorithm();
        // algorithm.setElitism(false);
        algorithm.setSizeGenotype(75); // Anzahl Chromosomen/Tour
        // algorithm.setSizeChromosome(...); // Anzahl Städte = Anzahl Gene im Chromosom/Tour
        algorithm.setCities(cities);

        // Create an initial population
        Genotype<Gene<City>> population = algorithm.createInitialGenotype();
        Chromosome<Gene<City>> fittest = null;

        // for (int i = 0; fittest.calcFitnessValue() < algorithm.getMaxFitness(); i++)
        for (int i = 0; i < algorithm.getSizeGenotype(); i++)
        {
            fittest = population.getFittest();

            System.out.printf("Generation: %2d; Fittest: %2.9f; Distance: %4.3f; %s%n", i, fittest.calcFitnessValue(),
                    algorithm.getDistance(fittest), fittest);
            population = algorithm.evolvePopulation(population);
        }

        // Print final results: minimum distance found: 871,117
        System.out.println("Solution found!");
        System.out.printf("Genes: %s; Final distance: %4.3f%n", fittest, algorithm.getDistance(fittest));
    }
}
