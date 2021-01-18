// Created: 07.11.2009
/**
 * 07.11.2009
 */
package de.freese.sudoku.generator.algorythm;

/**
 * Algorythmus zum Erstellen von Sudoku-Raetseln mit rekursivem Backtracking.
 *
 * @author Thomas Freese
 */
public class AlgorythmRecursiveBacktracking implements ISudokuAlgorythm
{
    /**
     * Prueft ob der Wert bereits in der Box existiert.
     * 
     * @param grid int[][]
     * @param x int
     * @param y int
     * @param value int
     * @return boolean
     */
    private boolean checkBox(final int[][] grid, final int x, final int y, final int value)
    {
        // Oberes, linke Ecke der Box herausfinden
        int blockSize = (int) Math.sqrt(grid.length);

        int x_start = (x / blockSize) * blockSize;
        int y_start = (y / blockSize) * blockSize;

        for (int a = x_start; a < (x_start + blockSize); a++)
        {
            for (int b = y_start; b < (y_start + blockSize); b++)
            {
                if (grid[a][b] == value)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Prueft ob der Wert bereits in der Spalte existiert.
     * 
     * @param grid int[][]
     * @param column int
     * @param value int
     * @return boolean
     */
    private boolean checkColumn(final int[][] grid, final int column, final int value)
    {
        for (int y = 0; y < grid[column].length; y++)
        {
            if (grid[column][y] == value)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Prueft ob der Wert bereits in der Zeile existiert.
     * 
     * @param grid int[][]
     * @param row int
     * @param value int
     * @return boolean
     */
    private boolean checkRow(final int[][] grid, final int row, final int value)
    {
        for (int[] element : grid)
        {
            if (element[row] == value)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @see de.freese.sudoku.generator.algorythm.ISudokuAlgorythm#create(int[][])
     */
    @Override
    public boolean create(final int[][] grid)
    {
        boolean emptyField = false;

        // Moegliche Zahglen zufaellig generieren
        int[] shuffledNumbers = shuffleNumbers(grid.length);

        // Durch die Spalten gehen
        for (int x = 0; x < grid.length; x++)
        {
            // Durch die Zeilen gehen
            for (int y = 0; y < grid[0].length; y++)
            {
                if (grid[x][y] == 0)
                {
                    emptyField = true;

                    // Fuer alle moeglichen Zahlen
                    for (int k = 0; k < grid.length; k++)
                    {
                        int value = shuffledNumbers[k] + 1;

                        if (isLegal(grid, x, y, value))
                        {
                            grid[x][y] = value;

                            // Versuchen den Rest aufzufuellen
                            if (create(grid))
                            {
                                return true;
                            }

                            // Backtracking
                            grid[x][y] = 0;
                        }
                    }

                    // Ungueltigen Raetsel -> Backtracking
                    return false;
                }
            }
        }

        if (!emptyField)
        {
            // Fertig !
            return true;
        }

        return false;
    }

    /**
     * Liefert true, wenn Value noch nich in der Zeile, Spalte oder der Box vorhanden ist.
     * 
     * @param grid int[][]
     * @param x int
     * @param y int
     * @param value int
     * @return boolean
     */
    private boolean isLegal(final int[][] grid, final int x, final int y, final int value)
    {
        return !checkBox(grid, x, y, value) && !checkColumn(grid, x, value) && !checkRow(grid, y, value);
    }

    /**
     * Liefert ein Array der verwendeten Zahlen in zufaelliger Reihenfolge.
     * 
     * @param size int
     * @return int[]
     */
    private int[] shuffleNumbers(final int size)
    {
        int[] list = new int[size];

        for (int i = 0; i < size; i++)
        {
            list[i] = i;
        }

        for (int i = 0; i < size; i++)
        {
            int r = (int) (Math.random() * size);
            int swap = list[r];
            list[r] = list[i];
            list[i] = swap;
        }

        return list;
    }
}
