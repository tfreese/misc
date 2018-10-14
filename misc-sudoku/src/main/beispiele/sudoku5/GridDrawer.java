package sudoku5;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * GridDrawer does all the painting that need to be done.
 */
public class GridDrawer
{
    /**
     *
     */
    private static final Color CLR_BACKGROUND = Color.white;

    /**
     *
     */
    private static final Color CLR_CORRECT = new Color(200, 255, 200);

    /**
     *
     */
    private static final Color CLR_GIVEN = new Color(220, 220, 220);

    /**
     *
     */
    private static final Color CLR_GRID = Color.BLACK;

    /**
     *
     */
    private static final Color CLR_LEVEL = new Color(120, 120, 120);

    /**
     *
     */
    private static final Color CLR_MISTAKE = new Color(255, 200, 200);

    /**
     *
     */
    private static final Color CLR_SELECTED = new Color(255, 128, 0);

    /**
     *
     */
    private static final Color CLR_TEXT = Color.black;

    /**
     *
     */
    private static final Font FNT_GIVEN = new Font("SanSerif", Font.BOLD, 20);

    /**
     *
     */
    private static final Font FNT_NORMAL = new Font("SanSerif", Font.PLAIN, 20);

    /**
     *
     */
    private static final Font FNT_SMALL = new Font("SanSerif", Font.PLAIN, 9);

    /**
     *
     */
    private static final boolean OPTION_DRAW_OVAL = true;

    /**
     *
     */
    private Dimension dim;

    /**
     *
     */
    private int[][] gridSolution;

    /**
     *
     */
    private int squareHeight = 20;

    /**
     *
     */
    private int squareWidth = 20;

    /**
     *
     */
    private Valids V;

    /**
     *
     */
    private int xOffset = 50;

    /**
     *
     */
    private int yOffset = 70;

    /**
     * Erstellt ein neues {@link GridDrawer} Object.
     * 
     * @param w int
     * @param h int
     */
    GridDrawer(final int w, final int h)
    {
        this.dim = new Dimension(w, h);
        this.squareWidth = Math.min((w - (2 * this.xOffset)) / 9, (h - this.xOffset - this.yOffset) / 9);
        this.squareHeight = this.squareWidth;
    }

    /**
     * Erstellt ein neues {@link GridDrawer} Object.
     * 
     * @param gridSol int[][]
     * @param w int
     * @param h int
     */
    GridDrawer(final int[][] gridSol, final int w, final int h)
    {
        this(w, h);
        this.setSolution(gridSol);
    }

    /**
     * @param x int
     * @param y int
     * @return int[]
     */
    public int[] getGridXY(int x, int y)
    {
        if ((x < this.xOffset) || (y < this.yOffset))
        {
            return null;
        }

        x -= this.xOffset;
        y -= this.yOffset;
        x = x / this.squareWidth;
        y = y / this.squareHeight;

        if ((x > 8) || (y > 8))
        {
            return null;
        }

        int[] r =
        {
                x, y
        };

        return r;
    }

