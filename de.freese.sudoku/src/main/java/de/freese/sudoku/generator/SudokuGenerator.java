// Created: 07.11.2009
/**
 * 07.11.2009
 */
package de.freese.sudoku.generator;

import de.freese.sudoku.generator.algorythm.AlgorythmLinear;
import de.freese.sudoku.generator.algorythm.AlgorythmRecursiveBacktracking;
import de.freese.sudoku.generator.algorythm.ISudokuAlgorythm;

/**
 * Erstellt mit einem {@link ISudokuAlgorythm} ein gültiges Rätsel.
 * 
 * @author Thomas Freese
 */
public final class SudokuGenerator
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		SudokuGenerator generator = new SudokuGenerator();
		int[][] grid = generator.create(6);

		toString(grid);
	}

	/**
	 * @param grid int[][]
	 */
	public static void toString(final int[][] grid)

	{
		int blockSize = (int) Math.sqrt(grid.length);

		StringBuilder sb = new StringBuilder();

		for (int x = 0; x < grid.length; x++)
		{
			if ((x % blockSize) == 0)
			{
				for (int i = 0; i < ((grid.length * 3) + blockSize + 1); i++)
				{
					sb.append("-");
				}

				sb.append("\n");
			}

			for (int y = 0; y < grid[0].length; y++)
			{
				if ((y % blockSize) == 0)
				{
					sb.append("|");
				}

				sb.append(String.format("%2d ", grid[x][y]));
			}

			sb.append("|\n");
		}

		for (int i = 0; i < ((grid.length * 3) + blockSize + 1); i++)
		{
			sb.append("-");
		}

		sb.append("\n");

		System.out.println(sb.toString());
	}

	/**
	 *
	 */
	private ISudokuAlgorythm algorythm = new AlgorythmRecursiveBacktracking();

	/**
	 * Erstellt ein neues {@link SudokuGenerator} Object.
	 */
	public SudokuGenerator()
	{
		super();
	}

	/**
	 * Erstellt das Rätsel einer bestimmten Blockgrösse und liefert das Array.
	 * 
	 * @param blockSize int
	 * @return int[][]
	 */
	public int[][] create(final int blockSize)
	{
		int n = blockSize * blockSize;

		int[][] grid = new int[n][n];

		return create(grid);
	}

	/**
	 * Füllt das Array mit Zahlen zu einem gültigen Rätsel.
	 * 
	 * @param grid int[][]
	 * @return int[][]
	 */
	public int[][] create(final int[][] grid)
	{
		if (grid == null)
		{
			throw new NullPointerException();
		}

		if (grid.length == 0)
		{
			throw new IllegalArgumentException("Array ist leer !");
		}

		if (grid.length != grid[0].length)
		{
			String text =
					String.format("Array ist falsch dimensioniert: x=%d, y=%d !", grid.length,
							grid[0].length);

			throw new IllegalArgumentException(text);
		}

		double blockSize = Math.sqrt(grid.length);

		if ((blockSize - Math.floor(blockSize)) != 0.0D)
		{
			throw new IllegalArgumentException(
					"Array benötigt ganzzahlige Wurzel der Dimensionen !");
		}

		if (blockSize > 4)
		{
			setAlgorythm(new AlgorythmLinear());
		}
		else
		{
			setAlgorythm(new AlgorythmRecursiveBacktracking());
		}

		while (!this.algorythm.create(grid))
		{
			// Empty
		}

		return grid;
	}

	/**
	 * @param algorythm {@link ISudokuAlgorythm}
	 */
	private void setAlgorythm(final ISudokuAlgorythm algorythm)
	{
		this.algorythm = algorythm;
	}
}
