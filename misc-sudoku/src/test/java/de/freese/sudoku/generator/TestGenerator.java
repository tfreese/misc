// Created: 07.11.2009
/**
 * 07.11.2009
 */
package de.freese.sudoku.generator;


/**
 * @author Thomas Freese
 */
public class TestGenerator
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		testCheckBlock();
		testBlockIndex();
	}

	/**
	 * 
	 */
	public static void testBlockIndex()
	{
		for (int x = 0; x < 25; x++)
		{
			for (int y = 0; y < 25; y++)
			{
				System.out.println(String.format("[%2d,%2d] = %d-%d-%d", x, y, x % 5, y % 5, (x / 5) + (y / 5)));
			}
		}
	}

	/**
	 * 
	 */
	public static void testCheckBlock()
	{
		// SudokuGenerator generator = new SudokuGenerator();
		// generator.checkBox(TestGrids.GRID3_1, 4, 4, 9);
	}

	/**
	 * Erstellt ein neues {@link TestGenerator} Object.
	 */
	public TestGenerator()
	{
		super();
	}
}
