package sudoku5;

import java.util.Date;

/**
 * Class Grid manages the creation of puzzles
 */
public class Grid
{
    /**
     *
     */
    public static final int LEVEL_EASY = 2;

    /**
     *
     */
    public static final int LEVEL_HARD = 4;

    /**
     *
     */
    public static final int LEVEL_NORMAL = 3;

    /**
     * Difficulty levels:
     */
    public static final int LEVEL_VERY_EASY = 1;

    /**
     *
     */
    public static final int LEVEL_VERY_HARD = 5;

    /**
     * Available numbers in each box, row and column, used for contructing a valid grid (in function 'create')
     */
    private boolean[][] boxes;

    /**
     *
     */
    private boolean[][] cols;

    /**
     *
     */
    public int countGiven;

    /**
     *
     */
    public int difficulty;

    /**
     * Given numbers:
     */
    public int[][] given;

    /**
     * The Master Grid
     */
    public int[][] grid;

    /**
     * Size of a box (measured in squares) and the puzzle (meas. in boxes).
     */
    private int n;

    /**
     * n*n
     */
    private int N;

    /**
     *
     */
    private boolean output = true;

    /**
     * Help figure out in which box (1..9) coordinates (i,j) are:
     */
    private int[][] rc2box =
    {
            {
                    0, 0, 0, 1, 1, 1, 2, 2, 2
            },
            {
                    0, 0, 0, 1, 1, 1, 2, 2, 2
            },
            {
                    0, 0, 0, 1, 1, 1, 2, 2, 2
            },
            {
                    3, 3, 3, 4, 4, 4, 5, 5, 5
            },
            {
                    3, 3, 3, 4, 4, 4, 5, 5, 5
            },
            {
                    3, 3, 3, 4, 4, 4, 5, 5, 5
            },
            {
                    6, 6, 6, 7, 7, 7, 8, 8, 8
            },
            {
                    6, 6, 6, 7, 7, 7, 8, 8, 8
            },
            {
                    6, 6, 6, 7, 7, 7, 8, 8, 8
            }
    };

    /**
     *
     */
    private boolean[][] rows;

    /**
     * Erstellt ein neues {@link Grid} Object.
     * 
     * @param n int
     * @param N int
     */
    Grid(final int n, final int N)
    {
        super();

        this.n = n;
        this.N = N;
        this.grid = new int[N][N];
        this.given = new int[N][N];
        this.boxes = new boolean[N][N];
        this.rows = new boolean[N][N];
        this.cols = new boolean[N][N];
    }

    /**
     * 
     */
    private void addRandomGiven()
    {
        int i = (int) (Math.random() * this.N);
        int j = (int) (Math.random() * this.N);

        while (this.given[i][j] != 0)
        {
            i = (int) (Math.random() * this.N);
            j = (int) (Math.random() * this.N);
        }

        this.given[i][j] = this.grid[i][j];
        this.countGiven++;

        if (Sudoku.OPTION_SYMMETRICAL)
        {
            this.given[j][this.N - i - 1] = this.grid[j][this.N - i - 1];
            this.given[this.N - i - 1][this.N - j - 1] = this.grid[this.N - i - 1][this.N - j - 1];
            this.given[this.N - j - 1][i] = this.grid[this.N - j - 1][i];
            this.given[j][i] = this.grid[j][i];
            this.given[i][this.N - j - 1] = this.grid[i][this.N - j - 1];
            this.given[this.N - i - 1][j] = this.grid[this.N - i - 1][j];
            this.given[this.N - j - 1][this.N - i - 1] = this.grid[this.N - j - 1][this.N - i - 1];
            this.countGiven += 7;
        }
    }

    /*** Helper functions ***/
    /**
     * Cleans all grids and prepare them for generating a new puzzle
     */
    private void cleanGrid()
    {
        for (int i = 0; i < this.N; i++)
        {
            for (int j = 0; j < this.N; j++)
            {
                this.grid[i][j] = 0;
                this.boxes[i][j] = true;
                this.rows[i][j] = true;
                this.cols[i][j] = true;
                this.given[i][j] = 0;
            }
        }
    }

