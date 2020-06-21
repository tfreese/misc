// Erzeugt: 02.09.2015
package de.freese.ga.examples.travelling_salesman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import de.freese.ga.algoritm.AbstractAlgorithm;
import de.freese.ga.chromonome.Chromosome;
import de.freese.ga.chromonome.DefaultChromosome;
import de.freese.ga.gene.Gene;
import de.freese.ga.gene.ObjectGene;

/**
 * @author Thomas Freese
 */
public class TspAlgorithm extends AbstractAlgorithm<Gene<City>>
{
    /**
     *
     */
    private final List<City> cities = new ArrayList<>();

    /**
     * Erzeugt eine neue Instanz von {@link TspAlgorithm}.
     */
    public TspAlgorithm()
    {
        // Größen werden ausserhalb gesetzt.
        super(0, 0);
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#calcFitnessValue(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public double calcFitnessValue(final Chromosome<Gene<City>> chromosome)
    {
        return 1.0D / getDistance(chromosome);
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#crossover(de.freese.ga.chromonome.Chromosome, de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public Chromosome<Gene<City>> crossover(final Chromosome<Gene<City>> parent1, final Chromosome<Gene<City>> parent2)
    {
        // Create new child tour
        Chromosome<Gene<City>> childChromosome = new DefaultChromosome<>(parent1.getAlgorithm());

        // Get start and end sub tour positions for parent1's tour
        int startPos = getRandom().nextInt(parent1.size());
        int endPos = getRandom().nextInt(parent1.size());

        // Loop and add the sub tour from parent1 to our child
        for (int i = 0; i < childChromosome.size(); i++)
        {
            // If our start position is less than the end position
            if ((startPos < endPos) && (i > startPos) && (i < endPos))
            {
                childChromosome.setGene(i, parent1.getGene(i));
            } // If our start position is larger
            else if (startPos > endPos)
            {
                if (!((i < startPos) && (i > endPos)))
                {
                    childChromosome.setGene(i, parent1.getGene(i));
                }
            }
        }

        // Loop through parent2's city tour
        for (int i = 0; i < parent2.size(); i++)
        {
            // If child doesn't have the city add it
            if (!childChromosome.contains(parent2.getGene(i)))
            {
                // Loop to find a spare position in the child's tour
                for (int ii = 0; ii < childChromosome.size(); ii++)
                {
                    // Spare position found, add city
                    if (childChromosome.getGene(ii) == null)
                    {
                        childChromosome.setGene(ii, parent2.getGene(i));
                        break;
                    }
                }
            }
        }

        return childChromosome;
    }

    /**
     * Gets the total distance of the tour
     *
     * @param chromosome {@link Chromosome}
     * @return double
     */
    public double getDistance(final Chromosome<Gene<City>> chromosome)
    {
        double tourDistance = 0.0D;

        // Loop through our tour's cities
        for (int cityIndex = 0; cityIndex < chromosome.size(); cityIndex++)
        {
            // Get city we're travelling from
            City fromCity = chromosome.getGene(cityIndex).getValue();
            // City we're travelling to
            City destinationCity;

            // Check we're not on our tour's last city, if we are set our
            // tour's final destination city to our starting city
            if ((cityIndex + 1) < chromosome.size())
            {
                destinationCity = chromosome.getGene(cityIndex + 1).getValue();
            }
            else
            {
                destinationCity = chromosome.getGene(0).getValue();
            }

            // Get the distance between the two cities
            tourDistance += fromCity.distanceTo(destinationCity);
        }

        return tourDistance;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#getMaxFitness()
     */
    @Override
    public double getMaxFitness()
    {
        // Keine Lösung bekannt.
        return Double.MAX_VALUE;
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#pupulateChromosome(de.freese.ga.chromonome.Chromosome)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void pupulateChromosome(final Chromosome<Gene<City>> chromosome)
    {
        List<Gene<City>> t = new ArrayList<>();

        // Loop through all our destination cities and add them to our tour
        for (int cityIndex = 0; cityIndex < chromosome.size(); cityIndex++)
        {
            t.add(new ObjectGene<>(this.cities.get(cityIndex)));
        }

        // Randomly reorder the tour
        Collections.shuffle(t);
        chromosome.setGenes(t.toArray(new Gene[0]));
    }

    /**
     * Anzahl Städte = Anzahl Gene im Chromosom/Tour
     *
     * @param cities {@link List}
     */
    public void setCities(final List<City> cities)
    {
        Objects.requireNonNull(cities, "cities required");

        this.cities.clear();
        this.cities.addAll(cities);

        setSizeChromosome(this.cities.size());
    }

    /**
     * @see de.freese.ga.algoritm.Algorithm#toString(de.freese.ga.chromonome.Chromosome)
     */
    @Override
    public String toString(final Chromosome<Gene<City>> chromosome)
    {
        String s = null;

        // @formatter:off
        s = Stream.of(chromosome.getGenes())
               .map(Gene::getValue)
               .map(City::getName)
               .collect(Collectors.joining());
      // @formatter:on

        // StringJoiner sj = new StringJoiner("|", "|", "|");
        //
        // for (Gene<City> gene : chromosome.getGenes())
        // {
        // sj.add(gene.getValue().toString());
        // }
        //
        // s = sj.toString();

        return s;
    }
}
