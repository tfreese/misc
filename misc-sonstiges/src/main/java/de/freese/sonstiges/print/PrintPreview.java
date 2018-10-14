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
    private static final long serialVersionUID = -2189370102458478566L;

    /**
     *
     */
    private static final Color colBackground = Color.darkGray;

    /**
     *
     */
    private static final Color colFrame = Color.lightGray;

    /**
     *
     */
    private static final Color colPaper = Color.white;

    /**
     *
     */
    private static final Color colForeground = Color.black;

    /**
     *
     */
    private static final String mstrLabelMnuEnterText = "Text eingeben";

    /**
     *
     */
    private static final String mstrLabelMnuPrint = "Drucken";

    /**
     *
     */
    private static final String mstrLabelMnuPageLayout = "Seite einrichten";

    /**
     *
     */
    private static final String mstrLabelMnuPrinter = "Drucker einrichten";

    /**
     *
     */
    private static final String mstrLabelMnuZoomIn = "Vergroessern";

    /**
     *
     */
    private static final String mstrLabelMnuZoomOut = "Verkleinern";

    /**
     *
     */
    private static final String mstrLabelMnuExit = "Beenden";

    /**
     *
     */
    private static final int miBorderSize = 50;

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

        menuItem = new JMenuItem(mstrLabelMnuEnterText);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menu.add(new JSeparator());

        menuItem = new JMenuItem(mstrLabelMnuZoomIn);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menuItem = new JMenuItem(mstrLabelMnuZoomOut);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menu.add(new JSeparator());

        menuItem = new JMenuItem(mstrLabelMnuPageLayout);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menuItem = new JMenuItem(mstrLabelMnuPrinter);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menuItem = new JMenuItem(mstrLabelMnuPrint);
        menuItem.addActionListener(printPreview);
        menu.add(menuItem);

        menu.add(new JSeparator());

        menuItem = new JMenuItem(mstrLabelMnuExit);
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
    private Image imgCup;

    /**
     *
     */
    private Image imgDuke;

    /**
     *
     */
    private PageFormat mPageFormat;

    /**
     *
     */
    private PrinterJob mPrinterJob;

    /**
     *
     */
    private String mstrText = "Drucken mit Java 2";

    /**
     *
     */
    private double mdPreviewScale = 0.5d;

    /**
     * Erstellt ein neues {@link PrintPreview} Object.
     */
    public PrintPreview()
    {
        super();

        // Druckereinstellungen und Seitenlayout initialisieren
        this.mPrinterJob = PrinterJob.getPrinterJob();
        this.mPageFormat = this.mPrinterJob.defaultPage();

        // Hintergrundfarbe des Panels einstellen
        setBackground(colBackground);

        // "Wunschgroesse" fuer das Panel berechnen
        setPreferredSize(new Dimension((int) ((this.mPageFormat.getWidth() + (2 * miBorderSize)) * this.mdPreviewScale),
                (int) ((this.mPageFormat.getHeight() + (2 * miBorderSize)) * this.mdPreviewScale)));

        // Grafiken laden
        this.imgDuke = getToolkit().getImage("resources/WavingDuke.gif");
        this.imgCup = getToolkit().getImage("resources/JavaCup.gif");
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent evt)
    {
        if (evt.getSource() instanceof JMenuItem)
        {
            if (evt.getActionCommand().equals(mstrLabelMnuPrint))
            {
                // Ausdruck starten
                print();
            }
            else if (evt.getActionCommand().equals(mstrLabelMnuPageLayout))
            {
                // Seitenlayoutdialog anzeigen
                this.mPageFormat = this.mPrinterJob.pageDialog(this.mPageFormat);
                repaint();
            }
            else if (evt.getActionCommand().equals(mstrLabelMnuPrinter))
            {
                // Druckerauswahldialog anzeigen
                if (this.mPrinterJob.printDialog())
                {
                    this.mPageFormat = this.mPrinterJob.validatePage(this.mPageFormat);
                    repaint();
                }
            }
            else if (evt.getActionCommand().equals(mstrLabelMnuZoomIn))
            {
                // neuen Zoomfaktor berechnen
                if (this.mdPreviewScale < 2)
                {
                    this.mdPreviewScale *= 2;
                    repaint();
                }
            }
            else if (evt.getActionCommand().equals(mstrLabelMnuZoomOut))
            {
                // neuen Zoomfaktor berechnen
                if (this.mdPreviewScale > 0.25)
                {
                    this.mdPreviewScale /= 2;
                    repaint();
                }
            }
            else if (evt.getActionCommand().equals(mstrLabelMnuEnterText))
            {
                // Texteingabe machen
                enterText();
            }
            else if (evt.getActionCommand().equals(mstrLabelMnuExit))
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
        g2.setPaint(colForeground);

        // Text mit Schriftgroesse 30 ausgeben
        g2.setFont(g2.getFont().deriveFont(30f));
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
        if (userInput != null)
        {
            if (userInput instanceof String)
            {
                this.mstrText = (String) userInput;
                repaint(); // neu zeichnen
            }
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
        g2.setPaint(colPaper);
        g2.fillRect(miBorderSize, miBorderSize, (int) this.mPageFormat.getWidth(), (int) this.mPageFormat.getHeight());

        // Ursprung auf die Papierkante legen
        g2.translate(miBorderSize, miBorderSize);

        // Randlinien fuer den bedruckbaren Bereich einzeichnen
        g2.setPaint(colFrame);

        g2.drawLine(0, (int) this.mPageFormat.getImageableY() - 1, (int) this.mPageFormat.getWidth() - 1, (int) this.mPageFormat.getImageableY() - 1);
        g2.drawLine(0, (int) (this.mPageFormat.getImageableY() + this.mPageFormat.getImageableHeight()), (int) this.mPageFormat.getWidth() - 1,
                (int) (this.mPageFormat.getImageableY() + this.mPageFormat.getImageableHeight()));
        g2.drawLine((int) this.mPageFormat.getImageableX() - 1, 0, (int) this.mPageFormat.getImageableX() - 1, (int) this.mPageFormat.getHeight() - 1);
        g2.drawLine((int) (this.mPageFormat.getImageableX() + this.mPageFormat.getImageableWidth()), 0,
                (int) (this.mPageFormat.getImageableX() + this.mPageFormat.getImageableWidth()), (int) this.mPageFormat.getHeight() - 1);

        // Ursprung zum 2.mal verschieben. Achtung: translate() arbeitet inkrementell!
        g2.translate(this.mPageFormat.getImageableX(), this.mPageFormat.getImageableY());

        // Ausgabebereich auf den druckbaren Bereich einschraenken
        g2.setClip(0, 0, (int) this.mPageFormat.getImageableWidth(), (int) this.mPageFormat.getImageableHeight());

        // Grafik ausgeben die auf dem Drucker und in der Vorschau angezeigt werden soll
        drawMyGraphics(g2);
    }

    /**
     * Druckausgabe starten
     */
    public void print()
    {
        // Standardseitenformat holen und auf Querformat stellen
        PageFormat pfLandscape = this.mPrinterJob.defaultPage();
        pfLandscape.setOrientation(PageFormat.LANDSCAPE);

        // Ein Buch erzeugen
        Book book = new Book();

        // Deckblatt hinzufuegen. Feste Seitenanzahl, daher kann die Indexabfrage
        // in der print(...)-Methode von CoverPage entfallen
        book.append(new CoverPage(), pfLandscape, 1);

        // Grafik aus Vorschau hinzufuegen
        book.append(this, this.mPageFormat);

        // das Buch dem Druckauftrag uebergeben
        this.mPrinterJob.setPageable(book);

        try
        {
            this.mPrinterJob.print();
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
    public int print(final Graphics g, final PageFormat pageFormat, final int iPageIndex) throws PrinterException
    {
        int iPrintState = Printable.NO_SUCH_PAGE;

        // Achtung! iPageIndex == 0 ist die CoverPage!
        if (iPageIndex == 1)
        {
            Graphics2D g2 = (Graphics2D) g;

            // Ursprung verschieben und Ausgabebereich eingrenzen
            g2.translate((int) this.mPageFormat.getImageableX(), (int) this.mPageFormat.getImageableY());
            g2.setClip(0, 0, (int) this.mPageFormat.getImageableWidth(), (int) this.mPageFormat.getImageableHeight());

            // Grafik ausgeben
            drawMyGraphics(g2);

            iPrintState = Printable.PAGE_EXISTS;
        }

        return iPrintState;
    }
}
