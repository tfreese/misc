/**
 * Created: 17.04.2018
 */

package de.freese.ga.examples.coins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.freese.ga.algoritm.AbstractAlgorithm;
import de.freese.ga.chromonome.Chromosome;
import de.freese.ga.chromonome.DefaultChromosome;
import de.freese.ga.gene.Gene;
import de.freese.ga.gene.IntegerGene;

/**
 * @author Thomas Freese
 */
public class CoinsAlgorithm extends AbstractAlgorithm<Gene<Integer>>
{
    /**
    *
    */
    private static final int MAXIMUM_CENTS = 99;

    /**
     * Enthält die Anzahl von Münzen pro Wert.
     */
    private Map<Integer, Long> coinCounter = new HashMap<>();

    /**
     *
     */
    private final List<Integer> existingCoins = new ArrayList<>();

    /**
     *
     */
    private int targetCents = 0;

    /**
     * Erstellt ein neues {@link CoinsAlgorithm} Object.
     */
    public CoinsAlgorithm()
    {
        super(0, 0);
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#calcFitnessValue(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public double calcFitnessValue(final Chromosome<Gene<Integer>> chromosome)
    {
        int cents = getCents(chromosome);
        // int totalCoins = getNumberOfCoins(chromosome);

        int changeDifference = Math.abs(getTargetCents() - cents);

        // 99 Cent ist maximum.
        double fitness = (99 - changeDifference);

        // Zielbetrag erreicht.
        if (cents == getTargetCents())
        {
            // fitness += 100 - (10 * totalCoins);
            fitness = getMaxFitness();
        }

        return fitness;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#crossover(de.freese.ga.chromonome.Chromosome, de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public Chromosome<Gene<Integer>> crossover(final Chromosome<Gene<Integer>> parent1, final Chromosome<Gene<Integer>> parent2)
    {
        Chromosome<Gene<Integer>> population = new DefaultChromosome<>(parent1.getAlgorithm());

        for (int i = 0; i < parent1.size(); i++)
        {
            final Gene<Integer> coin;

            if (Math.random() <= getCrossoverRate())
            {
                coin = parent1.getGene(i);
            }
            else
            {
                coin = parent2.getGene(i);
            }

            // Zählen wie viele Münzen von diesem Wert insgesamt vorhanden sind.
            long coinsExisting = this.coinCounter.getOrDefault(coin.getValue(), 1L);

            // Zählen wie viele Münzen von diesem Wert im Chromosom bereits vorhanden sind.
            long coinsInPopulation = Stream.of(population.getGenes()).filter(g -> g != null).filter(g -> g.getValue() == coin.getValue())
                    .count();

            // Münze eines Wertes nur zuweisen, wenn noch welche übrig sind.
            if (coinsInPopulation < coinsExisting)
            {
                population.setGene(i, coin);
            }
            else
            {
                population.setGene(i, new IntegerGene(0));
            }
        }

        return population;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#getMaxFitness()
     */
    @Override
    public double getMaxFitness()
    {
        // Keine Lösung bekannt.
        // return Double.MAX_VALUE;
        return 1000D;
    }

    /**
     * @return int
     */
    public int getTargetCents()
    {
        return this.targetCents;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#pupulateChromosome(de.freese.ga.chromonome.Chromosome)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void pupulateChromosome(final Chromosome<Gene<Integer>> chromosome)
    {
        List<Gene<Integer>> t = new ArrayList<>();

        for (int i = 0; i < chromosome.size(); i++)
        {
            t.add(new IntegerGene(this.existingCoins.get(i)));
        }

        // Randomly reorder the tour
        Collections.shuffle(t);
        chromosome.setGenes(t.toArray(new Gene[0]));
    }

    /**
     * @param existingCoins List<Integer>
     */
    public void setExistingCoins(final List<Integer> existingCoins)
    {
        this.coinCounter.clear();
        this.existingCoins.clear();
        this.existingCoins.addAll(existingCoins);

        // 0-Münzen falls wir nicht so viele brauchen, wie die das Chromosom lang ist.
        for (int i = 0; i < existingCoins.size(); i++)
        {
            this.existingCoins.add(0);
        }

        setSizeChromosome(this.existingCoins.size());

        // Anzahl Münzen pro Wert zählen.
        List<Integer> list = new ArrayList<>(this.existingCoins);
        list.removeIf(value -> value == 0); // 0-Münzen igorieren

        this.coinCounter = list.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    /**
     * Cents
     *
     * @param cents int
     */
    public void setTargetCents(final int cents)
    {
        if ((cents <= 0) || (cents > MAXIMUM_CENTS))
        {
            throw new IllegalArgumentException("cents must be between 0 - " + MAXIMUM_CENTS + ": " + cents);
        }

        this.targetCents = cents;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#toString(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public String toString(final Chromosome<Gene<Integer>> chromosome)
    {
        String s = null;

        // @formatter:off
        s = Stream.of(chromosome.getGenes())
                 .map(Gene::getValue)
                 .filter(coin -> coin > 0)
                 .map(Object::toString)
                 //.map(Optional::ofNullable)
                 //.map(o -> o.map(Object::toString).orElse("null"))
                 .collect(Collectors.joining(" + "));
        // @formatter:on

        return s;
    }

    /**
     * Berechnet den Münzinhalt des Chromosoms in Cent.
     *
     * @param chromosome {@link Chromosome}
     * @return int
     */
    protected int getCents(final Chromosome<Gene<Integer>> chromosome)
    {
        int amount = 0;

        for (Gene<Integer> gene : chromosome.getGenes())
        {
            amount += gene.getValue();
        }

        return amount;
    }

    /**
     * Liefert die Anzahl der Münzen.
     *
     * @param chromosome {@link Chromosome}
     * @return int
     */
    protected int getNumberOfCoins(final Chromosome<Gene<Integer>> chromosome)
    {
        int coins = 0;

        for (Gene<Integer> gene : chromosome.getGenes())
        {
            if (gene.getValue() > 0)
            {
                coins++;
            }
        }

        return coins;
    }
}
