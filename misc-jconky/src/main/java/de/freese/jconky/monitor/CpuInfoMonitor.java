// Created: 05.12.2020
package de.freese.jconky.monitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.CpuTimes;
import de.freese.jconky.model.Values;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
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
    private final Stop[] gradientStops = new Stop[]
    {
            new Stop(0D, Color.GREEN), new Stop(1D, Color.RED)
    };

    /**
     * Da wir eine feste Höhe haben brauchen wir den nur einmal zu erzeugen.
     */
    private final LinearGradient linearGradientTotalUsageGraph;

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

        this.linearGradientTotalUsageGraph = new LinearGradient(0D, 0D, 0D, 20D, false, CycleMethod.NO_CYCLE, this.gradientStops);

    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @return double; momentane Höhe
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

        // CpuUsage
        x = getSettings().getMarginInner().getLeft();
        y += fontSize + 5D;
        CpuTimes cpuTimesCurrent = this.cpuInfosCurrent.getTotal().getCpuTimes();
        CpuTimes cpuTimesPrevious = this.cpuInfosPrevious.getTotal().getCpuTimes();
        String totalUsage = String.format("%3.0f%%", cpuTimesCurrent.getUsage(cpuTimesPrevious));
        gc.setFill(getSettings().getColorValue());
        gc.fillText(totalUsage, x, y);

        x += 35D;
        double barWidth = width - getSettings().getMarginInner().getRight() - x;
        Values<Double> totalUsageValues = this.coreUsageMap.computeIfAbsent(-1, key -> new Values<>());
        List<Double> values = totalUsageValues.getLastValues((int) barWidth);

        gc.setLineDashes();
        gc.setStroke(getSettings().getColorText());
        gc.strokeRect(x, y - fontSize, barWidth, 15D);

        gc.setFill(new LinearGradient(x, y - fontSize, barWidth, y - fontSize, false, CycleMethod.NO_CYCLE, this.gradientStops));
        double barValue = 0D;

        if (!values.isEmpty())
        {
            barValue = (values.get(values.size() - 1) * barWidth) / 100D; // Prozent in Pixels umrechnen
        }

        gc.fillRect(x, y - fontSize, barValue, 15D);

        // gc.setFill(linearGradientTotalUsageGraph);
        // Values<Double> totalUsageValues = this.coreUsageMap.computeIfAbsent(-1, key -> new Values<>());
        // List<Double> values = totalUsageValues.getLastValues((int) width);
        //
        // double xOffset = width - values.size(); // Diagramm von rechts aufbauen.
        // // double xOffset = 0F; // Diagramm von links aufbauen.
        //
        // for (int i = 0; i < values.size(); i++)
        // {
        // double value = values.get(i);
        //
        // double graphX = i + xOffset;
        // double graphY = value;
        //
        // if (value > 0D)
        // {
        // gc.fillRect(x, middle - y, 1, y);
        // }
        // else
        // {
        // gc.fillRect(x, middle, 1, y);
        // }
        // }

        return y;
    }

    /**
     * @see de.freese.jconky.monitor.Monitor#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        double yTotal = paintTotal(gc, width);

        double height = yTotal + 5D;
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
        double usage = cpuTimesCurrent.getUsage(cpuTimesPrevious);

        this.coreUsageMap.computeIfAbsent(-1, key -> new Values<>()).addValue(usage);

        for (int i = 0; i < this.cpuInfosCurrent.getNumCpus(); i++)
        {
            cpuTimesCurrent = this.cpuInfosCurrent.get(i).getCpuTimes();
            cpuTimesPrevious = this.cpuInfosPrevious.get(i).getCpuTimes();
            usage = cpuTimesCurrent.getUsage(cpuTimesPrevious);

            this.coreUsageMap.computeIfAbsent(i, key -> new Values<>()).addValue(usage);
        }
    }
}
