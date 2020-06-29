/**
 * Created: 29.06.2020
 */

package de.freese.ga.examples.sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import de.freese.ga.Config;

/**
 * @author Thomas Freese
 */
public class SudokuConfig extends Config
{
    /**
     * Beim Sudoku dürfen die fest vorgegebenen Zahlen nicht verändert werden !
     */
    private final Map<Integer, SudokuGene> fixNumbers = new TreeMap<>();

    /**
    *
    */
    private int puzzleBlockSize = (int) Math.sqrt(this.puzzleSize);

    /**
     *
     */
    private int puzzleSize = 9;

    /**
     * Erstellt ein neues {@link SudokuConfig} Object.
     */
    public SudokuConfig()
    {
        super();
    }

    /**
     * @return {@link Map}
     */
    Map<Integer, SudokuGene> getFixNumbers()
    {
        return this.fixNumbers;
    }

    /**
     * @see de.freese.ga.Config#getMaxFitness()
     */
    @Override
    public double getMaxFitness()
    {
        /**
         * Summe pro Zeile, Spalte und Block.<br>
         * Gaußsche Summenformel = (n² + n) / 2
         */
        int puzzleSum = (int) (Math.pow(this.puzzleSize, 2) + this.puzzleSize) / 2;

        double fitness = 0.0D;

        // 405: Summe aller Zeilen = 9 * 45
        fitness += this.puzzleSize * puzzleSum;

        // // 405: Summe aller Spalten = 9 * 45
        // fitness += this.puzzleSize * this.puzzleSum;
        //
        // // 405: Summe aller Blöcke = 9 * 45
        // fitness += this.puzzleSize * this.puzzleSum;

        // 405 * 3 = 1215
        fitness *= 3;

        return fitness;
    }

    /**
     * @return int
     */
    int getPuzzleBlockSize()
    {
        return this.puzzleBlockSize;
    }

    /**
     * @return int
     */
    int getPuzzleSize()
    {
        return this.puzzleSize;
    }

    /**
     * @param inputStream {@link InputStream}
     * @return {@link List}>
     * @throws IOException Falls was schief geht.
     */
    public List<String[]> parsePuzzle(final InputStream inputStream) throws IOException
    {
        List<String[]> puzzle = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
        {
            // @formatter:off
            reader.lines()
                .filter(l -> !l.startsWith("-"))
                .map(l -> l.replace("|", " "))
                .map(l -> l.replace("_", "0"))
                .map(l -> l.replace("x", "0"))
                .map(l -> l.replace("X", "0"))
                //.peek(System.out::println)
                .map(l -> l.replace("  ", " "))
                .map(l -> l.replace("  ", " "))
                .map(String::trim)
                //.peek(System.out::println)
                .map(l -> l.split("[ ]"))
                //.peek(array -> System.out.println(Arrays.toString(array)))
                .forEach(puzzle::add);
            // @formatter:on
        }

        return puzzle;
    }

    /**
     * Ungekannte Zahlen sind als X markiert.<br>
     * Index 0 = Rows<br>
     * Index 1 = Columns
     *
     * @param puzzle {@link List}]
     */
    public void setPuzzle(final List<String[]> puzzle)
    {
        Objects.requireNonNull(puzzle, "puzzle required");

        if ((puzzle.size() > 9) || (puzzle.get(0).length > 9))
        {
            throw new IllegalArgumentException("only puzzle with 9x9 format supported");
        }

        this.puzzleSize = puzzle.size();
        this.puzzleBlockSize = (int) Math.sqrt(puzzle.size());

        this.fixNumbers.clear();

        for (int row = 0; row < this.puzzleSize; row++)
        {
            String[] columns = puzzle.get(row);

            for (int col = 0; col < this.puzzleSize; col++)
            {
                int index = (row * this.puzzleSize) + col;

                int number = Integer.parseInt(columns[col]);

                if (number > 0)
                {
                    this.fixNumbers.put(index, new SudokuGene(number, false));
                }
            }
        }

        setSizeChromosome(this.puzzleSize * this.puzzleSize);
    }
}
