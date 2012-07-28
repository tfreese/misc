package de.freese.sonstiges.ballsimulation;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author Thomas Freese
 */
public class Simulation extends Applet
{
	/**
	 * Use serialVersionUID for interoperability.
	 */
	private static final long serialVersionUID = -738410624501457995L;

	/**
	 * 
	 */
	private JButton b = new JButton("Start");

	/**
	 * 
	 */
	private JLabel d = new JLabel("Daempfung in %");

	/**
	 * 
	 */
	private JLabel durchm = new JLabel("Balldurchmesser [cm]");

	/**
	 * 
	 */
	private JTextField ed = new JTextField("", 10);

	/**
	 * 
	 */
	private JTextField edurchm = new JTextField("", 10);

	/**
	 * 
	 */
	private JTextField ep = new JTextField("", 10);

	/**
	 * 
	 */
	private JTextField estartx = new JTextField("", 10);

	/**
	 * 
	 */
	private JTextField estarty = new JTextField("", 10);

	/**
	 * 
	 */
	private JTextField evx = new JTextField("", 10);

	/**
	 * 
	 */
	private JTextField evy = new JTextField("", 10);

	/**
	 * 
	 */
	private BorderLayout meinLayout = new BorderLayout();

	/**
	 * 
	 */
	private JLabel p = new JLabel("Pause in ms");

	/**
	 * 
	 */
	private JPanel p1 = new JPanel(new GridLayout(7, 1));

	/**
	 * 
	 */
	private JPanel p2 = new JPanel(new GridLayout(7, 1));

	/**
	 * 
	 */
	private JPanel p3 = new JPanel(new FlowLayout());

	// /**
	// *
	// */
	// private JPanel p4 = new JPanel(new GridLayout(7, 1));

	/**
	 * 
	 */
	private JLabel startx = new JLabel("Startpunkt x (max.1000cm)");

	/**
	 * 
	 */
	private JLabel starty = new JLabel("Startpunkt y (max.500cm)");

	/**
	 * 
	 */
	private JLabel vx = new JLabel("Startgeschwindigkeit x [m/s]");

	/**
	 * 
	 */
	private JLabel vy = new JLabel("Startgeschwindigkeit y [m/s]");

	/**
	 * Erstellt ein neues {@link Simulation} Object.
	 * 
	 * @throws HeadlessException Falls was schief geht.
	 */
	public Simulation() throws HeadlessException
	{
		super();
	}

	/**
	 * @see java.applet.Applet#init()
	 */
	@Override
	public void init()
	{
		setLayout(this.meinLayout);

		this.p1.add(this.startx);
		this.p2.add(this.estartx);

		this.p1.add(this.starty);
		this.p2.add(this.estarty);

		this.p1.add(this.vx);
		this.p2.add(this.evx);

		this.p1.add(this.vy);
		this.p2.add(this.evy);

		this.p1.add(this.durchm);
		this.p2.add(this.edurchm);

		this.p1.add(this.d);
		this.p2.add(this.ed);

		this.p1.add(this.p);
		this.p2.add(this.ep);

		this.b.addActionListener(new myButtonListener());
		this.p3.add(this.b, BorderLayout.SOUTH);

		add(BorderLayout.WEST, this.p1);
		add(BorderLayout.EAST, this.p2);
		add(BorderLayout.SOUTH, this.p3);

		setSize(300, 200);
	}

	/**
	 * @author Thomas Freese
	 */
	class myButtonListener implements ActionListener
	{
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(final ActionEvent e)
		{
			double stx = ((double) Integer.parseInt(Simulation.this.estartx.getText())) / 100;
			double sty = ((double) Integer.parseInt(Simulation.this.estarty.getText())) / 100;

			if (stx > 10)
			{
				stx = 10;
			}

			if (sty > 5)
			{
				sty = 5;
			}

			// Ballsimulation bal =
			new Ballsimulation(stx, sty, (Integer.parseInt(Simulation.this.evx.getText())),
					(Integer.parseInt(Simulation.this.evy.getText())),
					((double) Integer.parseInt(Simulation.this.edurchm.getText())) / 100,
					((double) Integer.parseInt(Simulation.this.ed.getText())) / 100,
					Integer.parseInt(Simulation.this.ep.getText()));
		}
	}
}
