// Created: 13.04.2018
package de.freese.ga.genotype;

import de.freese.ga.algoritm.Algorithm;
import de.freese.ga.gene.Gene;

/**
 * Default-Implementierung eines {@link Genotype}.<br>
 *
 * @author Thomas Freese
 * @param <G> Konkreter Typ des Genoms
 */
public class DefaultGenotype<G extends Gene<?>> extends AbstractGenotype<G>
{
    /**
     * Erzeugt eine neue Instanz von {@link DefaultGenotype}.
     *
     * @param algorithm {@link Algorithm}
     */
    public DefaultGenotype(final Algorithm<G> algorithm)
    {
        super(algorithm);
    }

    /**
     * Erzeugt eine neue Instanz von {@link DefaultGenotype}.
     *
     * @param algorithm {@link Algorithm}
     * @param size int
     */
    public DefaultGenotype(final Algorithm<G> algorithm, final int size)
    {
        super(algorithm, size);
    }
}
