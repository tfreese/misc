package de.freese.sonstiges.dnd.picture;

/*
 * DragPictureDemo.java is a 1.4 example that requires the following files: Picture.java
 * DTPicture.java PictureTransferHandler.java images/Maya.jpg images/Anya.jpg images/Laine.jpg
 * images/Cosmo.jpg images/Adele.jpg images/Alexi.jpg
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Thomas Freese
 */
public class DragPictureDemo extends JPanel
{
	/**
	 * 
	 */
	static String adeleString = "Adele";

	/**
	 * 
	 */
	static String alexiString = "Alexi";

	/**
	 * 
	 */
	static String anyaString = "Anya";

	/**
	 * 
	 */
	static String cosmoString = "Cosmo";

	/**
	 * 
	 */
	static String laineString = "Laine";

	/**
	 * 
	 */
	static String mayaString = "Maya";

	/**
	 *
	 */
	private static final long serialVersionUID = 3063560622968069521L;

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI()
	{
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("DragPictureDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the menu bar and content pane.
		DragPictureDemo demo = new DragPictureDemo();
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
		// java.net.URL imageURL = DragPictureDemo.class.getResource(path);
		URL imageURL = DragPictureDemo.class.getClassLoader().getResource(path);

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
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				createAndShowGUI();
			}
		});
	}

	/**
	 * 
	 */
	DTPicture pic1;

	/**
	 * 
	 */
	DTPicture pic10;

	/**
	 * 
	 */
	DTPicture pic11;

	/**
	 * 
	 */
	DTPicture pic12;

	/**
	 * 
	 */
	DTPicture pic2;

	/**
	 * 
	 */
	DTPicture pic3;

	/**
	 *
	 */
	DTPicture pic4;

	/**
	 * 
	 */
	DTPicture pic5;

	/**
	 * 
	 */
	DTPicture pic6;

	/**
	 * 
	 */
	DTPicture pic7;

	/**
	 *
	 */
	DTPicture pic8;

	/**
	 * 
	 */
	DTPicture pic9;

	/**
	 * 
	 */
	PictureTransferHandler picHandler;

	/**
	 * Creates a new DragPictureDemo object.
	 */
	public DragPictureDemo()
	{
		super(new BorderLayout());
		this.picHandler = new PictureTransferHandler();

		JPanel mugshots = new JPanel(new GridLayout(4, 3));
		this.pic1 =
				new DTPicture(createImageIcon("dnd/picture/" + mayaString + ".jpg", mayaString)
						.getImage());
		this.pic1.setTransferHandler(this.picHandler);
		mugshots.add(this.pic1);
		this.pic2 =
				new DTPicture(createImageIcon("dnd/picture/" + anyaString + ".jpg", anyaString)
						.getImage());
		this.pic2.setTransferHandler(this.picHandler);
		mugshots.add(this.pic2);
		this.pic3 =
				new DTPicture(createImageIcon("dnd/picture/" + laineString + ".jpg", laineString)
						.getImage());
		this.pic3.setTransferHandler(this.picHandler);
		mugshots.add(this.pic3);
		this.pic4 =
				new DTPicture(createImageIcon("dnd/picture/" + cosmoString + ".jpg", cosmoString)
						.getImage());
		this.pic4.setTransferHandler(this.picHandler);
		mugshots.add(this.pic4);
		this.pic5 =
				new DTPicture(createImageIcon("dnd/picture/" + adeleString + ".jpg", adeleString)
						.getImage());
		this.pic5.setTransferHandler(this.picHandler);
		mugshots.add(this.pic5);
		this.pic6 =
				new DTPicture(createImageIcon("dnd/picture/" + alexiString + ".jpg", alexiString)
						.getImage());
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
}