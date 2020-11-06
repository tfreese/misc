package de.freese.sonstiges.print;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * Beispielklasse fuer Druck und Druckvorschau
 *
 * @author Thomas Freese
 */
class PrintPreview extends JPanel implements Printable, ActionListener
{
    /**
     *
     */
    private static final Color COLOT_BACKGROUND = Color.darkGray;

    /**
     *
     */
    private static final Color COLOR_FOREGROUND = Color.black;

    /**
     *
     */
    private static final Color COLOR_FRAME = Color.lightGray;

    /**
     *
     */
    private static final Color COLOR_PAPER = Color.white;

    /**
     *
     */
    private static final int BORDER_SIZE = 50;

    /**
     *
     */
    private static final String LABEL_MENU_ENTER_TEXT = "Text eingeben";

    /**
     *
     */
    private static final String LABEL_MENU_EXIT = "Beenden";

    /**
     *
     */
    private static final String LABEL_MENU_PAGE_LAYOUT = "Seite einrichten";

    /**
     *
     */
    private static final String LABEL_MENU_PRINT = "Drucken";

    /**
     *
     */
    private static final String LABEL_MENU_PRINTER = "Drucker einrichten";

    /**
     *
     */
    private static final String LABEL_MENU_ZOOM_IN = "Vergroessern";

    /**
     *
     */
    private static final String LABEL_MENU_ZOOM_OUT = "Verkleinern";

    /**
     *
     */
    private static final long serialVersionUID = -2189370102458478566L;

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        JFrame frame = new JFrame("Einfaches Druckbeispiel");
        PrintPreview printPreview = new PrintPreview();

