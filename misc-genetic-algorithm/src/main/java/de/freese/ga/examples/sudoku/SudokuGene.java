// Created: 19.04.2018
package de.freese.ga.examples.sudoku;

import java.util.Objects;
import de.freese.ga.gene.IntegerGene;

/**
 * Beim Sudoku dürfen die fest vorgegebenen Zahlen nicht verändert werden !
 *
 * @author Thomas Freese
 */
public class SudokuGene extends IntegerGene
{
    /**
     *
     */
    private final boolean mutable;

    /**
     * Erzeugt eine neue Instanz von {@link SudokuGene}.
     *
     * @param value Integer
     * @param mutable boolean
     */
    public SudokuGene(final Integer value, final boolean mutable)
    {
        super();

        super.setValue(value);
        this.mutable = mutable;
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

        if (!super.equals(obj))
        {
            return false;
        }

        if (!(obj instanceof SudokuGene))
        {
            return false;
        }

        SudokuGene other = (SudokuGene) obj;

        return this.mutable == other.mutable;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();

        result = (prime * result) + Objects.hash(this.mutable);

        return result;
    }

    /**
     * Beim Sudoku dürfen die fest vorgegebenen Zahlen nicht verändert werden !
     *
     * @return boolean
     */
    public boolean isMutable()
    {
        return this.mutable;
    }

    /**
     * @see de.freese.ga.gene.AbstractGene#setValue(java.lang.Object)
     */
    @Override
    public void setValue(final Integer value)
    {
        if (!this.mutable)
        {
            return;
        }

        super.setValue(value);
    }
}
