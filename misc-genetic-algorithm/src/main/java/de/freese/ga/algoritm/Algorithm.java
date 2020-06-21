// Erzeugt: 01.09.2015
package de.freese.ga.algoritm;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import de.freese.ga.chromonome.Chromosome;
import de.freese.ga.chromonome.DefaultChromosome;
import de.freese.ga.gene.Gene;
import de.freese.ga.genotype.DefaultGenotype;
import de.freese.ga.genotype.Genotype;

/**
 * Interface eines Algorithmus für genetische Algorythmen.
 *
 * @author Thomas Freese
 * @param <G> Konkreter Typ des Genoms.
 */
public interface Algorithm<G extends Gene<?>>
{
    /**
     * Berechnet die Fitness-Funktion des Chromosoms.<br>
     * Je näher an {@link #getMaxFitness()}, desto näher am optimalen Ergebnis.<br>
     *
     * @param chromosome {@link Chromosome}
     * @return double
     * @see Chromosome#calcFitnessValue()
     */
    public double calcFitnessValue(Chromosome<G> chromosome);

    /**
     * Erzeugt eine neues leeres Chromosom / Lösung.
     *
     * @return {@link Chromosome}
     */
    public default Chromosome<G> createEmptyChromosome()
    {
        return new DefaultChromosome<>(this);
    }

    /**
     * Erzeugt einen neuen leeren Genotype / Population.
     *
     * @param size int
     * @return {@link Genotype}
     */
    public default Genotype<G> createEmptyGenotype(final int size)
    {
        return new DefaultGenotype<>(this, size);
    }

    /**
     * Erzeugt den initial befüllten Genotype / Population.
     *
     * @return {@link Genotype}
     */
    public default Genotype<G> createInitialGenotype()
    {
        Genotype<G> population = createEmptyGenotype(getSizeGenotype());

        population.pupulateGenotype();

        return population;
    }

    /**
     * Zufällige Rekombination ausgewählter Individuen (Chromosomen).<br>
     * Beim Crossover werden aus der Population/Genotype paarweise Chromosomen ausgewählt, die dann mit einer Wahrscheinlichkeit W zu kreuzen sind.
     *
     * @param parent1 {@link Chromosome}
     * @param parent2 {@link Chromosome}
     * @return {@link Chromosome}
     */
    public default Chromosome<G> crossover(final Chromosome<G> parent1, final Chromosome<G> parent2)
    {
        Chromosome<G> population = createEmptyChromosome();

        for (int i = 0; i < parent1.size(); i++)
        {
            G gene = null;

            if (getRandom().nextDouble() <= getCrossoverRate())
            {
                gene = parent1.getGene(i);
            }
            else
            {
                gene = parent2.getGene(i);
            }

            population.setGene(i, gene);
        }

        // @formatter:off
//        IntStream.range(0, parent1.size())
//            .parallel()
//            .forEach(i -> {
//                G gene = null;
//
//                if (getRandom().nextDouble() <= getCrossoverRate())
//                {
//                    gene = parent1.getGene(i);
//                }
//                else
//                {
//                    gene = parent2.getGene(i);
//                }
//
//                population.setGene(i, gene);
//            });
        // @formatter:on

        return population;
    }

    /**
     * 1. Zufällige Auswahl eines Individuums (Chromosom) für die Rekombination, {@link #tournamentSelection(Genotype)}<br>
     * 2. Zufällige Rekombination ausgewählter Individuen (Chromosomen), {@link #crossover(Chromosome, Chromosome)}<br>
     * 3. Zufällige Veränderung der Gene, {@link #mutate(Chromosome)}<br>
     *
     * @param genotype {@link Genotype}
     * @return {@link Genotype}
     */
    public default Genotype<G> evolvePopulation(final Genotype<G> genotype)
    {
        Genotype<G> newPopulation = createEmptyGenotype(genotype.size());

        int elitismOffset = 0;

        if (isElitism())
        {
            newPopulation.setChromosome(0, genotype.getFittest());
            elitismOffset = 1;
        }

        // @formatter:off
        IntStream.range(elitismOffset, genotype.size())
            .parallel()
            .map(i -> {
                // Loop over the population size and create new individuals with crossover
                // Select parents
                Chromosome<G> parent1 = tournamentSelection(genotype);
                Chromosome<G> parent2 = tournamentSelection(genotype);

                // Kann bei einigen Beispielen zur Endlos-Schleife führen.
//                while(parent1.calcFitnessValue() == parent2.calcFitnessValue())
//                {
//                    parent2 = tournamentSelection(genotype);
//                }

                // Crossover parents
                Chromosome<G> child = crossover(parent1, parent2);

                // Add child to new population
                newPopulation.setChromosome(i, child);

                return i;
            })
            .forEach(i -> {
                // Mutate population
                mutate(newPopulation.getChromosome(i));
                }
            )
            ;
        // @formatter:on

        return newPopulation;
    }