    /**
     * Fill the grid with numbers, with regards to the rules of the game:
     * 
     * @param grid int[][]
     * @param boxes boolean[][]
     * @param rows boolean[][]
     * @param cols boolean[][]
     * @param level int
     * @return boolean
     */
    public boolean create(final int[][] grid, final boolean[][] boxes, final boolean[][] rows, final boolean[][] cols, final int level)
    {
        boolean validFound;
        boolean emptySquare = false;
        boolean _box, _row, _col;

        // Make sure the grid is realllllyyy random!
        int[] kList = permutateList();

        // For each row i...
        for (int i = 0; i < this.N; i++)
        {
            // ... and each column j...
            for (int j = 0; j < this.N; j++)
            {
                if (grid[i][j] == 0)
                {
                    emptySquare = true;
                    validFound = false;

                    // ... and for each value 1-9...
                    for (int k = 0; k < this.N; k++)
                    {
                        _box = boxes[this.rc2box[i][j]][kList[k]];
                        _row = rows[i][kList[k]];
                        _col = cols[j][kList[k]];

                        // ...if k is a valid value for grid[i,j]...
                        if (_box && _row && _col)
                        {
                            // ...fill it in...
                            boxes[this.rc2box[i][j]][kList[k]] = false;
                            rows[i][kList[k]] = false;
                            cols[j][kList[k]] = false;
                            grid[i][j] = kList[k] + 1;

                            // ...and try to fill the rest of the grid, recursively
                            if (create(grid, boxes, rows, cols, level + 1))
                            {
                                return true;
                            }

                            grid[i][j] = 0;
                            boxes[this.rc2box[i][j]][kList[k]] = _box;
                            rows[i][kList[k]] = _row;
                            cols[j][kList[k]] = _col;
                        }
                    }

                    if (!validFound)
                    {
                        // Puzzle is invalid. Backtrack and try again with different numbers.
                        return false;
                    }
                }
            }
        }

        if (!emptySquare)
        {
            // We're done!
            this.grid = grid;

            return true;
        }

        return false;
    }

    /**
     * 
     */
    public void createSudoku()
    {
        // Start the timer
        long t = new Date().getTime();
        this.difficulty = 0;

        // Fill a grid with numbers:
        cleanGrid();
        create(this.grid, this.boxes, this.rows, this.cols, 0);

        // Keep track of the best grid we've found:
        int[][] best = new int[this.N][this.N];
        int bestDifficulty = 0;
        int bestGiven = (this.N * this.N) + 1;
        int bestTry = 0;

        int tries = 0;

        // Generate 100 puzzles and pick the most difficult one
        while (tries < 100)
        {
            this.countGiven = 0;
            this.difficulty = 0;

            // Clean the given numbers, but keep the grid intact
            for (int i = 0; i < this.N; i++)
            {
                for (int j = 0; j < this.N; j++)
                {
                    this.given[i][j] = 0;
                }
            }

            // Select 17 random numbers to show
            // 17 is an absolute minimum. Any less and the puzzle is garanteed to be unsolvable.
            randomGiven(17);
            this.difficulty = solvable();

            // Keep adding numbers until the puzzle is solvable
            while (this.difficulty == 0)
            {
                addRandomGiven();
                this.difficulty = solvable();
            }

            if (this.countGiven <= bestGiven)
            {
                // If this puzzle is better then anything we've found so far, store it
                bestGiven = this.countGiven;
                bestDifficulty = this.difficulty;
                bestTry = tries;

                for (int i = 0; i < this.N; i++)
                {
                    for (int j = 0; j < this.N; j++)
                    {
                        best[i][j] = this.given[i][j];
                    }
                }

                if ((bestGiven < 35) && (bestDifficulty > 960))
                {
                    break; // Good enough. Stop searching.
                }
            }

            tries++;
        }

        // Restore the best grid:
        this.countGiven = bestGiven;
        this.difficulty = bestDifficulty;

        if (this.output)
        {
            System.out.println("Puzzle " + bestTry + " is best");
        }

        for (int i = 0; i < this.N; i++)
        {
            for (int j = 0; j < this.N; j++)
            {
                this.given[i][j] = best[i][j];
            }
        }

        if (this.output)
        {
            long t2 = new Date().getTime();

            System.out.println("Puzzle created in " + (t2 - t) + " ms,\n" + "with " + this.difficulty + " difficulty points\n" + "and " + this.countGiven
                    + " given numbers.");
        }
    }

