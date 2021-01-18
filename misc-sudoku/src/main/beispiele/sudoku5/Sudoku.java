package sudoku5;

/**
 * Sudoku.java Sudoku generator applet author: M.T.Lagerberg version: 2.00 website: http://mathijs.jurresip.nl copyright: (c) 2006 - Mathijs Lagerberg Classes:
 * - Sudoku Extends Applet. Controls the interface. - SudokuApplic Extends Frame. Enables creation of an applications that contains the applet. - Grid Does all
 * the important work. Creates sudoku's. - GridDrawer Draws the puzzle on a canvas - Solver Tries to solve sudoku's using smart rules - not brute force! -
 * Valids Keeps track of numbers that are valid for each square. Used for solving puzzles and for helping the user. To do: - possibility to load and save games
 * - optional 4 grey blocks - optional 1-9 on the diagonals - let the user pick the colours - add more rules for solving puzzles - create puzzles of difficulty
 * level set by user - enter your own puzzle and let the computer solve it
 */

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

/**
 *
 */
public class Sudoku extends Applet implements MouseListener, ActionListener, ItemListener
{
    /**
     *
     */
    public static final int CREATING_NEW = 3;
    /**
     * Change this to true to have letters A-H instead of numbers 1-9
     */
    public static final boolean OPTION_ALPHA = false;
    /**
     * If you make the puzzles symmetrical, they are generally easier, but much better looking :)
     */
    public static final boolean OPTION_SYMMETRICAL = false;
    /**
     *
     */
    private static final long serialVersionUID = 7008759494629941097L;
    /**
     *
     */
    public static final int SHOW_ERRORS = 2;
    /**
     * Several stati:
     */
    public static final int SHOW_POPUP = 1;
    /**
     *
     */
    Graphics2D bufferGraphics;
    /**
     *
     */
    Image bufferImage;
    /**
     * Interface
     */
    GridDrawer drawer;
    /**
     * The numbers that are given at the start
     */
    int[][] given;
    /**
     * The puzzle
     */
    Grid grid;
    /**
     *
     */
    int height;
    /**
     *
     */
    MenuItem[] items;
    /**
     *
     */
    ResourceBundle language = ResourceBundle.getBundle("sudoku5.LanguageResource");
    /**
     *
     */
    Point mouse = new Point(0, 0);
    /**
     * Dimension of a block (e.g. 3)
     */
    public int n;
    /**
     * Dimension of the puzzle (= n*n)
     */
    public int N;
    /**
     *
     */
    PopupMenu popup;

    /**
     *
     */
    Checkbox showValids;

    /**
     *
     */
    int stateUser = 0;

    /**
     * Numbers filled in by the user (may be wrong)
     */
    int[][] user;

    /**
     * Collections of valid numbers for each square
     */
    Valids V;

    /**
     *
     */
    int width;

    /**
     *
     */
    int X = -1;

    /**
     *
     */
    int Y = -1;

    /**
     * Constructor for applets
     */
    public Sudoku()
    {
        this(false, 400, 430);
    }

    /**
     * Contructor of applications
     *
     * @param isApplic boolean
     * @param w int
     * @param h int
     */
    public Sudoku(final boolean isApplic, final int w, final int h)
    {
        super();

        this.width = w;
        this.height = h;
        this.n = 3;
        this.N = this.n * this.n;

        if (isApplic)
        {
            init();
        }
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e)
    {
        if (e.getSource() instanceof MenuItem)
        {
            menuAction((MenuItem) e.getSource());
        }
        else if (e.getSource() instanceof Button)
        {
            buttonAction((Button) e.getSource());
        }
        else
        {
            System.out.println("Unknown action performed");
        }
    }

