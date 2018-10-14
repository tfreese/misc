// Erzeugt: 01.09.2015
package de.freese.ga.gene;

/**
 * @author Thomas Freese
 * @param <V> Konkreter Typ des Values.
 */
public class ObjectGene<V> extends AbstractGene<V>
{
    /**
     * Erzeugt eine neue Instanz von {@link ObjectGene}.
     */
    public ObjectGene()
    {
        super();
    }

    /**
     * Erzeugt eine neue Instanz von {@link ObjectGene}.
     * 
     * @param value Object
     */
    public ObjectGene(final V value)
    {
        super();

        setValue(value);
    }
}
