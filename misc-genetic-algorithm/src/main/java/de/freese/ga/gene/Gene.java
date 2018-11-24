// Erzeugt: 01.09.2015
package de.freese.ga.gene;

/**
 * Interface eines Genoms für genetische Algorythmen.
 *
 * @author Thomas Freese
 * @param <V> Konkreter Typ des Values.
 */
public interface Gene<V> extends Comparable<Gene<V>>
{
    /**
     * Liefert den Wert des Genoms.
     *
     * @return Object
     */
    public V getValue();

    /**
     * Setzt den Wert des Genoms.
     *
     * @param value Object
     */
    public void setValue(V value);
}
