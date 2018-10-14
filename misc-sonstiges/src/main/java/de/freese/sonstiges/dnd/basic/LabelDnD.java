package de.freese.sonstiges.dnd.basic;

/*
 * LabelDnD.java is a 1.4 example that requires no other files.
 */
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.TitledBorder;

/**
 * @author Thomas Freese
 */
public class LabelDnD extends JPanel
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
	private static final long serialVersionUID = -8569374356503714387L;

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI()
	{
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("LabelDnD");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		JComponent newContentPane = new LabelDnD();
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
	 * 
	 */
	private JTextField textField = null;

	/**
	 * Creates a new {@link LabelDnD} object.
	 */
	public LabelDnD()
	{
		super(new GridLayout(2, 1));

		this.textField = new JTextField(40);
		this.textField.setDragEnabled(true);

		JPanel tfpanel = new JPanel(new GridLayout(1, 1));
		TitledBorder t1 = BorderFactory.createTitledBorder("JTextField: drag and drop is enabled");
		tfpanel.add(this.textField);
		tfpanel.setBorder(t1);

		this.label = new JLabel("I'm a Label!", SwingConstants.LEADING);
		this.label.setTransferHandler(new TransferHandler("text"));

		MouseListener listener = new DragMouseAdapter();
		this.label.addMouseListener(listener);

		JPanel lpanel = new JPanel(new GridLayout(1, 1));
		TitledBorder t2 = BorderFactory.createTitledBorder("JLabel: drag from or drop to this label");
		lpanel.add(this.label);
		lpanel.setBorder(t2);

		add(tfpanel);
		add(lpanel);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
}
