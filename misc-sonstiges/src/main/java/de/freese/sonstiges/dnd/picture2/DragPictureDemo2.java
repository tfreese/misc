package de.freese.sonstiges.dnd.picture2;

/*
 * DragPictureDemo2.java is a 1.4 example that requires the following files: Picture.java DTPicture.java PictureTransferHandler.java TransferActionListener.java
 * images/Maya.jpg images/Anya.jpg images/Laine.jpg images/Cosmo.jpg images/Adele.jpg images/Alexi.jpg
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;

// A version of DragPictureDemo that creates an
// Edit menu with cut/copy/paste actions.
// This demo adds a class called TransferActionDemo
// that transfers the cut/copy/paste menu action
// to the currently focused component.
/**
 * @author Thomas Freese
 */
public class DragPictureDemo2 extends JPanel
{
    /**
     *
     */
    private static final String ADELE = "Adele";

    /**
     *
     */
    private static final String ALEXI = "Alexi";

    /**
     *
     */
    private static final String ANYA = "Anya";

    /**
     *
     */
    private static final String COSMO = "Cosmo";

    /**
     *
     */
    private static final String LAINE = "Laine";

    /**
     *
     */
    private static final String MAYA = "Maya";

    /**
     *
     */
    private static final long serialVersionUID = -3292264415574380091L;

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI()
    {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        JFrame frame = new JFrame("DragPictureDemo2");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create and set up the menu bar and content pane.
        DragPictureDemo2 demo = new DragPictureDemo2();
        frame.setJMenuBar(demo.createMenuBar());
        demo.setOpaque(true); // content panes must be opaque
        frame.setContentPane(demo);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     *
     * @param path String
     * @param description String
     * @return {@link ImageIcon}
     */
    protected static ImageIcon createImageIcon(final String path, final String description)
    {
        URL imageURL = DragPictureDemo2.class.getResource(path);

        if (imageURL == null)
        {
            System.err.println("Resource not found: " + path);

            return null;
        }

        return new ImageIcon(imageURL, description);
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(DragPictureDemo2::createAndShowGUI);
    }

    /**
     *
     */
    private DTPicture pic1;

    /**
     *
     */
    private DTPicture pic10;

    /**
     *
     */
    private DTPicture pic11;

    /**
     *
     */
    private DTPicture pic12;

    /**
     *
     */
    private DTPicture pic2;

    /**
     *
     */
    private DTPicture pic3;

    /**
     *
     */
    private DTPicture pic4;

    /**
     *
     */
    private DTPicture pic5;

    /**
     *
     */
    private DTPicture pic6;

    /**
     *
     */
    private DTPicture pic7;

    /**
     *
     */
    private DTPicture pic8;

    /**
     *
     */
    private DTPicture pic9;

    /**
     *
     */
    private PictureTransferHandler picHandler;

    /**
     * Creates a new DragPictureDemo2 object.
     */
    public DragPictureDemo2()
    {
        super(new BorderLayout());
        this.picHandler = new PictureTransferHandler();

        // Since we are using keyboard accelerators, we don't
        // need the component to install its own input map
        // bindings.
        DTPicture.setInstallInputMapBindings(false);

        JPanel mugshots = new JPanel(new GridLayout(4, 3));
        this.pic1 = new DTPicture(createImageIcon(MAYA + ".jpg", MAYA).getImage());
        this.pic1.setTransferHandler(this.picHandler);
        mugshots.add(this.pic1);
        this.pic2 = new DTPicture(createImageIcon(ANYA + ".jpg", ANYA).getImage());
        this.pic2.setTransferHandler(this.picHandler);
        mugshots.add(this.pic2);
        this.pic3 = new DTPicture(createImageIcon(LAINE + ".jpg", LAINE).getImage());
        this.pic3.setTransferHandler(this.picHandler);
        mugshots.add(this.pic3);
        this.pic4 = new DTPicture(createImageIcon(COSMO + ".jpg", COSMO).getImage());
        this.pic4.setTransferHandler(this.picHandler);
        mugshots.add(this.pic4);
        this.pic5 = new DTPicture(createImageIcon(ADELE + ".jpg", ADELE).getImage());
        this.pic5.setTransferHandler(this.picHandler);
        mugshots.add(this.pic5);
        this.pic6 = new DTPicture(createImageIcon(ALEXI + ".jpg", ALEXI).getImage());
        this.pic6.setTransferHandler(this.picHandler);
        mugshots.add(this.pic6);

        // These six components with no pictures provide handy
        // drop targets.
        this.pic7 = new DTPicture(null);
        this.pic7.setTransferHandler(this.picHandler);
        mugshots.add(this.pic7);
        this.pic8 = new DTPicture(null);
        this.pic8.setTransferHandler(this.picHandler);
        mugshots.add(this.pic8);
        this.pic9 = new DTPicture(null);
        this.pic9.setTransferHandler(this.picHandler);
        mugshots.add(this.pic9);
        this.pic10 = new DTPicture(null);
        this.pic10.setTransferHandler(this.picHandler);
        mugshots.add(this.pic10);
        this.pic11 = new DTPicture(null);
        this.pic11.setTransferHandler(this.picHandler);
        mugshots.add(this.pic11);
        this.pic12 = new DTPicture(null);
        this.pic12.setTransferHandler(this.picHandler);
        mugshots.add(this.pic12);

        setPreferredSize(new Dimension(450, 630));
        add(mugshots, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    // Create an Edit menu to support cut/copy/paste.
    /**
     * @return {@link JMenuBar}
     */
    public JMenuBar createMenuBar()
    {
        JMenuItem menuItem = null;
        JMenuBar menuBar = new JMenuBar();
        JMenu mainMenu = new JMenu("Edit");
        mainMenu.setMnemonic(KeyEvent.VK_E);

        TransferActionListener actionListener = new TransferActionListener();

        menuItem = new JMenuItem("Cut");
        menuItem.setActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME));
        menuItem.addActionListener(actionListener);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menuItem.setMnemonic(KeyEvent.VK_T);
        mainMenu.add(menuItem);
        menuItem = new JMenuItem("Copy");
        menuItem.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));
        menuItem.addActionListener(actionListener);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menuItem.setMnemonic(KeyEvent.VK_C);
        mainMenu.add(menuItem);
        menuItem = new JMenuItem("Paste");
        menuItem.setActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME));
        menuItem.addActionListener(actionListener);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        menuItem.setMnemonic(KeyEvent.VK_P);
        mainMenu.add(menuItem);

        menuBar.add(mainMenu);

        return menuBar;
    }
}
