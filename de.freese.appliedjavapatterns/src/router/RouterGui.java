package router;

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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author Thomas Freese
 */
public class RouterGui implements ActionListener, Receiver
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
	private static int instanceCount = 1;

	/**
	 * 
	 */
	private JButton clearDisplay;

	/**
	 * 
	 */
	private JTextArea display;

	/**
	 * 
	 */
	private JButton exit;

	/**
	 * 
	 */
	private InputChannel inputChannel;

	/**
	 * 
	 */
	private JTextField inputTextField;

	/**
	 * 
	 */
	private JFrame mainFrame;

	/**
	 * 
	 */
	private RouterClient routerClient;

	/**
	 * 
	 */
	private JButton sendMessage;

	/**
	 * Creates a new RouterGui object.
	 * 
	 * @param newInputChannel {@link InputChannel}
	 */
	public RouterGui(final InputChannel newInputChannel)
	{
		this.inputChannel = newInputChannel;
		this.routerClient = new RouterClient(this);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent evt)
	{
		Object source = evt.getSource();

		if (source == this.sendMessage)
		{
			sendMessage();
		}
		else if (source == this.inputTextField)
		{
			sendMessage();
		}
		else if (source == this.clearDisplay)
		{
			clearDisplay();
		}
		else if (source == this.exit)
		{
			exitApplication();
		}
	}

	/**
	 * 
	 */
	private void clearDisplay()
	{
		this.inputTextField.setText("");
		this.display.setText("");
	}

	/**
	 * 
	 */
	public void createGui()
	{
		this.mainFrame =
				new JFrame("Demonstration for the Router pattern - GUI #" + instanceCount++);

		Container content = this.mainFrame.getContentPane();

		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		JPanel displayPanel = new JPanel();

		this.display = new JTextArea(10, 40);

		JScrollPane displayArea = new JScrollPane(this.display);

		this.display.setEditable(false);
		displayPanel.add(displayArea);
		content.add(displayPanel);

		JPanel dataPanel = new JPanel();

		dataPanel.add(new JLabel("Message:"));
		this.inputTextField = new JTextField(30);
		dataPanel.add(this.inputTextField);
		content.add(dataPanel);

		JPanel controlPanel = new JPanel();

		this.sendMessage = new JButton("Send Message");
		this.clearDisplay = new JButton("Clear");
		this.exit = new JButton("Exit");
		controlPanel.add(this.sendMessage);
		controlPanel.add(this.clearDisplay);
		controlPanel.add(this.exit);
		content.add(controlPanel);

		this.sendMessage.addActionListener(this);
		this.clearDisplay.addActionListener(this);
		this.exit.addActionListener(this);
		this.inputTextField.addActionListener(this);

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
	 * @return {@link OutputChannel}
	 */
	public OutputChannel getOutputChannel()
	{
		return this.routerClient;
	}

	/**
	 * @see router.Receiver#receiveMessage(router.Message)
	 */
	@Override
	public void receiveMessage(final Message message)
	{
		this.display.append(message.getMessage() + "\n");
	}

	/**
	 * 
	 */
	private void sendMessage()
	{
		String data = this.inputTextField.getText();

		this.routerClient.sendMessageToRouter(new Message(this.inputChannel, data));
		this.inputTextField.setText("");
	}
}
