package net.led.demo.util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class OptionsDialog extends JDialog implements ActionListener
{

	/**
	 *
	 */
	private static final long serialVersionUID = -935627873163594283L;

	private JTextField symbolField = new JTextField(6);

	private List currentSymbols;

	private OptionsListener listener;

	/**
	 * @param owner the parent of this window
	 * @param newListener the object that will be notified when the user adds or removes symbols
	 * @param symbols the array of symbols of the LedTicker
	 */
	public OptionsDialog(final JFrame owner, final OptionsListener newListener,
			final String[] symbols)
	{
		super(owner, "Options", true);
		this.listener = newListener;
		this.currentSymbols = new List(10);
		for (String symbol : symbols)
		{
			this.currentSymbols.add(symbol);
		}
		createGUI();
	}

	/**
	 * Places the dialog on the center of the screen.
	 */
	public static void centerFrame(final Window frame)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		frame.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
	}

	private void createGUI()
	{
		this.symbolField.addActionListener(this);
		this.symbolField.setActionCommand("add");

		JButton addSymbolButton = new JButton("Add");
		addSymbolButton.setActionCommand("add");
		addSymbolButton.addActionListener(this);

		JButton removeSymbolButton = new JButton("Remove");
		removeSymbolButton.setActionCommand("remove");
		removeSymbolButton.addActionListener(this);

		JButton closeButton = new JButton("Close");
		closeButton.setActionCommand("close");
		closeButton.addActionListener(this);

		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 0, 5);
		getContentPane().add(this.symbolField, gbc);

		gbc.gridx++;
		gbc.anchor = GridBagConstraints.WEST;
		getContentPane().add(addSymbolButton, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTH;
		getContentPane().add(removeSymbolButton, gbc);

		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 5, 5);
		getContentPane().add(this.currentSymbols, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.SOUTHEAST;
		getContentPane().add(closeButton, gbc);

		pack();
		centerFrame(this);
		setResizable(false);
		setVisible(true);
	}

	private void addSymbol()
	{
		String symbol = this.symbolField.getText().toUpperCase().trim();
		this.symbolField.setText("");
		if ((symbol != null) && !symbol.equals(""))
		{
			this.currentSymbols.add(symbol);
			this.listener.addSymbol(symbol);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command.equals("add"))
		{
			addSymbol();
		}
		else if (command.equals("remove"))
		{
			String symbol = this.currentSymbols.getSelectedItem().toUpperCase();
			this.currentSymbols.remove(symbol);
			this.listener.removeSymbol(symbol);
		}
		else if (command.equals("close"))
		{
			dispose();
		}
	}
}