// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.freese.simulationen.model.AbstractWorld;

/**
 * BasisView fuer die Simulationen.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter Typ der Welt
 */
public abstract class AbstractSimulationView<T extends AbstractWorld>
{
	/**
	 *
	 */
	private JPanel controlPanel = null;

	/**
	 *
	 */
	private JPanel buttonPanel = null;

	/**
	 *
	 */
	private JButton buttonStart = null;

	/**
	 *
	 */
	private JPanel mainPanel = null;

	/**
	 *
	 */
	private T model = null;

	/**
	 *
	 */
	private ScheduledFuture<?> scheduledFuture = null;

	/**
	 *
	 */
	private final int delay;

	/**
	 * Erstellt ein neues {@link AbstractSimulationView} Object.
	 */
	public AbstractSimulationView()
	{
		super();

		this.delay =
				Integer.parseInt(SimulationDemo.properties.getProperty("simulation.delay", "200"));
	}

	/**
	 * Erzeugt das Model.
	 * 
	 * @param fieldWidth int
	 * @param fieldHeight int
	 * @return {@link AbstractWorld}
	 */
	protected abstract T createModel(final int fieldWidth, final int fieldHeight);

	/**
	 * @return {@link JPanel}
	 */
	protected JPanel getControlPanel()
	{
		if (this.controlPanel == null)
		{
			this.controlPanel = new JPanel();
		}

		return this.controlPanel;
	}

	/**
	 * @return {@link JPanel}
	 */
	private JPanel getButtonPanel()
	{
		if (this.buttonPanel == null)
		{
			this.buttonPanel = new JPanel();
			this.buttonPanel.setLayout(new BorderLayout());

			this.buttonStart = new JButton("Start");
			this.buttonStart.addActionListener(new ActionListener()
			{
				/**
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					start();
				}
			});
			this.buttonPanel.add(this.buttonStart, BorderLayout.WEST);

			JButton buttonStop = new JButton("Stop");
			buttonStop.addActionListener(new ActionListener()
			{
				/**
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					stop();
				}
			});
			this.buttonPanel.add(buttonStop, BorderLayout.EAST);

			JButton buttonStep = new JButton("Step");
			buttonStep.addActionListener(new ActionListener()
			{
				/**
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					stop();
					start();
					stop();
				}
			});
			this.buttonPanel.add(buttonStep, BorderLayout.NORTH);

			JButton buttonReset = new JButton("Reset");
			buttonReset.addActionListener(new ActionListener()
			{
				/**
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				@Override
				public void actionPerformed(final ActionEvent e)
				{
					reset();
				}
			});
			this.buttonPanel.add(buttonReset, BorderLayout.SOUTH);
		}

		return this.buttonPanel;
	}

	/**
	 * @return {@link JPanel}
	 */
	protected JPanel getMainPanel()
	{
		if (this.mainPanel == null)
		{
			this.mainPanel = new JPanel();
			this.mainPanel.setDoubleBuffered(true);
		}

		return this.mainPanel;
	}

	/**
	 * @return {@link AbstractWorld}
	 */
	public T getModel()
	{
		return this.model;
	}

	/**
	 * @return {@link ScheduledExecutorService}
	 */
	protected ScheduledExecutorService getScheduledExecutorService()
	{
		return SimulationDemo.scheduledExecutorService;
	}

	/**
	 * Aufbau der GUI.
	 * 
	 * @param fieldWidth int
	 * @param fieldHeight int
	 */
	public void initialize(final int fieldWidth, final int fieldHeight)
	{
		this.model = createModel(fieldWidth, fieldHeight);

		getControlPanel().setLayout(new BorderLayout());
		getControlPanel().setPreferredSize(new Dimension(150, 10));
		getControlPanel().add(getButtonPanel(), BorderLayout.NORTH);

		getMainPanel().setLayout(new BorderLayout());
		getMainPanel().add(getControlPanel(), BorderLayout.EAST);
	}

	/**
	 * Zuruecksetzen die Simulation.
	 */
	protected void reset()
	{
		stop();
		getModel().initialize();
		start();
	}

	/**
	 * Startet die Simulation.
	 */
	protected void start()
	{
		Runnable runnable = new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				try
				{
					getModel().nextGeneration();
				}
				catch (Exception ex)
				{
					stop();

					ex.printStackTrace();

					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);

					ex.printStackTrace(pw);
					pw.close();

					JOptionPane.showMessageDialog(getMainPanel(), sw);
				}
			}
		};
		// Callable<Void> callable = new Callable<Void>()
		// {
		// /**
		// * @see java.util.concurrent.Callable#call()
		// */
		// @Override
		// public Void call() throws Exception
		// {
		// getModel().nextGeneration();
		//
		// return null;
		// }
		// };

		this.scheduledFuture =
				getScheduledExecutorService().scheduleWithFixedDelay(runnable, 0, this.delay,
						TimeUnit.MILLISECONDS);
		// this.scheduledFuture =
		// getScheduledExecutorService().schedule(callable, 0, TimeUnit.MILLISECONDS);

		this.buttonStart.setEnabled(false);
	}

	/**
	 * Stopt die Simulation.
	 */
	protected void stop()
	{
		if ((this.scheduledFuture != null))
		{
			this.scheduledFuture.cancel(false);
		}

		this.buttonStart.setEnabled(true);
	}
}
