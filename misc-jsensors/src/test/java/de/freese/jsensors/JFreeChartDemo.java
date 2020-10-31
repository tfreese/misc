/**
 * Created: 02.06.2017
 */

package de.freese.jsensors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.WindowConstants;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 * @author Thomas Freese
 */
public class JFreeChartDemo
{
    /**
    *
    */
    static void jfreeChart()
    {
        // //File file = new File("/proc/loadavg");
        // // File file = new File("/proc/cpuinfo");
        // File file = new File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq");
        //
        // List<String> result = FileUtils.readLines(file);
        //
        // for (String line : result)
        // {
        // System.out.println(line);
        // }

        final SingleConnectionDataSource ds = new SingleConnectionDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://htpc2/RRDTOOL?user=...&password=...");
        ds.setSuppressClose(true);

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

        final TimeSeries timeSeries = new TimeSeries("RRDSENSOR");
        jdbcTemplate.query("select * from RRDVALUE where SENSOR_ID=11 and TIMESTAMP > ? order by TIMESTAMP asc", (ResultSetExtractor<Void>) rs -> {
            while (rs.next())
            {
                final long timestamp = rs.getLong("TIMESTAMP");
                final double value = rs.getDouble("VALUE");

                final RegularTimePeriod timePeriod = new FixedMillisecond(timestamp * 1000);
                timeSeries.addOrUpdate(timePeriod, value);

                // System.out.println(timePeriod + "; " + value);
            }

            return null;
        }, (System.currentTimeMillis() / 1000) - 36000);

        ds.destroy();

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(timeSeries);

        final Font font = new Font("Arial", Font.BOLD, 12);

        final ValueAxis timeAxis = new DateAxis("Zeitachse");
        timeAxis.setLowerMargin(0.02D);
        timeAxis.setUpperMargin(0.02D);
        timeAxis.setAutoRange(true);
        // timeAxis.setFixedAutoRange(60000D);
        timeAxis.setTickLabelsVisible(true);
        timeAxis.setTickLabelFont(font);
        timeAxis.setLabelFont(font);

        final NumberAxis valueAxis = new NumberAxis("RRDSENSOR");
        valueAxis.setAutoRangeIncludesZero(false);
        valueAxis.setTickLabelFont(font);
        valueAxis.setLabelFont(font);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, new BasicStroke(2.5F));

        final XYPlot xyplot = new XYPlot(dataset, timeAxis, valueAxis, renderer);

        final JFreeChart chart = new JFreeChart(null, null, xyplot, true);
        final LegendTitle legend = chart.getLegend();
        legend.setItemFont(font);

        final ChartFrame chartFrame = new ChartFrame("RRDSENSOR", chart, true);
        chartFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        chartFrame.setSize(1280, 768);
        chartFrame.setLocationRelativeTo(null);
        chartFrame.setVisible(true);
    }

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // String line = "RX packets 32997 bytes 46685918 (44.5 MiB)";
        // String line = "TX packets 15894 bytes 1288395 (1.2 MiB)";
        //
        // Pattern pattern = Pattern.compile(" bytes (.+?) ");
        // Matcher matcher = pattern.matcher(line);
        //
        // matcher.find();
        // long value = Long.valueOf(matcher.group(1));
        // System.out.println(value);

        mariaDB();
        jfreeChart();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    static void mariaDB() throws Exception
    {
        // Test mariadb
        try (Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/jsensors?user=tommy&password=tommy");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select CURRENT_DATE"))
        {
            resultSet.next();

            System.out.println(resultSet.getString(1));
        }
    }
}