        WindowListener l = new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent e)
            {
                System.exit(0);
            }
        };

        frame.addWindowListener(l);

        frame.getContentPane().add(printPreview);

        JMenuItem menuItem;
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menue");

        menuItem = new JMenuItem(LABEL_MENU_ENTER_TEXT);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menu.add(new JSeparator());

        menuItem = new JMenuItem(LABEL_MENU_ZOOM_IN);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menuItem = new JMenuItem(LABEL_MENU_ZOOM_OUT);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menu.add(new JSeparator());

        menuItem = new JMenuItem(LABEL_MENU_PAGE_LAYOUT);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menuItem = new JMenuItem(LABEL_MENU_PRINTER);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menuItem = new JMenuItem(LABEL_MENU_PRINT);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menu.add(new JSeparator());

        menuItem = new JMenuItem(LABEL_MENU_EXIT);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menuBar.add(menu);

        frame.setJMenuBar(menuBar);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     *
     */
    private transient Image imgCup;

    /**
     *
     */
    private transient Image imgDuke;

    /**
     *
     */
    private double mdPreviewScale = 0.5D;

    /**
     *
     */
    private String mstrText = "Drucken mit Java 2";

    /**
     *
     */
    private transient PageFormat pageFormat;

    /**
     *
     */
    private transient PrinterJob printerJob;

    /**
     * Erstellt ein neues {@link PrintPreview} Object.
     */
    public PrintPreview()
    {
        super();

        // Druckereinstellungen und Seitenlayout initialisieren
        this.printerJob = PrinterJob.getPrinterJob();
        this.pageFormat = this.printerJob.defaultPage();

        // Hintergrundfarbe des Panels einstellen
        setBackground(COLOT_BACKGROUND);

        // "Wunschgroesse" fuer das Panel berechnen
        setPreferredSize(new Dimension((int) ((this.pageFormat.getWidth() + (2 * BORDER_SIZE)) * this.mdPreviewScale),
                (int) ((this.pageFormat.getHeight() + (2 * BORDER_SIZE)) * this.mdPreviewScale)));

        // Grafiken laden
        this.imgDuke = getToolkit().getImage("resources/WavingDuke.gif");
        this.imgCup = getToolkit().getImage("resources/JavaCup.gif");
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent event)
    {
        if (event.getSource() instanceof JMenuItem)
        {
            if (event.getActionCommand().equals(LABEL_MENU_PRINT))
            {
                // Ausdruck starten
                print();
            }
            else if (event.getActionCommand().equals(LABEL_MENU_PAGE_LAYOUT))
            {
                // Seitenlayoutdialog anzeigen
                this.pageFormat = this.printerJob.pageDialog(this.pageFormat);
                repaint();
            }
            else if (event.getActionCommand().equals(LABEL_MENU_PRINTER))
            {
                // Druckerauswahldialog anzeigen
                if (this.printerJob.printDialog())
                {
                    this.pageFormat = this.printerJob.validatePage(this.pageFormat);
                    repaint();
                }
            }
            else if (event.getActionCommand().equals(LABEL_MENU_ZOOM_IN))
            {
                // neuen Zoomfaktor berechnen
                if (this.mdPreviewScale < 2)
                {
                    this.mdPreviewScale *= 2;
                    repaint();
                }
            }
            else if (event.getActionCommand().equals(LABEL_MENU_ZOOM_OUT))
            {
                // neuen Zoomfaktor berechnen
                if (this.mdPreviewScale > 0.25)
                {
                    this.mdPreviewScale /= 2;
                    repaint();
                }
            }
            else if (event.getActionCommand().equals(LABEL_MENU_ENTER_TEXT))
            {
                // Texteingabe machen
                enterText();
            }
            else if (event.getActionCommand().equals(LABEL_MENU_EXIT))
            {
                // Programm beenden
                System.exit(0);
            }
        }
    }

    /**
     * @param g2 {@link Graphics2D}
     */
    private void drawMyGraphics(final Graphics2D g2)
    {
        // Schriftfarbe einstellen
        g2.setPaint(COLOR_FOREGROUND);

        // Text mit Schriftgroesse 30 ausgeben
        g2.setFont(g2.getFont().deriveFont(30F));
        g2.drawString(this.mstrText, 20, 40);

        // Bilder zeichnen
        g2.drawImage(this.imgDuke, 10, 100, this);
        g2.drawImage(this.imgCup, 100, 200, this);
    }

    /**
     *
     */
    private void enterText()
    {
        // Eingabedialog erzeugen und starten
        Object userInput = JOptionPane.showInputDialog(null, "Bitte einen Text eingeben", "Drucktext", JOptionPane.PLAIN_MESSAGE, null, null, this.mstrText);

        // wenn Eingabe OK, Text uebernehmen
        if (userInput instanceof String)
        {
            this.mstrText = (String) userInput;
            repaint(); // neu zeichnen
        }
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(final Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Grafik auf den gewuenschten Massstab skalieren
        g2.scale(this.mdPreviewScale, this.mdPreviewScale);

        // "Papier" zeichnen
        g2.setPaint(COLOR_PAPER);
        g2.fillRect(BORDER_SIZE, BORDER_SIZE, (int) this.pageFormat.getWidth(), (int) this.pageFormat.getHeight());

        // Ursprung auf die Papierkante legen
        g2.translate(BORDER_SIZE, BORDER_SIZE);

        // Randlinien fuer den bedruckbaren Bereich einzeichnen
        g2.setPaint(COLOR_FRAME);

        g2.drawLine(0, (int) this.pageFormat.getImageableY() - 1, (int) this.pageFormat.getWidth() - 1, (int) this.pageFormat.getImageableY() - 1);
        g2.drawLine(0, (int) (this.pageFormat.getImageableY() + this.pageFormat.getImageableHeight()), (int) this.pageFormat.getWidth() - 1,
                (int) (this.pageFormat.getImageableY() + this.pageFormat.getImageableHeight()));
        g2.drawLine((int) this.pageFormat.getImageableX() - 1, 0, (int) this.pageFormat.getImageableX() - 1, (int) this.pageFormat.getHeight() - 1);
        g2.drawLine((int) (this.pageFormat.getImageableX() + this.pageFormat.getImageableWidth()), 0,
                (int) (this.pageFormat.getImageableX() + this.pageFormat.getImageableWidth()), (int) this.pageFormat.getHeight() - 1);

        // Ursprung zum 2.mal verschieben. Achtung: translate() arbeitet inkrementell!
        g2.translate(this.pageFormat.getImageableX(), this.pageFormat.getImageableY());

        // Ausgabebereich auf den druckbaren Bereich einschraenken
        g2.setClip(0, 0, (int) this.pageFormat.getImageableWidth(), (int) this.pageFormat.getImageableHeight());

        // Grafik ausgeben die auf dem Drucker und in der Vorschau angezeigt werden soll
        drawMyGraphics(g2);
    }

    /**
     * Druckausgabe starten
     */
    public void print()
    {
        // Standardseitenformat holen und auf Querformat stellen
        PageFormat pfLandscape = this.printerJob.defaultPage();
        pfLandscape.setOrientation(PageFormat.LANDSCAPE);

        // Ein Buch erzeugen
        Book book = new Book();

        // Deckblatt hinzufuegen. Feste Seitenanzahl, daher kann die Indexabfrage
        // in der print(...)-Methode von CoverPage entfallen
        book.append(new CoverPage(), pfLandscape, 1);

        // Grafik aus Vorschau hinzufuegen
        book.append(this, this.pageFormat);

        // das Buch dem Druckauftrag uebergeben
        this.printerJob.setPageable(book);

        try
        {
            this.printerJob.print();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Ausgabe auf dem Drucker machen
     *
     * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
     */
    @Override
    public int print(final Graphics g, final PageFormat pageFormat, final int pageIndex) throws PrinterException
    {
        int printState = Printable.NO_SUCH_PAGE;

        // Achtung! iPageIndex == 0 ist die CoverPage!
        if (pageIndex == 1)
        {
            Graphics2D g2 = (Graphics2D) g;

            // Ursprung verschieben und Ausgabebereich eingrenzen
            g2.translate((int) this.pageFormat.getImageableX(), (int) this.pageFormat.getImageableY());
            g2.setClip(0, 0, (int) this.pageFormat.getImageableWidth(), (int) this.pageFormat.getImageableHeight());

            // Grafik ausgeben
            drawMyGraphics(g2);

            printState = Printable.PAGE_EXISTS;
        }

        return printState;
    }
}