    /**
     * Liefert die Rate der Vererbung eines Chromosoms.
     *
     * @return double
     */
    public double getCrossoverRate();

    /**
     * Liefert, wenn möglich, den max. Wert der Fitnessfunktion.
     *
     * @return double
     */
    public double getMaxFitness();

    /**
     * Liefert die Rate der Mutation eines Chromosoms.
     *
     * @return double
     */
    public double getMutationRate();

    /**
     * @return {@link Random}
     */
    public Random getRandom();

    /**
     * Liefert die Größe des Chromosoms.
     *
     * @return int
     */
    public int getSizeChromosome();

    /**
     * Liefert die Größe des Genotyps.
     *
     * @return int
     */
    public int getSizeGenotype();

    /**
     * Liefert die Größe für die natürliche Selektion für einen Genotyp.
     *
     * @return int
     */
    public int getTournamentSize();

    /**
     * Beim Elitismus wird das fitteste Chromosom in die nächste Generation übernommen.
     *
     * @return boolean
     */
    public boolean isElitism();

    /**
     * Zufällige Veränderung der Gene.<br>
     * Die Mutation verändert zufällig ein oder mehrere Gene eines Chromosoms.
     *
     * @param chromosome {@link Chromosome}
     */
    public default void mutate(final Chromosome<G> chromosome)
    {
        for (int i = 0; i < chromosome.size(); i++)
        {
            if (getRandom().nextDouble() < getMutationRate())
            {
                int j = getRandom().nextInt(chromosome.size());

                G gene1 = chromosome.getGene(i);
                G gene2 = chromosome.getGene(j);

                chromosome.setGene(j, gene1);
                chromosome.setGene(i, gene2);
            }
        }

        // @formatter:off
//        IntStream.range(0, chromosome.size())
//            .parallel()
//            .forEach(i -> {
//                if (getRandom().nextDouble() < getMutationRate())
//                {
//                    int j = getRandom().nextInt(chromosome.size());
//
//                    G gene1 = chromosome.getGene(i);
//                    G gene2 = chromosome.getGene(j);
//
//                    chromosome.setGene(j, gene1);
//                    chromosome.setGene(i, gene2);
//                }
//            });
        // @formatter:on
    }

    /**
     * Befüllt das Chromosom mit Genen.<br>
     *
     * @param chromosome {@link Chromosome}
     * @see Chromosome#populateChromosome()
     */
    public void populateChromosome(Chromosome<G> chromosome);

    /**
     * Setzt die Rate der Vererbung eines Chromosoms.
     *
     * @param rate double
     */
    public void setCrossoverRate(double rate);

    /**
     * Beim Elitismus wird das fitteste Chromosom in die nächste Generation übernommen.
     *
     * @param elitism boolean
     */
    public void setElitism(boolean elitism);

    /**
     * Setzt die Rate der Mutation eines Chromosoms.
     *
     * @param rate double
     */
    public void setMutationRate(double rate);

    /**
     * Setzt die Größe des Chromosoms.
     *
     * @param size int
     */
    public void setSizeChromosome(int size);

    /**
     * Setzt die Größe des Genotyps.
     *
     * @param size int
     */
    public void setSizeGenotype(int size);

    /**
     * Setzt die Größe für die natürliche Selektion für einen Genotyp.
     *
     * @param size int
     */
    public void setTournamentSize(int size);

    /**
     * Erstellt einen String aus einem {@link Chromosome}.
     *
     * @param chromosome {@link Chromosome}
     * @return String
     */
    public default String toString(final Chromosome<G> chromosome)
    {
        String s = null;

        // @formatter:off
        s = Stream.of(chromosome.getGenes())
                .map(Gene::getValue)
                .map(Optional::ofNullable)
                .map(o -> o.map(Object::toString).orElse("null"))
                .collect(Collectors.joining(","));
        // @formatter:on

        return s;
    }

    /**
     * Zufällige Auswahl eines Individuums (Chromosom) für die Rekombination.<br>
     * (survival of the fittest)
     *
     * @param genotype {@link Genotype}
     * @return {@link Chromosome}
     */
    public default Chromosome<G> tournamentSelection(final Genotype<G> genotype)
    {
        Genotype<G> tournament = createEmptyGenotype(getTournamentSize());

        for (int i = 0; i < getTournamentSize(); i++)
        {
            int randomID = getRandom().nextInt(genotype.size());

            tournament.setChromosome(i, genotype.getChromosome(randomID));
        }

        Chromosome<G> fittest = tournament.getFittest();

        return fittest;
    }
}
