// Created: 05.12.2020
package de.freese.jconky.painter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.freese.jconky.model.CpuInfo;
import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.Values;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * @author Thomas Freese
 */
public class CpuMonitorPainter extends AbstractMonitorPainter
{
    /**
     *
     */
    private Map<Integer, Values<Double>> coreUsageMap = new HashMap<>();

    /**
     *
     */
    private final Stop[] gradientStops;

    /**
     * Erstellt ein neues {@link CpuMonitorPainter} Object.
     */
    public CpuMonitorPainter()
    {
        super();

        this.gradientStops = new Stop[]
        {
                new Stop(0D, getSettings().getColorGradientStart()), new Stop(1D, getSettings().getColorGradientStop())
        };
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @param cpuInfo {@link CpuInfo}
     * @return double Höhe
     */
    private double paintCore(final GraphicsContext gc, final double width, final CpuInfo cpuInfo)
    {
        double fontSize = getSettings().getFontSize();

        double x = 0D;
        double y = 0D;

        int core = cpuInfo.getCore();
        double usage = cpuInfo.getCpuUsage();
        int frequency = cpuInfo.getFrequency() / 1000;
        double temperature = cpuInfo.getTemperature();

        String text = null;

        if (temperature > 0D)
        {
            text = String.format("Core%d%3.0f%% %4dMHz %2.0f°C", core, usage * 100D, frequency, temperature);
        }
        else
        {
            text = String.format("Core%d%3.0f%% %4dMhz", core, usage * 100D, frequency);
        }

        paintText(gc, text, x, y);

        x = fontSize * 14D;
        y = -fontSize + 3D;
        double barWidth = width - x;

        gc.setStroke(getSettings().getColorText());
        gc.strokeRect(x, y, barWidth, 10D);

        gc.setFill(new LinearGradient(x, y, x + barWidth, y, false, CycleMethod.NO_CYCLE, this.gradientStops));
        gc.fillRect(x, y, usage * barWidth, 10D);

        return fontSize * 1.25D;
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @param cpuInfos {@link CpuInfos}
     * @return double Höhe
     */
    private double paintCores(final GraphicsContext gc, final double width, final CpuInfos cpuInfos)
    {
        double fontSize = getSettings().getFontSize();

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize;

        double coreWidth = width - getSettings().getMarginInner().getLeft() - getSettings().getMarginInner().getRight();

        for (int i = 0; i < getContext().getNumberOfCores(); i++)
        {
            gc.save();
            gc.translate(x, y);
            y += paintCore(gc, coreWidth, cpuInfos.get(i));
            gc.restore();
        }

        return y;
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @param cpuInfos {@link CpuInfos}
     * @return double Höhe
     */
    private double paintTotal(final GraphicsContext gc, final double width, final CpuInfos cpuInfos)
    {
        CpuLoadAvg cpuLoadAvg = getContext().getCpuLoadAvg();

        double fontSize = getSettings().getFontSize();

        gc.setFont(getSettings().getFont());

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize * 1.25D;
        paintTitle(gc, "CPU", x, y, width);

        // CpuLoads
        x = getSettings().getMarginInner().getLeft();
        y += fontSize + 5D;
        paintText(gc, "Total", x, y);

        x = width - (fontSize * 13D);
        paintTextAndValue(gc, "Loads:", String.format("%.2f %.2f %.2f", cpuLoadAvg.getOneMinute(), cpuLoadAvg.getFiveMinutes(), cpuLoadAvg.getFifteenMinutes()),
                x, y);

        // CpuUsage Bar
        x = getSettings().getMarginInner().getLeft();
        y += 15D;

        gc.save();
        gc.translate(x, y);
        y += paintTotalBar(gc, width - x - getSettings().getMarginInner().getRight(), cpuInfos);
        gc.restore();

        // CpuUsage Graph
        x = getSettings().getMarginInner().getLeft();
        y -= fontSize;

        gc.save();
        gc.translate(x, y);
        y += paintTotalGraph(gc, width - x - getSettings().getMarginInner().getRight());
        gc.restore();

        return y;
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @param cpuInfos {@link CpuInfos}
     * @return double Höhe
     */
    private double paintTotalBar(final GraphicsContext gc, final double width, final CpuInfos cpuInfos)
    {
        double height = 15D;
        double fontSize = getSettings().getFontSize();

        double x = 0D;
        double y = 0D;

        double usage = cpuInfos.getTotal().getCpuUsage();
        double temperature = cpuInfos.getTotal().getTemperature();

        paintTextValue(gc, String.format("%3.0f%% %2.0f°C", usage * 100D, temperature), x, y);

        x += 70D;
        y += 3D;
        double barWidth = width - x;

        gc.setStroke(getSettings().getColorText());
        gc.strokeRect(x, y - fontSize, barWidth, 10D);

        y -= fontSize;
        gc.setFill(new LinearGradient(x, y, x + barWidth, y, false, CycleMethod.NO_CYCLE, this.gradientStops));
        gc.fillRect(x, y, usage * barWidth, 10D);

        return height;
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @return double Höhe
     */
    private double paintTotalGraph(final GraphicsContext gc, final double width)
    {
        List<Double> values = this.coreUsageMap.computeIfAbsent(-1, key -> new Values<>()).getLastValues((int) width);
        double height = 20D;

        // gc.setStroke(getSettings().getColorText());
        // gc.strokeRect(0D, 0D, width, height);

        // gc.setFill(new LinearGradient(0D, height - 2, 0D, 0D, false, CycleMethod.NO_CYCLE, this.gradientStops));
        gc.setStroke(new LinearGradient(0D, height - 2, 0D, 0D, false, CycleMethod.NO_CYCLE, this.gradientStops));

        double xOffset = width - values.size(); // Diagramm von rechts aufbauen.
        // double xOffset = 0D; // Diagramm von links aufbauen.

        for (int i = 0; i < values.size(); i++)
        {
            double value = values.get(i);

            double x = i + xOffset;
            double y = value * (height - 2);

            // gc.fillRect(x, height - 1 - y, 1, y);
            gc.strokeLine(x, height - 1 - y, x, height - 1);
        }

        return height;
    }

    /**
     * @see de.freese.jconky.painter.MonitorPainter#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        CpuInfos cpuInfos = getContext().getCpuInfos();

        this.coreUsageMap.computeIfAbsent(-1, key -> new Values<>()).addValue(cpuInfos.getTotal().getCpuUsage());

        for (int i = 0; i < getContext().getNumberOfCores(); i++)
        {
            this.coreUsageMap.computeIfAbsent(i, key -> new Values<>()).addValue(cpuInfos.get(i).getCpuUsage());
        }

        double y = paintTotal(gc, width, cpuInfos);

        gc.save();
        gc.translate(0, y);
        y += paintCores(gc, width, cpuInfos);
        gc.restore();

        double height = y - 10D;
        drawDebugBorder(gc, width, height);

        return height;
    }
}
