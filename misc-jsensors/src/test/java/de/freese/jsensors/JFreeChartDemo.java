/**
 * Created: 02.06.2017
 */

package de.freese.jsensors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.WindowConstants;
import org.hsqldb.jdbc.JDBCPool;
import org.jfree.chart.ChartFrame;
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

/**
 * @author Thomas Freese
 */
public class JFreeChartDemo
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        String sensor = "cpu.usage";

        JDBCPool dataSource = new JDBCPool();
        dataSource.setUrl("jdbc:hsqldb:file:logs/hsqldb/sensordb;create=false;shutdown=true");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        TimeSeries timeSeries = new TimeSeries(sensor);

        // String sql = "select * from SENSORS where NAME = ? order by TIMESTAMP asc";
        String sql = String.format("select * from %s order by TIMESTAMP asc", sensor.replace('.', '_').toUpperCase());

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);)
        {
            // statement.setString(1, sensor);

            try (ResultSet resultSet = statement.executeQuery())
            {
                while (resultSet.next())
                {
                    final long timestamp = resultSet.getLong("TIMESTAMP");
                    final double value = resultSet.getDouble("VALUE");

                    final RegularTimePeriod timePeriod = new FixedMillisecond(timestamp);
                    timeSeries.addOrUpdate(timePeriod, value);
                }
            }
        }

        dataSource.close(1);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(timeSeries);

        Font font = new Font("Arial", Font.BOLD, 12);

        ValueAxis timeAxis = new DateAxis("Zeitachse");
        timeAxis.setLowerMargin(0.02D);
        timeAxis.setUpperMargin(0.02D);
        timeAxis.setAutoRange(true);
        // timeAxis.setFixedAutoRange(60000D);
        timeAxis.setTickLabelsVisible(true);
        timeAxis.setTickLabelFont(font);
        timeAxis.setLabelFont(font);

        NumberAxis valueAxis = new NumberAxis(sensor);
        valueAxis.setAutoRangeIncludesZero(false);
        valueAxis.setTickLabelFont(font);
        valueAxis.setLabelFont(font);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2.5F));

        XYPlot xyplot = new XYPlot(dataset, timeAxis, valueAxis, renderer);

        JFreeChart chart = new JFreeChart(null, null, xyplot, true);
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(font);

        ChartFrame chartFrame = new ChartFrame(sensor, chart, true);
        chartFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        chartFrame.setSize(1280, 800);
        chartFrame.setLocationRelativeTo(null);
        chartFrame.setVisible(true);
    }
}