    /**
     * @param b {@link Button}
     */
    public void buttonAction(final Button b)
    {
        String s = b.getLabel();

        if (s.equals(this.language.getString("BUTTON_SOLUTION")))
        {
            for (int i = 0; i < this.N; i++)
            {
                for (int j = 0; j < this.N; j++)
                {
                    this.given[i][j] = this.grid.grid[i][j];
                    this.user[i][j] = this.grid.grid[i][j];
                }
            }

            paintSudoku();
        }
        else if (s.equals(this.language.getString("BUTTON_NEW")))
        {
            this.stateUser = Sudoku.CREATING_NEW;
            this.repaint();
        }
        else if (s.equals(this.language.getString("BUTTON_CHECK")))
        {
            this.stateUser = Sudoku.SHOW_ERRORS;
            paintSudoku();
        }
        else if (s.equals(this.language.getString("BUTTON_HELP")))
        {
            // Count the number of empty squares:
            int count = 0;
            int firstI = -1, firstJ = -1;

            for (int i = 0; i < this.N; i++)
            {
                for (int j = 0; j < this.N; j++)
                {
                    if (this.given[i][j] == 0)
                    {
                        count++;
                        if (firstI < 0)
                        {
                            firstI = i;
                            firstJ = j;
                        }
                    }
                }
            }

            // If puzzle is full, we can't give a hint
            if (count == 0)
            {
                return;
            }

            // If >5 squares empty, we can pick them @ random
            int x = -1, y = -1;

            if (count > 5)
            {
                int i = 0;

                while (x < 0)
                {
                    if ((i++) > 50)
                    {
                        break;
                    }

                    x = (int) (Math.random() * this.N);
                    y = (int) (Math.random() * this.N);

                    if ((this.user[x][y] != 0) && (this.user[x][y] == this.grid.grid[x][y]))
                    {
                        x = -1;
                    }
                }
            }

            // If <5 squares empty, we just pick the first (don't waste
            // time endlessly picking already filled-in squares)
            if (x < 0)
            {
                x = firstI;
                y = firstJ;
            }

            this.given[x][y] = this.grid.grid[x][y];
            this.user[x][y] = this.grid.grid[x][y];
            this.V.set(x, y, this.grid.grid[x][y]);
            this.X = x;
            this.Y = y;

            paintSudoku();
        }

    }

    /**
     * Creates a new Sudoku and performs a speed test
     */
    private void createSudoku()
    {
        // reset stuff:
        this.user = new int[this.N][this.N];
        this.V = new Valids(this.n);

        this.grid.createSudoku();
        // Use this to create 100 puzzles and get difficulty statistics: */
        // int tries=100;
        // this.grid.setOutput(false);
        // long t = new Date().getTime();
        // int[] level={0,0,0,0,0};
        // int[] diff =new int[1000];
        // int x;
        // Create a sudoku
        // for(int i=0; i<tries; i++)
        // {
        // this.grid.createSudoku();
        // x=this.grid.getDifficultyLevel();
        // System.out.println(i+") Diff: "+ x);
        // level[x-1]++;
        // if(this.grid.difficulty>=500 && this.grid.difficulty<1500)
        // {
        // diff[this.grid.difficulty-500]++;
        // }
        // else
        // { System.out.println("Difficulty out of 500-1500 range: "+this.grid.difficulty); }
        // }
        // for(int i=0; i<5; i++) System.out.println("level["+i+"] = "+level[i] + " (" +
        // ((level[i]100)/tries) + "%)");
        // for(int i=0; i<1000; i++) if(diff[i]>0)
        // System.out.println((i+500)+") " + diff[i]); long t2 = new Date().getTime();
        // System.out.println("Average time: "+((t2-t)/tries)+" ms");

        // Send the complete grid to the gridDrawer
        this.drawer.setSolution(this.grid.grid);

        // Copy the given numbers to a second matrix
        // array 'given' represents the numbers that were given at the beginning.
        // any numbers the user derives, are put in a second array, 'user', because we
        // want to remember which numbers were given in the beginning.
        this.given = this.grid.given;

        for (int i = 0; i < this.N; i++)
        {
            for (int j = 0; j < this.N; j++)
            {
                this.user[i][j] = this.given[i][j];
            }
        }

        // Keep track of 'valids'
        this.V.update(this.given);
        this.drawer.setValids(this.V);

        // We're done.
        this.stateUser = 0;
    }

    /**
     * @return {@link Container}
     */
    private Container createToolbar()
    {
        Container c = new Container();
        c.setLayout(new FlowLayout());

        Button bSolve = new Button(this.language.getString("BUTTON_SOLUTION"));
        bSolve.addActionListener(this);
        Button bHint = new Button(this.language.getString("BUTTON_HELP"));
        bHint.addActionListener(this);
        Button bNew = new Button(this.language.getString("BUTTON_NEW"));
        bNew.addActionListener(this);
        Button bCheck = new Button(this.language.getString("BUTTON_CHECK"));
        bCheck.addActionListener(this);
        this.showValids = new Checkbox(this.language.getString("TEXT_SHOW_VALIDS"), null, false);
        this.showValids.addItemListener(this);
        this.showValids.setBackground(Color.white);

        c.add(bNew);
        c.add(bSolve);
        c.add(bCheck);
        c.add(bHint);
        c.add(this.showValids);

        return c;
    }

