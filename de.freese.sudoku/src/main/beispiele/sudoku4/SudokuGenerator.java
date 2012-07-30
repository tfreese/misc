package sudoku4;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

// Created: 01.11.2009

/**
 * produce a Sudoku grid
 * 
 * @author Paul-benoit Larochelle
 */
public class SudokuGenerator
{
	/**
	 * the grid with the numbers
	 */
	private int[][] grid;

	/**
	 * random and array list
	 */
	private Random ran;

	/**
	 *
	 */
	private ArrayList<Integer> al;

	/**
	 * number of row and column in a region
	 */
	private int size;

	/**
	 *
	 */
	private int regionSize;

	/**
	 * Erstellt ein neues {@link SudokuGenerator} Object.
	 * 
	 * @param size size of the Sudoku grid is received as parameter actually not the size of the
	 *            grid but the size of the region
	 */
	SudokuGenerator(final int size)
	{
		this.regionSize = size;
		this.size = size * size;
		// random number generator
		this.ran = new Random();
		// arraylist that will contain the possible values for every case in the grid
		this.al = new ArrayList<Integer>();

	}

	/**
	 * call to generate a new grid
	 * 
	 * @param traceOn boolean
	 */
	public void generate(final boolean traceOn)
	{
		// start by row 0
		int currentRow = 0;
		// to count the startOver
		int[] trials = new int[this.size];
		// this this the grid that we will fill
		this.grid = new int[this.size][this.size];
		// now let's fill the grid row by row
		while (currentRow < this.size)
		{
			trials[currentRow]++;
			// try to generate the row if it works pass to next roow
			if (genRow(currentRow))
			{
				if (traceOn)
				{
					System.out.print("Row " + (currentRow + 1) + " generated after "
							+ trials[currentRow] + " trial");
					if (trials[currentRow] > 1)
					{
						System.out.print("s");
					}
					System.out.println(".");
				}
				currentRow++;
				continue;
			}
			// so it didn't work check our count
			if (trials[currentRow] < this.regionSize * this.regionSize * this.regionSize * 2)
			{
				continue;
			}
			// so despite all our effort it does not fit we will have to restart for the whole
			// row regions
			if (traceOn)
			{
				System.out.print("Quitting for row: " + (currentRow + 1));
			}
			while (currentRow % this.regionSize != 0)
			{
				trials[currentRow--] = 0;
			}
			trials[currentRow] = 0;
			if (traceOn)
			{
				System.out.println(". Starting over with row: " + (currentRow + 1) + ".");
			}
		}
		// ok our grid is filled with 0-size but sudoku grids do not have 0
		for (int i = 0; i < this.size; i++)
		{
			for (int j = 0; j < this.size; j++)
			{
				this.grid[i][j]++;
			}
		}
	}

	/**
	 * try to generate an return true if it worked (it might be impossible to fill the row for
	 * example if the only element left in the row is 5 so the last column must be 5 but I may
	 * already have a 5 in the column or region)
	 * 
	 * @param row int
	 * @return boolean
	 */
	private boolean genRow(final int row)
	{
		// for every column in the row
		for (int col = 0; col < this.size; col++)
		{
			// fill the arrayList of available value if no value abort
			if (fillArrayList(row, col) == 0)
			{
				return false;
			}
			// ok I can retrieve a random value from the arrayList
			this.grid[row][col] = this.al.remove(this.ran.nextInt(this.al.size()));
		}
		return true;
	}

	/**
	 * fill the ArrayList with all available number for that row,col returns the number of elements
	 * in the arraylist
	 * 
	 * @param row int
	 * @param col int
	 * @return int
	 */
	private int fillArrayList(final int row, final int col)
	{
		boolean[] available = new boolean[this.size];
		// flag all the slot as available
		for (int i = 0; i < this.size; i++)
		{
			available[i] = true;
		}

		// remove the number already used in row
		for (int i = 0; i < row; i++)
		{
			available[this.grid[i][col]] = false;
		}
		// remove the number already used in col
		for (int i = 0; i < col; i++)
		{
			available[this.grid[row][i]] = false;
		}
		// now the region. I just have to take care of the row over me in
		// the region the columns to the left of my position have already been checked as
		// unavailable
		Point rowRange = getRegionRowsOrCols(row);
		Point colRange = getRegionRowsOrCols(col);
		for (int i = rowRange.x; i < row; i++)
		{
			for (int j = colRange.x; j <= colRange.y; j++)
			{
				available[this.grid[i][j]] = false;
			}
		}

		// empty the arrayList
		this.al.clear();
		// fill it with all still available numbers
		for (int i = 0; i < this.size; i++)
		{
			if (available[i])
			{
				this.al.add(i);
			}
		}
		return this.al.size();
	}

	/**
	 * return the first and last row/column of the region into which is located the (row or col)
	 * 
	 * @param rowOrCol int
	 * @return {@link Point}
	 */
	private Point getRegionRowsOrCols(final int rowOrCol)
	{
		int x = (rowOrCol / this.regionSize) * this.regionSize;
		int y = x + this.regionSize - 1;
		Point point = new Point(x, y);
		return point;
	}

	/**
	 * to retrieve the grid
	 * 
	 * @return int[][]
	 */
	public int[][] getGrid()
	{
		return this.grid;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		// line to separate the region we build it in a StringBuffer
		StringBuffer buffer = new StringBuffer(this.size * this.size * this.size);
		buffer.append('+');
		for (int i = 0; i < this.size * 2 + this.size - 2; i++)
		{
			buffer.append('-');
		}
		// if I use 2 digits to represent the number
		if (this.size >= 16)
		{
			for (int i = 0; i < this.regionSize * 2 + 4; i++)
			{
				buffer.append('-');
			}
		}
		buffer.append('+');
		// saved in a String
		String dash = new String(buffer);

		// and we continue with the numbers
		buffer.append("\n|");
		for (int i = 0; i < this.size; i++)
		{ // for every row
			for (int j = 0; j < this.size; j++)
			{ // and column
				// depending of the size of the display
				// we may have to pad with spaces
				if (this.size >= 16)
				{
					if (this.grid[i][j] < 16)
					{
						buffer.append(' ');
					}
				}
				buffer.append(' ');
				buffer.append(Integer.toHexString((this.grid[i][j])).toUpperCase()); // add value to
				// String

				// add a | to separate the regions
				if (j % this.regionSize == this.regionSize - 1)
				{
					buffer.append(" | ");
				}
			}
			// add a serie of dash every region
			if (i % this.regionSize == this.regionSize - 1)
			{
				buffer.append('\n').append(dash);
			}
			buffer.append('\n');
			// add a | but if it is the last one
			if (i < this.size - 1)
			{
				buffer.append('|');
			}
		}
		return new String(buffer);
	}

	/**
	 * @param arg String[]
	 */
	public static void main(final String[] arg)
	{
		SudokuGenerator s = new SudokuGenerator(5);
		s.generate(true);
		System.out.print(s);
	}
}