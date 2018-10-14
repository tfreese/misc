// Created: 19.04.2018
package de.freese.ga.examples.sudoku;

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