    /**
     * @see java.applet.Applet#init()
     */
    @Override
    public void init()
    {
        // initialize stuff:
        this.grid = new Grid(this.n, this.N);
        this.drawer = new GridDrawer(this.width, this.height);
        this.user = new int[this.N][this.N];
        this.given = new int[this.N][this.N];

        // Set state to 'creating new':
        this.stateUser = Sudoku.CREATING_NEW;

        // Create the popup menu:
        this.popup = new PopupMenu();
        this.items = new MenuItem[this.N + 2];

        for (int i = 0; i < this.N; i++)
        {
            this.items[i] = new MenuItem(Sudoku.OPTION_ALPHA ? String.valueOf((char) (65 + i)) : "" + (i + 1));
            this.items[i].addActionListener(this);
            this.popup.add(this.items[i]);
        }

        this.popup.addSeparator();
        this.items[this.N] = new MenuItem("?");
        this.items[this.N].addActionListener(this);
        this.popup.add(this.items[this.N]);
        this.items[this.N + 1] = new MenuItem(this.language.getString("TEXT_CANCEL"));
        this.items[this.N + 1].addActionListener(this);
        this.popup.add(this.items[this.N + 1]);

        setLayout(new BorderLayout());
        this.add(this.popup);
        addMouseListener(this);

        // Create the toolbar:
        this.add(createToolbar(), BorderLayout.NORTH);

        // this.paintSudoku();
        this.repaint();
    }

    /**
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    @Override
    public void itemStateChanged(final ItemEvent e)
    {
        paintSudoku();
    }

    /**
     *
     */
    private void makeNewBuffer()
    {
        if ((this.bufferImage == null) || (this.bufferGraphics == null))
        {
            this.bufferImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);

            if (this.bufferImage == null)
            {
                System.out.println("bufferImage==null, width: " + getSize().width + ", height: " + getSize().height);
            }

            this.bufferGraphics = (Graphics2D) this.bufferImage.getGraphics();
        }
    }

    /**
     * @param mi {@link MenuItem}
     */
    public void menuAction(final MenuItem mi)
    {
        if (mi.getLabel().equals("?"))
        {
            this.user[this.X][this.Y] = 0;
            this.V.update(this.user);
        }
        else if (mi.getLabel().equals(this.language.getString("TEXT_CANCEL")))
        {
            // .. do nothing
        }
        else
        {
            int i = (Sudoku.OPTION_ALPHA ? Character.getNumericValue(mi.getLabel().charAt(0)) - 9 : Integer.parseInt(mi.getLabel()));
            this.user[this.X][this.Y] = i;
            this.V.set(this.X, this.Y, i);
        }

        paintSudoku();
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(final MouseEvent e)
    {
        int[] r = this.drawer.getGridXY(e.getX(), e.getY());

        if (r != null)
        {
            // User clicked on square (r[0], r[1])
            if (this.given[r[0]][r[1]] == 0)
            {
                this.X = r[0];
                this.Y = r[1];

                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    this.stateUser = Sudoku.SHOW_POPUP;
                    this.mouse.x = e.getX();
                    this.mouse.y = e.getY();
                }
                else
                {
                    this.user[this.X][this.Y] = 0;
                    this.V.update(this.user);
                }
            }
        }
        else
        {
            this.X = -1;
            this.Y = -1;
        }

        paintSudoku();
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(final MouseEvent e)
    {
        // Empty
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(final MouseEvent e)
    {
        // Empty
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(final MouseEvent e)
    {
        int[] r = this.drawer.getGridXY(e.getX(), e.getY());

        if (r != null)
        {
            // User clicked on square (r[0], r[1])
            this.X = r[0];
            this.Y = r[1];
        }
        else
        {
            this.X = -1;
            this.Y = -1;
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(final MouseEvent e)
    {
        // Empty
    }

    /**
     * @see java.awt.Container#paint(java.awt.Graphics)
     */
    @Override
    public void paint(final Graphics g)
    {
        if (this.stateUser == Sudoku.CREATING_NEW)
        {
            g.setColor(Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.black);
            g.drawString(this.language.getString("TEXT_CREATING"), 60, getHeight() / 2);

            this.drawer.paintMessage((Graphics2D) g, this.language.getString("TEXT_CREATING"));
            createSudoku();
            this.stateUser = 0;
            paintSudokuRepaint(false);
            g.drawImage(this.bufferImage, 0, 0, this);
        }
        else
        {
            makeNewBuffer();
            g.drawImage(this.bufferImage, 0, 0, this);
        }

        if (this.stateUser == Sudoku.SHOW_POPUP)
        {
            this.popup.show(this, this.mouse.x, this.mouse.y);
            this.stateUser = 0;
        }
    }

    /**
     *
     */
    private void paintSudoku()
    {
        paintSudokuRepaint(true);
    }

    /**
     * @param rp boolean
     */
    private void paintSudokuRepaint(final boolean rp)
    {
        makeNewBuffer();

        boolean showv = (this.showValids == null ? false : this.showValids.getState());
        this.drawer.paintSudoku(this.bufferGraphics, this.given, this.user, this.X, this.Y, this.stateUser, showv, this.grid.getDifficultyLevel());

        if (rp)
        {
            this.repaint();
        }
    }
}
