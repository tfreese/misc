package net.leddisplay.demo;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.led.demo.elements.display.TextDisplayElement;
import net.leddisplay.LedDisplay;
import net.leddisplay.LedDisplayFactory;

public class LedDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		JDialog dialog = new LedDialog(null);
		dialog.setVisible(true);

		dialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(final java.awt.event.WindowEvent e)
			{
				System.exit(0);
			}
		});
	}

	private DateFormat dateFormatter;

	private JPanel jContentPane = null;

	private JComponent jLedComponent = null;

	LedDisplay ledDisplay;

	private DateFormat timeFormatter;

	private Timer timer;

	/**
	 * @param owner {@link Frame}
	 */
	public LedDialog(final Frame owner)
	{
		super(owner);
		initialize();
		createTimer();
	}

	/**
	 * Erzeuge einen Timer für die Zeitanzeige.
	 */
	private void createTimer()
	{
		this.timer = new Timer(3000, new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				if (!isShowing())
				{
					return;
				}

				Date currentDate = new Date();

				String time = getTimeFormatter().format(new Time(currentDate.getTime()));
				String date = getDateFormatter().format(currentDate);

				String symbol = time + " " + date;
				TextDisplayElement displayElement = new TextDisplayElement(symbol);
				getLedDisplay().setDisplayElement(displayElement);

				LedDialog.this.timer.start();
			}
		});

		this.timer.setInitialDelay(5000);
		this.timer.setRepeats(true);
		this.timer.start();
	}

	/**
	 * @return Formatierer für Datumsangaben.
	 */
	private DateFormat getDateFormatter()
	{
		if (this.dateFormatter == null)
		{
			this.dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
		}

		return this.dateFormatter;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane()
	{
		if (this.jContentPane == null)
		{
			this.jContentPane = new JPanel();
			this.jContentPane.setLayout(new BorderLayout());
			this.jContentPane.add(getJLedComponent(), BorderLayout.CENTER);
		}

		return this.jContentPane;
	}

	/**
	 * This method initializes jLedComponent
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JComponent getJLedComponent()
	{
		if (this.jLedComponent == null)
		{
			String symbol = "Hallo!";
			TextDisplayElement displayElement = new TextDisplayElement(symbol);
			getLedDisplay().setDisplayElement(displayElement);

			this.jLedComponent = getLedDisplay().getComponent();
		}

		return this.jLedComponent;
	}

	/**
	 * @return LED-Anzeige.
	 */
	private LedDisplay getLedDisplay()
	{
		if (this.ledDisplay == null)
		{
			this.ledDisplay = LedDisplayFactory.createLedDisplay();
			this.ledDisplay.setTokenGap(2);
			this.ledDisplay.setDotSize(2, 2);
			this.ledDisplay.setDotGaps(1, 1);
		}

		return this.ledDisplay;
	}

	/**
	 * @return Formatierer für Zeitangaben.
	 */
	private DateFormat getTimeFormatter()
	{
		if (this.timeFormatter == null)
		{
			this.timeFormatter = DateFormat.getTimeInstance(DateFormat.LONG, Locale.getDefault());
		}

		return this.timeFormatter;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		setSize(429, 200);
		setContentPane(getJContentPane());
	}
}
