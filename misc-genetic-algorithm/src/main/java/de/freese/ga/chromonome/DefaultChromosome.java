// Created: 13.04.2018
package de.freese.ga.chromonome;

import de.freese.ga.algoritm.Algorithm;
import de.freese.ga.gene.Gene;

/**
 * Default-Implementierung eines {@link Chromosome}.<br>
 *
 * @author Thomas Freese
 * @param <G> Konkreter Typ des Genoms.
 */
public class DefaultChromosome<G extends Gene<?>> extends AbstractChromosome<G>
{
    /**
     * Erzeugt eine neue Instanz von {@link DefaultChromosome}.
     *
     * @param algorithm {@link Algorithm}
     */
    public DefaultChromosome(final Algorithm<G> algorithm)
    {
        super(algorithm);
    }
}
