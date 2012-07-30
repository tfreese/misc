package net.led.demo.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorSelectorPanel extends JPanel implements ActionListener
{

	/**
	 *
	 */
	private static final long serialVersionUID = 8309100804877536218L;

	private String displayLabel;

	private Color color = Color.white;

	private String id;

	private ColorSelectorListener listener;

	private JButton selectButton;

	public ColorSelectorPanel(final String displayLabel)
	{
		this(displayLabel, null, null, null);
	}

	public ColorSelectorPanel(final String displayLabel, final Color color)
	{
		this(displayLabel, color, null, null);
	}

	public ColorSelectorPanel(final String displayLabel, final Color color, final String id)
	{
		this(displayLabel, color, id, null);
	}

	public ColorSelectorPanel(final String displayLabel, final Color color, final String id,
			final ColorSelectorListener listener)
	{
		this.displayLabel = displayLabel;
		this.color = color;
		this.id = id;
		this.listener = listener;
		createGUI();
	}

	private void centerFrame(final Window frame)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		frame.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
	}

	private void createGUI()
	{
		this.selectButton = new JButton();
		this.selectButton.setPreferredSize(new Dimension(15, 15));
		this.selectButton.setActionCommand(this.displayLabel);
		this.selectButton.setBackground(this.color);
		this.selectButton.addActionListener(this);

		JLabel selectLabel = new JLabel(this.displayLabel);

		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
		add(this.selectButton);
		add(selectLabel);
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		String command = e.getActionCommand();
		if (command.equals(this.displayLabel))
		{
			Color newColor =
					JColorChooser.showDialog(ColorSelectorPanel.this, "Choose a color", this.color);
			if (newColor != null)
			{
				this.selectButton.setBackground(newColor);
				this.listener.setColor(this.id, newColor);
			}
		}
	}

	public void setColor(final Color newValue)
	{
		this.color = newValue;
		this.selectButton.setBackground(newValue);
	}

	public void setID(final String newValue)
	{
		this.id = newValue;
	}

	public void setListener(final ColorSelectorListener newValue)
	{
		this.listener = newValue;
	}
}