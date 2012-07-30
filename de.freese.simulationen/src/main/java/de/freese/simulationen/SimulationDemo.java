// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import de.freese.simulationen.ameise.AntView;
import de.freese.simulationen.gameoflife.GofView;
import de.freese.simulationen.wator.WaTorDiagrammPanel;
import de.freese.simulationen.wator.WaTorView;

/**
 * Hauptfenster der Simulation-Demos.
 * 
 * @author Thomas Freese
 */
public class SimulationDemo extends JFrame
{
	/**
	 * 3 Simulationen = 3 Threads fuer parallel Betrieb
	 */
	public static final ExecutorService executorService = Executors.newFixedThreadPool(3,
			new SimulationThreadFactory("PicturePool"));

	/**
	 * 3 Simulationen = 3 Threads fuer parallel Betrieb
	 */
	public static final ScheduledExecutorService scheduledExecutorService = Executors
			.newScheduledThreadPool(3, new SimulationThreadFactory("SchedulePool"));

	/**
	 * 
	 */
	public static final Properties properties = new Properties();

	/**
	 *
	 */
	private static final long serialVersionUID = -8931412063622174282L;

	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				SimulationDemo demo = new SimulationDemo();

				demo.pack();
				demo.setResizable(false);
				demo.setLocationRelativeTo(null);
				demo.setVisible(true);
			}
		});
	}

	/**
	 * Erstellt ein neues {@link SimulationDemo} Object.
	 * 
	 * @throws HeadlessException Falls was schief geht.
	 */
	public SimulationDemo() throws HeadlessException
	{
		super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration());

		initialize();
	}

	/**
	 * Aufbau der GUI.
	 */
	private void initialize()
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			/**
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(final WindowEvent e)
			{
				SimulationDemo.scheduledExecutorService.shutdownNow();
				SimulationDemo.executorService.shutdownNow();
			}
		});

		try
		{
			InputStream inputStream =
					ClassLoader.getSystemResourceAsStream("simulation.properties");
			properties.load(inputStream);
			inputStream.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		int fieldWidth = Integer.parseInt(properties.getProperty("simulation.field.width", "180"));
		int fieldHeight =
				Integer.parseInt(properties.getProperty("simulation.field.height", "180"));

		JTabbedPane tabbedPane = new JTabbedPane();
		add(tabbedPane);

		AntView antView = new AntView();
		antView.initialize(fieldWidth, fieldHeight);
		tabbedPane.addTab("Ameise", antView.getMainPanel());

		GofView gofView = new GofView();
		gofView.initialize(fieldWidth, fieldHeight);
		tabbedPane.addTab("Game of Life", gofView.getMainPanel());

		WaTorView waTorView = new WaTorView();
		waTorView.initialize(fieldWidth, fieldHeight);
		tabbedPane.addTab("WaTor", waTorView.getMainPanel());

		WaTorDiagrammPanel waTorDiagrammPanel = new WaTorDiagrammPanel(waTorView.getModel());
		tabbedPane.addTab("WaTor-Diagramm", waTorDiagrammPanel);
	}
}