    /**
     * @param g {@link Graphics2D}
     * @param message String
     */
    public void paintMessage(final Graphics2D g, final String message)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(GridDrawer.CLR_BACKGROUND);
        g.fillRect(0, 0, (int) this.dim.getWidth(), (int) this.dim.getHeight());
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(GridDrawer.CLR_TEXT);
        g.drawString(message, 100, (int) this.dim.getHeight() / 2);
    }

    /**
     * @param g {@link Graphics2D}
     * @param grid int[][]
     * @param user int[][]
     * @param x int
     * @param y int
     * @param state int
     * @param showValids boolean
     * @param difficulty int
     */
    public void paintSudoku(final Graphics2D g, final int[][] grid, final int[][] user, final int x, final int y, final int state, final boolean showValids,
                            final int difficulty)
    {
        int N = 9;
        int n = 3;
        int txtXOffset = 11;
        int txtYOffset = 25;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(GridDrawer.CLR_BACKGROUND);
        g.fillRect(0, 0, (int) this.dim.getWidth(), (int) this.dim.getHeight());

        // Highlight selected square:
        if ((x >= 0) && (y >= 0))
        {
            g.setColor(GridDrawer.CLR_SELECTED);
            g.fillRect(this.xOffset + (x * this.squareWidth), this.yOffset + (y * this.squareHeight), this.squareWidth, this.squareHeight);
        }
        g.setColor(Color.black);

        // Draw the numbers and coloured squares:
        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < N; j++)
            {
                // Show right and wrong numbers
                if (state == Sudoku.SHOW_ERRORS)
                {
                    if (user[i][j] != 0)
                    {
                        if (user[i][j] != this.gridSolution[i][j])
                        {
                            g.setColor(GridDrawer.CLR_MISTAKE);
                        }
                        else
                        {
                            g.setColor(GridDrawer.CLR_CORRECT);
                        }
                        g.fillRect(this.xOffset + (i * this.squareWidth), this.yOffset + (j * this.squareHeight), this.squareWidth, this.squareHeight);
                    }

                }
                // Show given number
                if (grid[i][j] != 0)
                {
                    g.setFont(GridDrawer.FNT_GIVEN);
                    g.setColor(GridDrawer.CLR_GIVEN);
                    if (GridDrawer.OPTION_DRAW_OVAL)
                    {
                        g.fillOval(this.xOffset + (i * this.squareWidth) + 1, this.yOffset + (j * this.squareHeight) + 1, this.squareWidth - 2,
                                this.squareHeight - 2);
                    }
                    else
                    {
                        g.fillRect(this.xOffset + (i * this.squareWidth), this.yOffset + (j * this.squareHeight), this.squareWidth, this.squareHeight);
                    }
                    g.setColor(GridDrawer.CLR_TEXT);
                    g.drawString((Sudoku.OPTION_ALPHA ? String.valueOf((char) (64 + grid[i][j])) : "" + grid[i][j]), this.xOffset + (i * this.squareWidth)
                            + txtXOffset, this.yOffset + (j * this.squareHeight) + txtYOffset);
                }
                else
                {
                    if (user[i][j] != 0)
                    {
                        g.setFont(GridDrawer.FNT_NORMAL);
                        g.setColor(GridDrawer.CLR_TEXT);
                        g.drawString((Sudoku.OPTION_ALPHA ? String.valueOf((char) (64 + user[i][j])) : "" + user[i][j]), this.xOffset + (i * this.squareWidth)
                                + txtXOffset, this.yOffset + (j * this.squareHeight) + txtYOffset);
                    }
                }
            }
        }

        // Show the little numbers in the corners if the user selected "Toon mogelijkheden":
        g.setColor(GridDrawer.CLR_TEXT);
        if (showValids && (this.V != null))
        {
            g.setFont(GridDrawer.FNT_SMALL);
            // For each square (i,j)
            for (int i = 0; i < N; i++)
            {
                for (int j = 0; j < N; j++)
                {
                    // If square is still empty
                    if (user[i][j] == 0)
                    {
                        // for each value 1..9
                        for (int k = 0; k < N; k++)
                        {
                            // If value is valid
                            if (this.V.get(i, j, k))
                            {
                                // Print it!
                                int a = (k < 3 ? 0 : (k < 6 ? 1 : 2));
                                int b = k % 3;
                                g.drawString("" + (k + 1), this.xOffset + (i * this.squareWidth) + ((11 * b) + 2), this.yOffset + (j * this.squareHeight)
                                        + ((11 * a) + 9));
                            }
                        }
                    }
                }
            }
        }

        // Draw the grid itself:
        g.setColor(GridDrawer.CLR_GRID);
        g.drawRect(this.xOffset - 3, this.yOffset - 3, (this.squareWidth * N) + 6, (this.squareHeight * N) + 6);
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                g.drawRect((this.xOffset - 1) + (this.squareWidth * n * i), (this.yOffset - 1) + (this.squareHeight * n * j), (this.squareWidth * n) + 2,
                        (this.squareHeight * n) + 2);
            }
        }
        for (int i = 0; i <= N; i++)
        {
            g.drawLine(this.xOffset, this.yOffset + (this.squareHeight * i), this.xOffset + (this.squareWidth * N), this.yOffset + (this.squareHeight * i));
            g.drawLine(this.xOffset + (this.squareWidth * i), this.yOffset, this.xOffset + (this.squareWidth * i), this.yOffset + (this.squareHeight * N));
        }

        // Show difficulty:
        for (int i = 0; i < 5; i++)
        {
            if (i < difficulty)
            {
                g.setColor(GridDrawer.CLR_LEVEL);
                g.fillOval(this.xOffset + (i * 10), this.yOffset - 15, 10, 10);
                g.setColor(GridDrawer.CLR_GRID);
            }
            g.drawOval(this.xOffset + (i * 10), this.yOffset - 15, 10, 10);
        }

        // Copyright
        g.setFont(GridDrawer.FNT_SMALL);
        g.drawString("Copyright © 2006 - Mathijs Lagerberg - http://mathijs.jurresip.nl", 5, (int) (this.dim.getHeight()) - 40);
    }

    /**
     * @param grid int[][]
     */
    public void setSolution(final int[][] grid)
    {
        this.gridSolution = grid;
    }

    /**
     * @param v {@link Valids}
     */
    public void setValids(final Valids v)
    {
        this.V = v;
    }
}