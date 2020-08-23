// Erzeugt: 01.09.2015
package de.freese.ga;

import java.util.Objects;

/**
 * Basisklasse eines Genoms / Gens für genetische Algorythmen.
 *
 * @author Thomas Freese
 */
public class Gene implements Comparable<Gene>
{
    /**
     *
     */
    private Object value = null;

    /**
     * Erstellt ein neues {@link Gene} Object.
     */
    public Gene()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link Gene} Object.
     *
     * @param value Object
     */
    public Gene(final Object value)
    {
        super();

        setValue(value);
    }

    /**
     * Selber Typ und selber Wert.
     */
    @Override
    public int compareTo(final Gene o)
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
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Gene))
        {
            return false;
        }

        Gene other = (Gene) obj;

        return Objects.equals(this.value, other.value);
    }

    /**
     * @return Boolean
     */
    public Boolean getBoolean()
    {
        return getValue();
    }

    /**
     * @return Double
     */
    public Double getDouble()
    {
        return getValue();
    }

    /**
     * @return Integer
     */
    public Integer getInteger()
    {
        return getValue();
    }

    /**
     * Liefert den Wert des Genoms.
     *
     * @return Object
     */
    public <T> T getValue()
    {
        return (T) this.value;
    }

    /**
     * Setzt den Wert des Genoms.
     *
     * @param value Object
     */
    public void setValue(final Object value)
    {
        this.value = value;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.value);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(": ");
        sb.append(Objects.toString(getValue(), "null"));

        return sb.toString();
    }
}
