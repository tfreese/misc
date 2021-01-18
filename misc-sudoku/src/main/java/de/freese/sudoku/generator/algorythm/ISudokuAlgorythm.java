// Created: 07.11.2009
/**
 * 07.11.2009
 */
package de.freese.sudoku.generator.algorythm;

/**
 * Interface fuer einen Algorythmus zum Erstellen von Sudoku-Ruetseln.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ISudokuAlgorythm
{
    /**
     * Fuellt das Array mit den Zahlen.
     * 
     * @param grid int[][]
     * @return boolean, true, wenn das Raetsel gueltig ist.
     */
    public boolean create(final int[][] grid);
}
