package command;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author Thomas Freese
 */
public class CommandGui implements ActionListener, LocationEditor
{
	/**
	 * @author Thomas Freese
	 */
	private class WindowCloseManager extends WindowAdapter
	{
		/**
		 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosing(final WindowEvent evt)
		{
			exitApplication();
		}
	}

	/**
     * 
     */
	private Appointment appointment = null;

	/**
     * 
     */
	private UndoableCommand command = null;

	/**
     * 
     */
	private JPanel controlPanel = null;

	/**
     * 
     */
	private JPanel displayPanel = null;

	/**
     * 
     */
	private JPanel editorPanel = null;

	/**
     * 
     */
	private JTextArea display = null;

	/**
     * 
     */
	private JFrame mainFrame = null;

	/**
     * 
     */
	private JButton update = null;

	/**
     * 
     */
	private JButton undo = null;

	/**
     * 
     */
	private JButton redo = null;

	/**
     * 
     */
	private JButton exit = null;

	/**
     * 
     */
	private JTextField updatedLocation = null;

	/**
	 * Creates a new {@link CommandGui} object.
	 * 
	 * @param newCommand {@link UndoableCommand}
	 */
	public CommandGui(final UndoableCommand newCommand)
	{
		super();

		this.command = newCommand;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent evt)
	{
		Object originator = evt.getSource();

		if (originator == this.update)
		{
			executeCommand();
		}

		if (originator == this.undo)
		{
			undoCommand();
		}

		if (originator == this.redo)
		{
			redoCommand();
		}
		else if (originator == this.exit)
		{
			exitApplication();
		}
	}

	/**
     * 
     */
	public void createGui()
	{
		this.mainFrame = new JFrame("Command Pattern Example");
		Container content = this.mainFrame.getContentPane();

		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		this.editorPanel = new JPanel();
		this.editorPanel.add(new JLabel("Location"));
		this.updatedLocation = new JTextField(20);
		this.editorPanel.add(this.updatedLocation);
		content.add(this.editorPanel);

		this.displayPanel = new JPanel();
		this.display = new JTextArea(10, 40);
		this.display.setEditable(false);
		this.displayPanel.add(this.display);
		content.add(this.displayPanel);

		this.controlPanel = new JPanel();
		this.update = new JButton("Update Location");
		this.undo = new JButton("Undo Location");
		this.redo = new JButton("Redo Location");
		this.exit = new JButton("Exit");
		this.controlPanel.add(this.update);
		this.controlPanel.add(this.undo);
		this.controlPanel.add(this.redo);
		this.controlPanel.add(this.exit);
		content.add(this.controlPanel);

		this.update.addActionListener(this);
		this.undo.addActionListener(this);
		this.redo.addActionListener(this);
		this.exit.addActionListener(this);

		refreshDisplay();
		this.mainFrame.addWindowListener(new WindowCloseManager());
		this.mainFrame.pack();
		this.mainFrame.setVisible(true);
	}

	/**
     * 
     */
	private void executeCommand()
	{
		this.command.execute();
		refreshDisplay();
	}

	/**
     * 
     */
	private void exitApplication()
	{
		System.exit(0);
	}

	/**
	 * @see command.LocationEditor#getNewLocation()
	 */
	@Override
	public Location getNewLocation()
	{
		return new LocationImpl(this.updatedLocation.getText());
	}

	/**
     * 
     */
	private void redoCommand()
	{
		this.command.redo();
		refreshDisplay();
	}

	/**
     * 
     */
	private void refreshDisplay()
	{
		this.display.setText(this.appointment.toString());
	}

	/**
	 * @param newAppointment {@link Appointment}
	 */
	public void setAppointment(final Appointment newAppointment)
	{
		this.appointment = newAppointment;
	}

	/**
     * 
     */
	private void undoCommand()
	{
		this.command.undo();
		refreshDisplay();
	}
}
