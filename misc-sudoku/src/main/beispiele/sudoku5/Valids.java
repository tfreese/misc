package sudoku5;

/**
 * A Valids-object is basically a matrix N*N*N of booleans, where V[i,j,k] is true if number k is a
 * valid option for square (i,j), considering the values that are filled in in the puzzle.
 */
public class Valids
{
	/**
	 *
	 */
	@SuppressWarnings("unused")
	private int n;

	/**
	 *
	 */
	private int N;

	/**
	 *
	 */
	private boolean[][][] V;

	/**
	 * Erstellt ein neues {@link Valids} Object.
	 * 
	 * @param n int
	 */
	Valids(final int n)
	{
		this.n = n;
		this.N = n * n;
		this.V = new boolean[this.N][this.N][this.N];
		this.clear();
	}

	/**
	 * Erstellt ein neues {@link Valids} Object.
	 * 
	 * @param n int
	 * @param grid int[][]
	 */
	Valids(final int n, final int[][] grid)
	{
		this(n);
		this.update(grid);
	}

	/**
	 * Cleans V (sets all elements to 'true')
	 */
	public void clear()
	{
		for (int i = 0; i < this.N; i++)
		{
			for (int j = 0; j < this.N; j++)
			{
				for (int k = 0; k < this.N; k++)
				{
					this.V[i][j][k] = true;
				}
			}
		}
	}

	/**
	 * Counts the number of candidates in an array (representing a row or column)
	 * 
	 * @param i int
	 * @param j int
	 * @return int[]
	 */
	public int[] countCandidates(final int i, final int j)
	{
		int r = 0;
		int r2 = -1;
		for (int a = 0; a < this.N; a++)
		{
			if (this.V[i][j][a])
			{
				r++; // the number of possible values
				r2 = a; // one of the possible values (useful only if r=1)
			}
		}
		int[] _r =
		{
				r, r2
		};
		return _r;
	}

	/**
	 * Returns the valid values for square (i,j)
	 * 
	 * @param i int
	 * @param j int
	 * @return boolean[]
	 */
	public boolean[] get(final int i, final int j)
	{
		return this.V[i][j];
	}

	// public void set(int i, int j, int k, boolean value) {
	// this.V[i][j][k] = value;
	// }

	/**
	 * Checks if a value is valid for a square (i,j)
	 * 
	 * @param i int
	 * @param j int
	 * @param k int
	 * @return boolean
	 */
	public boolean get(final int i, final int j, final int k)
	{
		return this.V[i][j][k];
	}

	/**
	 * Updates V using a value for a single square
	 * 
	 * @param i int
	 * @param j int
	 * @param K int
	 */
	public void set(final int i, final int j, final int K)
	{
		// Eliminate candidate K from row, column and box:
		for (int a = 0; a < this.N; a++)
		{
			this.V[i][a][K - 1] = false; // Eliminate K from row i
			this.V[a][j][K - 1] = false; // and column j
			this.V[i][j][a] = false; // and eliminate other candidates for (i,j)
		}
		// and from the box (i,j) is in:
		int[] box = Solver.rc2box(i, j);
		for (int a = 0; a < 3; a++)
		{
			for (int b = 0; b < 3; b++)
			{
				this.V[box[0] + a][box[1] + b][K - 1] = false;
			}
		}

		// The derived value K is now also eliminated, so we must restore it:
		this.V[i][j][K - 1] = true;
	}

	/**
	 * Updates V using a grid
	 * 
	 * @param grid int[][]
	 */
	public void update(final int[][] grid)
	{
		this.clear();

		for (int i = 0; i < this.N; i++)
		{
			for (int j = 0; j < this.N; j++)
			{
				if (grid[i][j] != 0)
				{
					this.set(i, j, grid[i][j]);
				}
			}
		}
	}
}