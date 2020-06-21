// Erzeugt: 01.09.2015
package de.freese.ga.chromonome;

import de.freese.ga.algoritm.Algorithm;
import de.freese.ga.gene.Gene;
import de.freese.ga.genotype.Genotype;

/**
 * Interface eines Chromosomes für genetische Algorythmen.<br>
 * Chromosome = Mögliche Lösung
 *
 * @author Thomas Freese
 * @param <G> Typ des Genoms
 */
public interface Chromosome<G extends Gene<?>>
{
    /**
     * Berechnet die Fitness-Funktion.<br>
     *
     * @return double
     * @see Algorithm#calcFitnessValue(Chromosome)
     */
    public double calcFitnessValue();

    /**
     * Prüft, ob der Typ und das Value des Genoms im Chromosom vorhanden ist.
     *
     * @param gene {@link Gene}
     * @return boolean
     */
    public boolean contains(G gene);

    /**
     * Liefert das Genom am Index.
     *
     * @param index int
     * @return {@link Gene}
     */
    public G getGene(int index);

    /**
     * Liefert die Genome.
     *
     * @return {@link Gene}[]
     */
    public G[] getGenes();

    /**
     * Befüllt das Chromosom mit Genen.<br>
     *
     * @see Algorithm#populateChromosome(Chromosome)
     * @see Genotype#pupulateGenotype()
     */
    public void populateChromosome();

    /**
     * Setzt das Genom am Index.
     *
     * @param index int
     * @param gene {@link Gene}
     */
    public void setGene(int index, G gene);

    /**
     * Setzt die Genome.
     *
     * @param genes {@link Gene}[]
     */
    public void setGenes(G[] genes);

    /**
     * Liefert die Größe des Chromosomes (Anzahl Gene).
     *
     * @return int
     */
    public int size();
}
