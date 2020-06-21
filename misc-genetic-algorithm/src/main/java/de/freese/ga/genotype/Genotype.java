// Erzeugt: 02.09.2015
package de.freese.ga.genotype;

import de.freese.ga.algoritm.Algorithm;
import de.freese.ga.chromonome.Chromosome;
import de.freese.ga.chromonome.DefaultChromosome;
import de.freese.ga.gene.Gene;

/**
 * Interface eines Genotypes für genetische Algorythmen.<br>
 * Genotype = Sammlung von Chromosomen / Lösungen
 *
 * @author Thomas Freese
 * @param <G> Konkreter Typ des Genoms
 */
public interface Genotype<G extends Gene<?>>
{
    /**
     * Liefert den Algorithmus.
     *
     * @return {@link Algorithm}
     */
    public Algorithm<G> getAlgorithm();

    /**
     * Liefert das Chromosom am Index.
     *
     * @param index int
     * @return {@link Chromosome}
     */
    public Chromosome<G> getChromosome(int index);

    /**
     * Liefert das Chromosom mit dem höchsten Fitnesswert.
     *
     * @return {@link Chromosome}
     */
    public default Chromosome<G> getFittest()
    {
        Chromosome<G> fittest = getChromosome(0);

        for (int i = 1; i < size(); i++)
        {
            if (fittest.calcFitnessValue() <= getChromosome(i).calcFitnessValue())
            {
                fittest = getChromosome(i);
            }
        }

        return fittest;
    }

    /**
     * Befüllt den Genotype mit Chromosomen.
     */
    public default void pupulateGenotype()
    {
        for (int i = 0; i < size(); i++)
        {
            Chromosome<G> chromosome = new DefaultChromosome<>(getAlgorithm());
            chromosome.populateChromosome();
            setChromosome(i, chromosome);
        }
    }

    /**
     * Setzt das Chromosom am Index.
     *
     * @param index int
     * @param chromosome {@link Chromosome}
     */
    public void setChromosome(int index, Chromosome<G> chromosome);

    /**
     * Liefert die Größe des Genotypes (Anzahl Chromonomen).
     *
     * @return int
     */
    public int size();
}
