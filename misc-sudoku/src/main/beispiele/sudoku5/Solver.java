package sudoku5;

/**
 * Check if it can solve a puzzle using rules of logic The solutions it finds are always unique.
 */
class Solver
{
    /**
     *
     */
    private static final int WEIGHT_METHOD1_HIT = 3;

    /**
     *
     */
    private static final int WEIGHT_METHOD1_LOOP = 0;

    /**
     *
     */
    private static final int WEIGHT_METHOD2_HIT = 2;

    /**
     *
     */
    private static final int WEIGHT_METHOD2_LOOP = 1;

    /**
     * Returns the coordinates of the top-left square of the box square(i,j) is in.
     *
     * @param i int
     * @param j int
     * @return int[]
     */
    public static int[] rc2box(final int i, final int j)
    {
        int[] r =
        {
                6, 6
        };

        if (i < 6)
        {
            r[0] = 3;

            if (i < 3)
            {
                r[0] = 0;
            }
        }

        if (j < 6)
        {
            r[1] = 3;

            if (j < 3)
            {
                r[1] = 0;
            }
        }

        return r;
    }

    /**
     *
     */
    int difficulty;

    /**
     * The puzzle
     */
    int[][] grid;

    /**
     * Used for optimization of the basic rules algorithm:
     */
    private boolean[][] isChecked;

    /**
     * Dimension of the puzzle (usually N=9)
     */
    int N;

    /**
     * List of candidates for each square
     */
    Valids V;

    /**
     * Constructor Erstellt ein neues {@link Solver} Object.
     */
    Solver()
    {
        super();
    }

    /**
     * Returns the number of known values in the grid (usefull for checking wether the grid has changed after an operation).
     *
     * @return int
     */
    private int countKnown()
    {
        int r = 0;

        for (int i = 0; i < this.N; i++)
        {
            for (int j = 0; j < this.N; j++)
            {
                if (this.grid[i][j] != 0)
                {
                    r++;
                }
            }
        }

        return r;
    }

    /**
     * When a certain value K is derived for a square (i,j), pass it to this function and grid and V will both be updated to the new situation.
     *
     * @param i int
     * @param j int
     * @param K int
     */
    private void fillSquare(final int i, final int j, final int K)
    {
        // Eliminate candidate K from row, column and box:
        this.V.set(i, j, K);
        // Put K on place (i,j) in the grid:
        this.grid[i][j] = K;
        // Note that (i,j) has been filled and does
        // not have to be checked again. (optimization)
        this.isChecked[i][j] = true;
    }

    /**
     * @param N int
     * @param g int[][]
     * @return int
     */
    public int solve(final int N, final int[][] g)
    {
        // Grid is a puzzle containing given numbers.
        // this method should check it for a unique solution.
        // If such a solutions is found, return an integer indicating the
        // difficulty of the puzzle. If multiple solutions are possible, or
        // if the algorithm is not smart enough to solve it, return 0.

        // copy the given grid, because we don't want to mess up the original
        this.grid = new int[N][N];

        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
            {
                this.grid[i][j] = g[i][j];
            }
        }

        this.N = N;
        this.V = new Valids(3);
        this.isChecked = new boolean[N][N];
        this.difficulty = 1;

        // Filter out the given numbers:
        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
            {
                if (this.grid[i][j] != 0)
                {
                    fillSquare(i, j, this.grid[i][j]);
                }
            }
        }

        // Count and countOld are used to determine if there are still new values
        // found. If, after a loop, countOld==count, no new values have been found
        // and looping any further is useless.
        int count = countKnown();
        int countOld = -1;
        int iter = 0;

        while ((count < 81) && (count != countOld))
        {
            iter++;

            // Method #1
            // Fill in all squares for which there is only 1 candidate left:
            countOld = count;

            for (int i = 0; i < N; i++)
            {
                for (int j = 0; j < N; j++)
                {
                    if (!this.isChecked[i][j])
                    {
                        int[] r = this.V.countCandidates(i, j); // r[0] = number of candidates, r[1]
                        // =
                        // one of those candidates
                        if (r[0] == 1)
                        {
                            this.difficulty += Solver.WEIGHT_METHOD1_HIT;
                            fillSquare(i, j, r[1] + 1);
                        }
                        else
                        {
                            this.difficulty += Solver.WEIGHT_METHOD1_LOOP;
                        }
                    }
                }
            }

            // Method #2 part a
            // Fill in numbers for which there is only 1 valid square in a ROW left:

            // For each row...
            for (int i = 0; i < N; i++)
            {
                // ...for each number...
                for (int k = 0; k < N; k++)
                {
                    int r = 0, rj = -1;

                    // ...for each square in the row...
                    for (int j = 0; j < N; j++)
                    {
                        // ...count the number of options
                        if (this.V.get(i, j, k))
                        {
                            r++;
                            rj = j;
                        }
                    }

                    if (r == 1)
                    {
                        // ...and if there's only one, we got one step further to
                        // a solution.
                        this.difficulty += Solver.WEIGHT_METHOD2_HIT;
                        fillSquare(i, rj, k + 1);
                    }
                    else
                    {
                        this.difficulty += Solver.WEIGHT_METHOD2_LOOP;
                    }
                }
            }
            // Method #2 part b
            // Fill in numbers for which there is only 1 valid square in a COLUMN left:

            // For each column...
            for (int j = 0; j < N; j++)
            {
                // ...for each number...
                for (int k = 0; k < N; k++)
                {
                    int r = 0, ri = -1;

                    // ...for each square in the row...
                    for (int i = 0; i < N; i++)
                    {
                        // ...count the number of options
                        if (this.V.get(i, j, k))
                        {
                            r++;
                            ri = i;
                        }
                    }

                    if (r == 1)
                    {
                        // ...and if there's only one, we got one step further to
                        // a solution.
                        this.difficulty += Solver.WEIGHT_METHOD2_HIT;
                        fillSquare(ri, j, k + 1);
                    }
                    else
                    {
                        this.difficulty += Solver.WEIGHT_METHOD2_LOOP;
                    }
                }
            }

            // Method #2 part c
            // Fill in numbers for which there is only 1 valid square in a BOX left:

            // For each box...
            for (int i = 0; i < 3; i++)
            {
                for (int j = 0; j < 3; j++)
                {
                    // ...for each number...
                    for (int k = 0; k < N; k++)
                    {
                        int r = 0, ri = -1, rj = -1;

                        // ...for each square in the box...
                        for (int l = 0; l < 3; l++)
                        {
                            for (int m = 0; m < 3; m++)
                            {
                                // ...count the number of options
                                if (this.V.get((i * 3) + l, (j * 3) + m, k))
                                {
                                    r++;
                                    ri = (i * 3) + l;
                                    rj = (j * 3) + m;
                                }
                            }
                        }

                        if (r == 1)
                        {
                            // ...and if there's only one, we got one step further to
                            // a solution.
                            this.difficulty += Solver.WEIGHT_METHOD2_HIT;
                            fillSquare(ri, rj, k + 1);
                        }
                        else
                        {
                            this.difficulty += Solver.WEIGHT_METHOD2_LOOP;
                        }
                    }
                }
            }

            this.difficulty += 3;
            count = countKnown();
        }

        // To do:
        // try other methods, like:
        // "if n squares in a region each have only n
        // possible candidates, and those candidates
        // are the same, then those candidates can be
        // eliminated from the other squares in that region."

        if (count == 81)
        {
            return this.difficulty;
        }

        return 0;
    }
}