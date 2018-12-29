// Erzeugt: 26.08.2015
package de.freese.ga.examples.sudoku;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import de.freese.ga.chromonome.Chromosome;
import de.freese.ga.genotype.Genotype;

/**
 * @author Thomas Freese
 */
public class SudokuBeispiel
{
    /**
     * @param args String[]
     * @throws IOException Falls was schief geht.
     */
    public static void main(final String[] args) throws IOException
    {
        SudokuAlgorithm algorithm = new SudokuAlgorithm();
        List<String[]> puzzle = null;

        try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("sudoku_easy_1.txt"))
        {
            puzzle = algorithm.parsePuzzle(inputStream);
        }

        // algorithm.setElitism(true);
        // algorithm.setMutationRate(0.05D);
        // algorithm.setCrossoverRate(0.75D);
        algorithm.setTournamentSize(9);
        algorithm.setSizeGenotype(100); // Anzahl Chromosomen/Lösungen
        // algorithm.setSizeChromosome(...); // Anzahl Zahlen im Rätzel
        algorithm.setPuzzle(puzzle);

        double maxFitness = algorithm.getMaxFitness();

        // Create an initial population
        Genotype<SudokuGene> population = algorithm.createInitialGenotype();
        Chromosome<SudokuGene> fittest = population.getFittest();

        // for (int i = 0; fittest.calcFitnessValue() < algorithm.getMaxFitness(); i++)
        for (int i = 0; i < algorithm.getSizeGenotype(); i++)
        {
            fittest = population.getFittest();

            System.out.printf("Generation: %2d; Fittest: %3.0f / %3.0f; %s%n", i, fittest.calcFitnessValue(), maxFitness, fittest);
            population = algorithm.evolvePopulation(population);

            // if (i == algorithm.getSizeGenotype() - 1)
            // {
            // // Neustart
            // i = 0;
            // population = algorithm.createInitialGenotype();
            // fittest = population.getFittest();
            // }
        }

        // 1215 = 3 * 405: In allen Zeilen, Spalten und Blöcken ist die Summe 45.
        System.out.println(maxFitness != fittest.calcFitnessValue() ? "Wrong Solution !!!" : "Solution found!");
        System.out.printf("Genes: Fittest: %3.0f / %3.0f%s%n", fittest.calcFitnessValue(), maxFitness, fittest);
    }
}