    /**
     * Returns the difficulty level (1...5) based on the difficulty points (roughly 500...1500)
     * 
     * @return int
     */
    public int getDifficultyLevel()
    {
        // 0... 515 = very easy
        // 515... 570 = easy
        // 570... 960 = normal
        // 960...1200 = hard
        // 1200... = very hard
        if (this.difficulty < 515)
        {
            return Grid.LEVEL_VERY_EASY;
        }

        if (this.difficulty < 570)
        {
            return Grid.LEVEL_EASY;
        }

        if (this.difficulty < 960)
        {
            return Grid.LEVEL_NORMAL;
        }

        if (this.difficulty < 1200)
        {
            return Grid.LEVEL_HARD;
        }

        return Grid.LEVEL_VERY_HARD;
    }

    /**
     * Creates a random permutation of {1,2,...,N}
     * 
     * @return int[]
     */
    private int[] permutateList()
    {
        int[] a = new int[this.N];

        for (int i = 0; i < this.N; i++)
        {
            a[i] = i;
        }

        for (int i = 0; i < this.N; i++)
        {
            int r = (int) (Math.random() * this.N);
            int swap = a[r];
            a[r] = a[i];
            a[i] = swap;
        }

        return a;
    }

    // Debug functions

    /**
     * 
     */
    @SuppressWarnings("unused")
    private void printGrid()
    {
        this.printGrid(this.grid);
    }

    /**
     * @param g int[][]
     */
    private void printGrid(final int[][] g)
    {
        if (g == null)
        {
            System.out.println("Grid == null!");
        }

        for (int i = 0; i < this.n; i++)
        {
            System.out.println("+-----+-----+-----+");

            for (int j = 0; j < this.n; j++)
            {
                System.out.print("|");

                for (int k = 0; k < this.n; k++)
                {
                    int r = (i * this.n) + j;
                    int c = k * this.n;

                    System.out.print((g[r][c] != 0 ? "" + g[r][c] : ".") + " " + (g[r][c + 1] != 0 ? "" + g[r][c + 1] : ".") + " "
                            + (g[r][c + 2] != 0 ? "" + g[r][c + 2] : ".") + "|");
                }

                System.out.print("\n");
            }
        }

        System.out.println("+-----+-----+-----+");
    }

    /**
     * randomGiven(x) means: exactly x numbers are shown. The lower this number, the more difficult the puzzle. At least 17 numbers need to be shown for any
     * puzzle to be solvable.
     * 
     * @param showHowMany int
     */
    private void randomGiven(final int showHowMany)
    {
        // Erase all given numbers, of previous tries.
        for (int i = 0; i < this.N; i++)
        {
            for (int j = 0; j < this.N; j++)
            {
                this.given[i][j] = 0;
            }
        }

        while (this.countGiven < showHowMany)
        {
            addRandomGiven();
        }
    }

    /**
     * @param o boolean
     */
    public void setOutput(final boolean o)
    {
        this.output = o;
    }

    /**
     * Starts a Solver to check if the generated puzzle is solvable
     * 
     * @return int
     */
    private int solvable()
    {
        // returns difficulty if puzzle is solvable, or 0 otherwise
        return new Solver().solve(this.N, this.given);
    }
}