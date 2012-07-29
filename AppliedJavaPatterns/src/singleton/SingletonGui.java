package singleton;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author Thomas Freese
 */
public class SingletonGui implements ActionListener
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
	private static int historyCount = 0;

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
	private JTextArea display = null;

	/**
     * 
     */
	private JFrame mainFrame = null;

	/**
     * 
     */
	private JButton newContact = null;

	/**
     * 
     */
	private JButton newAppointment = null;

	/**
     * 
     */
	private JButton undo = null;

	/**
     * 
     */
	private JButton refresh = null;

	/**
     * 
     */
	private JButton exit = null;

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent evt)
	{
		Object originator = evt.getSource();

		if (originator == this.newContact)
		{
			addCommand(" New Contact");
		}
		else if (originator == this.newAppointment)
		{
			addCommand(" New Appointment");
		}
		else if (originator == this.undo)
		{
			undoCommand();
		}
		else if (originator == this.refresh)
		{
			refreshDisplay("");
		}
		else if (originator == this.exit)
		{
			exitApplication();
		}
	}

	/**
	 * @param message String
	 */
	private void addCommand(final String message)
	{
		HistoryList.getInstance().addCommand((++historyCount) + message);
		refreshDisplay("Add Command: " + message);
	}

	/**
     * 
     */
	public void createGui()
	{
		this.mainFrame = new JFrame("Singleton Pattern Example");
		Container content = this.mainFrame.getContentPane();

		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		this.displayPanel = new JPanel();
		this.display = new JTextArea(20, 60);
		this.display.setEditable(false);
		this.displayPanel.add(this.display);
		content.add(this.displayPanel);

		this.controlPanel = new JPanel();
		this.newContact = new JButton("Create contact");
		this.newAppointment = new JButton("Create appointment");
		this.undo = new JButton("Undo");
		this.refresh = new JButton("Refresh");
		this.exit = new JButton("Exit");
		this.controlPanel.add(this.newContact);
		this.controlPanel.add(this.newAppointment);
		this.controlPanel.add(this.undo);
		this.controlPanel.add(this.refresh);
		this.controlPanel.add(this.exit);
		content.add(this.controlPanel);

		this.newContact.addActionListener(this);
		this.newAppointment.addActionListener(this);
		this.undo.addActionListener(this);
		this.refresh.addActionListener(this);
		this.exit.addActionListener(this);

		this.mainFrame.addWindowListener(new WindowCloseManager());
		this.mainFrame.pack();
		this.mainFrame.setVisible(true);
	}

	/**
     * 
     */
	private void exitApplication()
	{
		System.exit(0);
	}

	/**
	 * @param actionMessage String
	 */
	public void refreshDisplay(final String actionMessage)
	{
		this.display.setText(actionMessage + "\nCOMMAND HISTORY:\n"
				+ HistoryList.getInstance().toString());
	}

	/**
     * 
     */
	private void undoCommand()
	{
		Object result = HistoryList.getInstance().undoCommand();

		historyCount--;
		refreshDisplay("Undo Command: " + result);
	}
}
