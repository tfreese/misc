// Created: 09.10.2009
/**
 * 09.10.2009
 */
package de.freese.simulationen.wator;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import de.freese.simulationen.model.AbstractWorld;
import de.freese.simulationen.model.WorldListener;

/**
 * DiagrammPanel der WaTor-Simulation.
 * 
 * @author Thomas Freese
 */
public class WaTorDiagrammPanel extends JPanel implements WorldListener
{
	/**
	 *
	 */
	private static final long serialVersionUID = -7891438395009637657L;

	/**
	 *
	 */
	private TimeSeries timeSeriesFische = new TimeSeries("Fische");

	/**
	 *
	 */
	private TimeSeries timeSeriesHaie = new TimeSeries("Haie");

	/**
	 * Erstellt ein neues {@link WaTorDiagrammPanel} Object.
	 * 
	 * @param world {@link WaTorWorld}
	 */
	public WaTorDiagrammPanel(final WaTorWorld world)
	{
		super();

		world.addWorldListener(this);

		initialize();
	}

	/**
	 * @see de.freese.simulationen.model.WorldListener#cellColorChanged(int, int, java.awt.Color)
	 */
	@Override
	public void cellColorChanged(final int x, final int y, final Color color)
	{
		// Nix zu tun
	}

	/**
	 * @return {@link TimeSeries}
	 */
	private TimeSeries getTimeSeriesFische()
	{
		return this.timeSeriesFische;
	}

	/**
	 * @return {@link TimeSeries}
	 */
	private TimeSeries getTimeSeriesHaie()
	{
		return this.timeSeriesHaie;
	}

	/**
	 * Aufbau der GUI.
	 */
	private void initialize()
	{
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(getTimeSeriesFische());
		dataset.addSeries(getTimeSeriesHaie());

		Font font = new Font("Arial", Font.BOLD, 12);

		ValueAxis timeAxis = new DateAxis("Zeitachse");
		timeAxis.setLowerMargin(0.02D);
		timeAxis.setUpperMargin(0.02D);
		timeAxis.setAutoRange(true);
		timeAxis.setFixedAutoRange(60000D);
		timeAxis.setTickLabelsVisible(true);
		timeAxis.setTickLabelFont(font);
		timeAxis.setLabelFont(font);

		NumberAxis valueAxis = new NumberAxis("Anzahl");
		valueAxis.setAutoRangeIncludesZero(false);
		valueAxis.setTickLabelFont(font);
		valueAxis.setLabelFont(font);
		// valueAxis.setAutoRange(true);
		// valueAxis.setFixedAutoRange(10000D);
		// valueAxis.setAutoTickUnitSelection(true);
		// valueAxis.setRange(0.0D, 20000D);

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
		renderer.setSeriesPaint(0, Color.GREEN);
		renderer.setSeriesStroke(0, new BasicStroke(2.5F));
		renderer.setSeriesPaint(1, Color.BLUE);
		renderer.setSeriesStroke(1, new BasicStroke(2.5F));

		XYPlot xyplot = new XYPlot(dataset, timeAxis, valueAxis, renderer);

		JFreeChart chart = new JFreeChart(null, null, xyplot, true);
		LegendTitle legend = chart.getLegend();
		legend.setItemFont(font);

		setLayout(new BorderLayout());
		add(new ChartPanel(chart), BorderLayout.CENTER);
	}

	/**
	 * @see de.freese.simulationen.model.WorldListener#worldChanged(de.freese.simulationen.model.AbstractWorld)
	 */
	@Override
	public void worldChanged(final AbstractWorld world)
	{
		WaTorWorld waTorWorld = (WaTorWorld) world;

		final int fishes = waTorWorld.getFishCounter();
		final int sharks = waTorWorld.getSharkCounter();

		Runnable runnable = new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				RegularTimePeriod timePeriod = new FixedMillisecond();

				getTimeSeriesFische().addOrUpdate(timePeriod, fishes);
				getTimeSeriesHaie().addOrUpdate(timePeriod, sharks);

				// getTimeSeriesFische().setDescription("" + fishes);
				// getTimeSeriesHaie().setDescription("" + sharks);
			}
		};

		if (SwingUtilities.isEventDispatchThread())
		{
			runnable.run();
		}
		else
		{
			SwingUtilities.invokeLater(runnable);
		}

		// SimulationDemo.executorService.execute(runnable);
	}
}
