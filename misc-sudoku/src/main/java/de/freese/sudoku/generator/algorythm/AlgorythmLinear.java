// Created: 07.11.2009
/**
 * 07.11.2009
 */
package de.freese.sudoku.generator.algorythm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Algorythmus zum Erstelln von Sudoku-Raetseln.
 *
 * @author Thomas Freese
 */
public class AlgorythmLinear implements ISudokuAlgorythm
{
    /**
     *
     */
    private int blockSize = 0;

    /**
     *
     */
    private int[][] grid;

    /**
     *
     */
    private List<Integer> numberList;

    /**
     * @see de.freese.sudoku.generator.algorythm.ISudokuAlgorythm#create(int[][])
     */
    @Override
    public boolean create(final int[][] grid)
    {
        this.grid = grid;
        this.blockSize = (int) Math.sqrt(grid.length);
        this.numberList = new ArrayList<>(grid.length);

        int currentRow = 0;

        // Versuche zum aufloesen einer Zeile
        int[] trials = new int[grid.length];
        boolean traceOn = true;

        // Zeilenweisen fuellen des Arrays
        while (currentRow < grid[0].length)
        {
            trials[currentRow]++;

            // Zeile generieren
            if (generateRow(currentRow))
            {
                if (traceOn)
                {
                    System.out.print("Row " + (currentRow + 1) + " generated after " + trials[currentRow] + " trial");

                    if (trials[currentRow] > 1)
                    {
                        System.out.print("s");
                    }

                    System.out.println(".");
                }

                currentRow++;
                continue;
            }

            // Generierung fehlgeschlagen, nochmal versuchen
            if (trials[currentRow] < (this.blockSize * this.blockSize * this.blockSize * 2))
            {
                continue;
            }

            // Generierung weiterhin fehlgeschlagen, dalle Zeilen des Blocks nochmal erzeugen
            if (traceOn)
            {
                System.out.print("Quitting for row: " + (currentRow + 1));
            }

            while ((currentRow % this.blockSize) != 0)
            {
                trials[currentRow--] = 0;
            }

            trials[currentRow] = 0;

            if (traceOn)
            {
                System.out.println(". Starting over with row: " + (currentRow + 1) + ".");
            }
        }

        // Zahlen sind 0-based, auf 1 normalisieren
        for (int x = 0; x < grid.length; x++)
        {
            for (int y = 0; y < grid[0].length; y++)
            {
                grid[x][y]++;
            }
        }

        return true;
    }

    /**
     * Fuellen der internen Liste mit allen noch verfuegbaren Zahlen.
     *
     * @param row int
     * @param col int
     * @return int, Anzahl verfuegbarer Zahlen
     */
    private int fillArrayList(final int row, final int col)
    {
        boolean[] available = new boolean[this.grid.length];
        Arrays.fill(available, true);

        // Entfernen der Zahlen, die in der Zeile schon exisiteren
        for (int x = 0; x < row; x++)
        {
            available[this.grid[x][row]] = false;
        }

        // Entfernen der Zahlen, die in der Spalte schon exisiteren
        for (int y = 0; y < col; y++)
        {
            available[this.grid[col][y]] = false;
        }

        // Entfernen der Zahlen, die in der Box schon exisiteren
        Point rowRange = getRegionRowsOrCols(row);
        Point colRange = getRegionRowsOrCols(col);

        for (int x = rowRange.x; x < row; x++)
        {
            for (int y = colRange.x; y <= colRange.y; y++)
            {
                available[this.grid[x][y]] = false;
            }
        }

        // int x_start = (col / this.blockSize) * this.blockSize;
        // int y_start = (row / this.blockSize) * this.blockSize;
        //
        // for (int x = x_start; x < x_start + this.blockSize; x++)
        // {
        // for (int y = y_start; y < y_start + this.blockSize; y++)
        // {
        // available[this.grid[x][y]] = false;
        // }
        // }

        this.numberList.clear();

        // Fuellen der Liste mit den restlichen verfuegbaren Zahlen.
        for (int i = 0; i < this.grid.length; i++)
        {
            if (available[i])
            {
                this.numberList.add(i);
            }
        }

        return this.numberList.size();
    }

    /**
     * Versuchen eine Zeile aufzubauen.
     *
     * @param row int
     * @return boolean
     */
    private boolean generateRow(final int row)
    {
        for (int col = 0; col < this.grid.length; col++)
        {
            // Keine verfuegbaren Zahlen mehr -> Abbruch
            if (fillArrayList(row, col) == 0)
            {
                return false;
            }

            // Zufuellige frei Zahl zuweisen
            int index = (int) (Math.random() * this.numberList.size());
            this.grid[row][col] = this.numberList.remove(index);
        }

        return true;
    }

    /**
     * Liefert die erste und letzte Zeile/Spalte innerhal des Blocks.
     *
     * @param rowOrCol int
     * @return {@link Point}
     */
    private Point getRegionRowsOrCols(final int rowOrCol)
    {
        int x = (rowOrCol / this.blockSize) * this.blockSize;
        int y = (x + this.blockSize) - 1;
        Point point = new Point(x, y);

        return point;
    }
}
