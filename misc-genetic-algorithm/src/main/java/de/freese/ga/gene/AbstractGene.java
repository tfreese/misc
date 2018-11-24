// Erzeugt: 01.09.2015
package de.freese.ga.gene;

import java.util.Objects;

/**
 * Basisklasse eines Genoms / {@link Gene}.
 *
 * @author Thomas Freese
 * @param <V> Konkreter Typ des Values.
 */
public abstract class AbstractGene<V> implements Gene<V>
{
    /**
     *
     */
    private V value = null;

    /**
     *
     */
    public AbstractGene()
    {
        super();
    }

    /**
     * Selber Typ und selber Wert.
     */
    @SuppressWarnings(
    {
            "unchecked", "rawtypes"
    })
    @Override
    public int compareTo(final Gene<V> o)
    {
        if (o == null)
        {
            return 0;
        }

        if (o == this)
        {
            return 0;
        }

        if (getClass() != o.getClass())
        {
            return 0;
        }

        int comp = 0;

        if (getValue() instanceof Comparable)
        {
            comp = ((Comparable) getValue()).compareTo(o.getValue());
        }
        else
        {
            throw new IllegalStateException("GeneValue must implement Comparable");
        }

        return comp;
    }

    /**
     * Selber Typ und selber Wert.
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
        {
            return false;
        }

        if (obj == this)
        {
            return true;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        final Gene<?> other = (Gene<?>) obj;

        return Objects.deepEquals(getValue(), other.getValue());
    }

    /**
     * @see de.freese.ga.gene.Gene#getValue()
     */
    @Override
    public V getValue()
    {
        return this.value;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        if (getValue() == null)
        {
            return -13;
        }

        return getValue().hashCode();
    }

    /**
     * @see de.freese.ga.gene.Gene#setValue(java.lang.Object)
     */
    @Override
    public void setValue(final V value)
    {
        this.value = value;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(": ");
        sb.append(getValue().toString());

        return sb.toString();
    }
}
