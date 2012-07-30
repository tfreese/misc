package de.freese.openstreetmap;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.freese.openstreetmap.model.OSMModel;

/**
 * @author Thomas Freese
 */
public class MyFrame extends JFrame implements ActionListener
{
	/**
	 *
	 */
	private static final long serialVersionUID = 9060490859375473760L;

	/**
	 * 
	 */
	private final MyPanel myPanel;

	/**
	 * Erstellt ein neues {@link MyFrame} Object.
	 * 
	 * @param model {@link OSMModel}
	 */
	public MyFrame(final OSMModel model)
	{
		super("");

		this.myPanel = new MyPanel(model);

		BorderLayout myLayout = new BorderLayout();

		// work on panel
		Panel myButtonPanel = new Panel();
		Button myWorkButton;

		this.myPanel.setLayout(new FlowLayout());
		myWorkButton = new Button("ZTF");
		myWorkButton.addActionListener(this);
		myButtonPanel.add(myWorkButton);
		myWorkButton = new Button("+");
		myWorkButton.addActionListener(this);
		myButtonPanel.add(myWorkButton);

		// inner panel
		Panel navPanel = new Panel();
		GridLayout gridL = new GridLayout();
		gridL.setRows(3);
		gridL.setColumns(3);
		navPanel.setLayout(gridL);
		navPanel.add(new Panel());
		myWorkButton = new Button("N");
		myWorkButton.addActionListener(this);
		navPanel.add(myWorkButton);
		navPanel.add(new Panel());
		myWorkButton = new Button("W");
		myWorkButton.addActionListener(this);
		navPanel.add(myWorkButton);
		navPanel.add(new Panel());
		myWorkButton = new Button("E");
		myWorkButton.addActionListener(this);
		navPanel.add(myWorkButton);
		navPanel.add(new Panel());
		myWorkButton = new Button("S");
		myWorkButton.addActionListener(this);
		navPanel.add(myWorkButton);
		navPanel.add(new Panel());

		// Outer panel again
		myButtonPanel.add(navPanel);
		myWorkButton = new Button("-");
		myWorkButton.addActionListener(this);
		myButtonPanel.add(myWorkButton);

		setLayout(myLayout);
		add(myButtonPanel, BorderLayout.SOUTH);
		add(this.myPanel, BorderLayout.CENTER);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setVisible(true);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(final ActionEvent e)
	{
		String doWhat = e.getActionCommand();

		if (doWhat == "ZTF")
		{
			this.myPanel.zoomToFit();
			// this.myPanel.repaint();
			repaint();
		}
		else if (doWhat == "+")
		{
			this.myPanel.zoom(1.3);
			// this.myPanel.repaint();
			repaint();
		}
		else if (doWhat == "-")
		{
			this.myPanel.zoom(1 / 1.3);
			// this.myPanel.repaint();
			repaint();
		}
		else if (doWhat == "N")
		{
			this.myPanel.scrollVertical(-20);
			// this.myPanel.repaint();
			repaint();
		}
		else if (doWhat == "W")
		{
			this.myPanel.scrollHorizontal(-20);
			// this.myPanel.repaint();
			repaint();
		}
		else if (doWhat == "E")
		{
			this.myPanel.scrollHorizontal(20);
			// this.myPanel.repaint();
			repaint();
		}
		else if (doWhat == "S")
		{
			this.myPanel.scrollVertical(20);
			// this.myPanel.repaint();
			repaint();
		}
	}
}
