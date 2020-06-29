/**
 * Created: 29.06.2020
 */

package de.freese.ga;

import java.util.Objects;

/**
 * Basisklasse eines Genotypes für genetische Algorythmen.<br>
 * Genotype = Sammlung von Chromosomen / Lösungen
 *
 * @author Thomas Freese
 */
public abstract class Genotype
{
    /**
    *
    */
    private final Chromosome[] chromosomes;

    /**
    *
    */
    private final Config config;

    /**
     * Erstellt ein neues {@link Genotype} Object.
     *
     * @param config {@link Config}
     */
    public Genotype(final Config config)
    {
        this(config, config.getSizeGenotype());
    }

    /**
     * Erstellt ein neues {@link Genotype} Object.
     *
     * @param config {@link Config}
     * @param size int
     */
    protected Genotype(final Config config, final int size)
    {
        super();

        this.config = Objects.requireNonNull(config, "config required");
        this.chromosomes = new Chromosome[size];
    }

    /**
     * Erzeugt eine neues leeres Chromosom / Lösung.
     *
     * @return {@link Chromosome}
     */
    public abstract Chromosome createEmptyChromosome();

    /**
     * Erzeugt einen neuen leeren Genotype / Population.
     *
     * @return {@link Genotype}
     */
    public Genotype createEmptyGenotype()
    {
        return createEmptyGenotype(getConfig().getSizeGenotype());
    }

    /**
     * Erzeugt einen neuen leeren Genotype / Population.<br>
     *
     * @param size int
     * @return {@link Genotype}
     * @see Genotype#tournamentSelection()
     */
    public abstract Genotype createEmptyGenotype(int size);

    /**
     * Zufällige Rekombination ausgewählter Individuen (Chromosomen).<br>
     * Beim Crossover werden aus der Population/Genotype paarweise Chromosomen ausgewählt, die dann mit einer Wahrscheinlichkeit W zu kreuzen sind.
     *
     * @param parent1 {@link Chromosome}
     * @param parent2 {@link Chromosome}
     * @return {@link Chromosome}
     */
    public Chromosome crossover(final Chromosome parent1, final Chromosome parent2)
    {
        Chromosome population = createEmptyChromosome();

        for (int i = 0; i < parent1.size(); i++)
        {
            Gene gene = null;

            if (getConfig().getRandom().nextDouble() <= getConfig().getCrossoverRate())
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
     * 1. Zufällige Auswahl eines Individuums (Chromosom) für die Rekombination, {@link #tournamentSelection()}<br>
     * 2. Zufällige Rekombination ausgewählter Individuen (Chromosomen), {@link #crossover(Chromosome, Chromosome)}<br>
     * 3. Zufällige Veränderung der Gene, {@link Chromosome#mutate()}<br>
     *
     * @return {@link Genotype}
     */
    public Genotype evolve()
    {
        Genotype newPopulation = createEmptyGenotype();

        int elitismOffset = 0;

        if (getConfig().isElitism())
        {
            newPopulation.setChromosome(0, getFittest());
            elitismOffset = 1;
        }

        for (int i = elitismOffset; i < size(); i++)
        {
            // Loop over the population size and create new individuals with crossover.
            // Select parents
            Chromosome parent1 = tournamentSelection();
            Chromosome parent2 = tournamentSelection();

            // Kann bei einigen Beispielen zur Endlos-Schleife führen.
            // while(parent1.calcFitnessValue() == parent2.calcFitnessValue())
            // {
            // parent2 = tournamentSelection();
            // }

            // Crossover parents
            Chromosome child = crossover(parent1, parent2);

            // Add child to new population
            newPopulation.setChromosome(i, child);

            // Mutate population
            child.mutate();
        }

//        // @formatter:off
//        IntStream.range(elitismOffset, size())
//            .parallel()
//            .map(i -> {
//                // Loop over the population size and create new individuals with crossover
//                // Select parents
//                Chromosome<G> parent1 = tournamentSelection(genotype);
//                Chromosome<G> parent2 = tournamentSelection(genotype);
//
//                // Kann bei einigen Beispielen zur Endlos-Schleife führen.
////                while(parent1.calcFitnessValue() == parent2.calcFitnessValue())
////                {
////                    parent2 = tournamentSelection(genotype);
////                }
//
//                // Crossover parents
//                Chromosome<G> child = crossover(parent1, parent2);
//
//                // Add child to new population
//                newPopulation.setChromosome(i, child);
//
//                return i;
//            })
//            .forEach(i -> {
//                // Mutate population
//                mutate(newPopulation.getChromosome(i));
//                }
//            )
//            ;
//        // @formatter:on

        return newPopulation;
    }

    /**
     * Liefert das Chromosom am Index.
     *
     * @param index int
     * @return {@link Chromosome}
     */
    public Chromosome getChromosome(final int index)
    {
        return getChromosomes()[index];
    }

    /**
     * @return {@link Chromosome}[]
     */
    protected Chromosome[] getChromosomes()
    {
        return this.chromosomes;
    }

    /**
     * @return {@link Config}
     */
    protected Config getConfig()
    {
        return this.config;
    }

    /**
     * Liefert das Chromosom mit dem höchsten Fitnesswert.
     *
     * @return {@link Chromosome}
     */
    public Chromosome getFittest()
    {
        Chromosome fittest = getChromosome(0);

        for (int i = 1; i < size(); i++)
        {
            Chromosome chromosome = getChromosome(i);

            if (fittest.calcFitnessValue() <= chromosome.calcFitnessValue())
            {
                fittest = chromosome;
            }
        }

        return fittest;
    }

    /**
     * Befüllt den Genotype mit Chromosomen.
     *
     * @see Chromosome#populate()
     */
    public void populate()
    {
        for (int i = 0; i < size(); i++)
        {
            Chromosome chromosome = createEmptyChromosome();
            chromosome.populate();
            setChromosome(i, chromosome);
        }
    }

    /**
     * Setzt das Chromosom am Index.
     *
     * @param index int
     * @param chromosome {@link Chromosome}
     */
    public void setChromosome(final int index, final Chromosome chromosome)
    {
        getChromosomes()[index] = chromosome;
    }

    /**
     * Liefert die Größe des Genotypes, Anzahl von Chromonomen.
     *
     * @return int
     */
    public int size()
    {
        return getChromosomes().length;
    }

    /**
     * Zufällige Auswahl eines Individuums (Chromosom) für die Rekombination.<br>
     * (survival of the fittest)
     *
     * @return {@link Chromosome}
     */
    public Chromosome tournamentSelection()
    {
        Genotype tournament = createEmptyGenotype(getConfig().getTournamentSize());

        for (int i = 0; i < getConfig().getTournamentSize(); i++)
        {
            int randomID = getConfig().getRandom().nextInt(size());

            tournament.setChromosome(i, getChromosome(randomID));
        }

        Chromosome fittest = tournament.getFittest();

        return fittest;
    }

}
