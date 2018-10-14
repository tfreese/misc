package de.freese.sonstiges.dnd.basic;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.TitledBorder;

/**
 * @author Thomas Freese
 */
public class LabelDnD2 extends JPanel
{
	/**
	 * @author Thomas Freese
	 */
	private class DragMouseAdapter extends MouseAdapter
	{
		/**
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(final MouseEvent e)
		{
			JComponent c = (JComponent) e.getSource();
			TransferHandler handler = c.getTransferHandler();
			handler.exportAsDrag(c, e, TransferHandler.COPY);
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 8991761070190202012L;

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI()
	{
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("LabelDnD2");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		JComponent newContentPane = new LabelDnD2();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
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
	private JLabel label = null;

	/**
	 * Creates a new LabelDnD2 object.
	 */
	public LabelDnD2()
	{
		super(new BorderLayout());

		JColorChooser chooser = new JColorChooser();
		chooser.setDragEnabled(true);

		this.label = new JLabel("I'm a Label and I accept dnd.color!", SwingConstants.LEADING);
		this.label.setTransferHandler(new TransferHandler("foreground"));

		// label.setTransferHandler(new TransferHandler("background"));
		MouseListener listener = new DragMouseAdapter();
		this.label.addMouseListener(listener);

		JPanel lpanel = new JPanel(new GridLayout(1, 1));
		TitledBorder t2 = BorderFactory.createTitledBorder("JLabel: drop dnd.color onto the label");
		lpanel.add(this.label);
		lpanel.setBorder(t2);

		add(chooser, BorderLayout.CENTER);
		add(lpanel, BorderLayout.PAGE_END);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
}
