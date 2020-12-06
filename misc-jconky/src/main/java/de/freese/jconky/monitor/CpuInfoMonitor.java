// Created: 05.12.2020
package de.freese.jconky.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.freese.jconky.model.CpuInfo;
import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.CpuTimes;
import de.freese.jconky.model.Values;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * @author Thomas Freese
 */
public class CpuInfoMonitor extends AbstractMonitor
{
    /**
     *
     */
    private Map<Integer, Values<Double>> coreUsageMap = new HashMap<>();

    /**
    *
    */
    private CpuInfos cpuInfosCurrent = new CpuInfos();

    /**
    *
    */
    private CpuInfos cpuInfosPrevious = new CpuInfos();

    /**
     *
     */
    private final Stop[] gradientStops;

    /**
     *
     */
    private CpuLoadAvg loadAvg = new CpuLoadAvg();

    /**
     * Erstellt ein neues {@link CpuInfoMonitor} Object.
     */
    public CpuInfoMonitor()
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
     * @param core int
     * @return double Höhe
     */
    private double paintCore(final GraphicsContext gc, final double width, final int core)
    {
        double fontSize = getSettings().getFontSize();

        double x = 0D;
        double y = 0D;

        gc.setFill(getSettings().getColorText());

        CpuInfo cpuInfoCurrent = this.cpuInfosCurrent.get(core);
        CpuInfo cpuInfoPrevious = this.cpuInfosPrevious.get(core);

        double usage = cpuInfoCurrent.getCpuTimes().getCpuUsage(cpuInfoPrevious.getCpuTimes());
        int frequency = cpuInfoCurrent.getFrequency() / 1000;
        double temperature = cpuInfoCurrent.getTemperature();

        String text = null;

        if (temperature > 0D)
        {
            text = String.format("Core%d%3.0f%% %4dMHz %2.0f°C", core, usage * 100D, frequency, temperature);
        }
        else
        {
            text = String.format("Core%d%3.0f%% %4dMhz", core, usage * 100D, frequency);
        }

        gc.fillText(text, x, y);

        // x = 0D;
        // y = -4D;
        // gc.setStroke(getSettings().getColorTitle());
        // gc.setLineDashes(5D);
        // gc.strokeLine(x, y, width, y);
        // gc.setLineDashes();

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
     * @return double Höhe
     */
    private double paintCores(final GraphicsContext gc, final double width)
    {
        double fontSize = getSettings().getFontSize();

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize;

        int numCpus = this.cpuInfosCurrent.getNumCpus();
        double coreWidth = width - getSettings().getMarginInner().getLeft() - getSettings().getMarginInner().getRight();

        for (int core = 0; core < numCpus; core++)
        {
            gc.save();
            gc.translate(x, y);
            y += paintCore(gc, coreWidth, core);
            gc.restore();
        }

        return y;
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @return double Höhe
     */
    private double paintTotal(final GraphicsContext gc, final double width)
    {
        double fontSize = getSettings().getFontSize();

        gc.setFont(getSettings().getFont());

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize * 1.25D;
        gc.setFill(getSettings().getColorTitle());
        gc.fillText("CPU", x, y);

        x = fontSize * 3D;
        y = fontSize;
        gc.setStroke(getSettings().getColorTitle());
        gc.setLineDashes(5D);
        gc.strokeLine(x, y, width - getSettings().getMarginInner().getRight(), y);
        gc.setLineDashes();

        // CpuLoads
        x = getSettings().getMarginInner().getLeft();
        y += fontSize + 5D;
        gc.setFill(getSettings().getColorText());
        gc.fillText("Total", x, y);

        x = width - (fontSize * 13D);
        gc.fillText("Loads:", x, y);

        x += fontSize * 4D;
        String loads = String.format("%.2f %.2f %.2f", this.loadAvg.getOneMinute(), this.loadAvg.getFiveMinutes(), this.loadAvg.getFifteenMinutes());
        gc.setFill(getSettings().getColorValue());
        gc.fillText(loads, x, y);

        // CpuUsage Bar
        x = getSettings().getMarginInner().getLeft();
        y += 15D;

        gc.save();
        gc.translate(x, y);
        y += paintTotalBar(gc, width - x - getSettings().getMarginInner().getRight());
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
     * @return double Höhe
     */
    private double paintTotalBar(final GraphicsContext gc, final double width)
    {
        double height = 15D;
        double fontSize = getSettings().getFontSize();

        double x = 0D;
        double y = 0D;

        CpuTimes cpuTimesCurrent = this.cpuInfosCurrent.getTotal().getCpuTimes();
        CpuTimes cpuTimesPrevious = this.cpuInfosPrevious.getTotal().getCpuTimes();
        double usage = cpuTimesCurrent.getCpuUsage(cpuTimesPrevious);

        String totalUsage = String.format("%3.0f%%", usage * 100D);
        gc.setFill(getSettings().getColorValue());
        gc.fillText(totalUsage, x, y);

        x += 35D;
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
     * @see de.freese.jconky.monitor.Monitor#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        double y = paintTotal(gc, width);

        gc.save();
        gc.translate(0, y);
        y += paintCores(gc, width);
        gc.restore();

        double height = y - 10D;
        drawDebugBorder(gc, width, height);

        return height;
    }

    /**
     * @see de.freese.jconky.monitor.Monitor#updateValue()
     */
    @Override
    public void updateValue()
    {
        this.loadAvg = getSystemMonitor().getCpuLoadAvg();

        this.cpuInfosPrevious = this.cpuInfosCurrent;
        this.cpuInfosCurrent = getSystemMonitor().getCpuInfos();

        // CpuUsages berechnen.
        CpuTimes cpuTimesCurrent = this.cpuInfosCurrent.getTotal().getCpuTimes();
        CpuTimes cpuTimesPrevious = this.cpuInfosPrevious.getTotal().getCpuTimes();
        double usage = cpuTimesCurrent.getCpuUsage(cpuTimesPrevious);

        this.coreUsageMap.computeIfAbsent(-1, key -> new Values<>()).addValue(usage);

        for (int i = 0; i < this.cpuInfosCurrent.getNumCpus(); i++)
        {
            cpuTimesCurrent = this.cpuInfosCurrent.get(i).getCpuTimes();
            cpuTimesPrevious = this.cpuInfosPrevious.get(i).getCpuTimes();
            usage = cpuTimesCurrent.getCpuUsage(cpuTimesPrevious);

            this.coreUsageMap.computeIfAbsent(i, key -> new Values<>()).addValue(usage);
        }
    }
}
