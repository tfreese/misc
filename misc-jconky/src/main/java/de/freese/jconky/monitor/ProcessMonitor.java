// Created: 10.12.2020
package de.freese.jconky.monitor;

import de.freese.jconky.model.ProcessInfo;
import de.freese.jconky.model.ProcessInfos;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 */
public class ProcessMonitor extends AbstractMonitor
{
    /**
     *
     */
    private ProcessInfos processInfos = new ProcessInfos();

    /**
     * Erstellt ein neues {@link ProcessMonitor} Object.
     */
    public ProcessMonitor()
    {
        super();
    }

    /**
     * @see de.freese.jconky.monitor.Monitor#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        ProcessInfos infos = this.processInfos;

        gc.setFont(getSettings().getFont());

        double fontSize = getSettings().getFontSize();

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize * 1.25D;
        gc.setFill(getSettings().getColorTitle());
        gc.fillText("Processes", x, y);

        x = fontSize * 6.5D;
        y = fontSize;
        gc.setStroke(getSettings().getColorTitle());
        gc.setLineDashes(5D);
        gc.strokeLine(x, y, width - getSettings().getMarginInner().getRight(), y);
        gc.setLineDashes();

        // Alive
        x = getSettings().getMarginInner().getLeft();
        y += fontSize * 1.5D;
        gc.setFill(getSettings().getColorText());
        gc.fillText("Alive:", x, y);

        x += 50D;
        gc.setFill(getSettings().getColorValue());
        gc.fillText(Integer.toString(infos.size()), x, y);

        // Running
        x += 50D;
        gc.setFill(getSettings().getColorText());
        gc.fillText("Running:", x, y);

        x += 60D;
        gc.setFill(getSettings().getColorValue());
        gc.fillText(Integer.toString(infos.getRunning()), x, y);

        // Uptime
        x += 45D;
        gc.setFill(getSettings().getColorText());
        gc.fillText("Uptime:", x, y);

        x += 55D;
        gc.setFill(getSettings().getColorValue());
        gc.fillText(infos.getUptime(), x, y);

        // Highest Usage
        x = getSettings().getMarginInner().getLeft();
        y += fontSize * 1.5D;

        gc.setFill(getSettings().getColorText());
        gc.fillText("Highest Usage", x, y);
        String text = String.format("%8s %8s %8s", "PID", "CPU%", "MEM%");
        gc.fillText(text, x + 100D, y);

        y += fontSize * 1.5D;

        long totalSystemMemory = getSystemMonitor().getTotalSystemMemory();

        for (ProcessInfo processInfo : infos.getSortedByCpuUsage(3))
        {
            gc.fillText(processInfo.getName(), x, y);

            double memoryUsage = (double) processInfo.getResidentBytes() / totalSystemMemory;

            text = String.format("%8d %8.2f %8.2f", processInfo.getPid(), processInfo.getCpuUsage() * 100D, memoryUsage * 100D);
            gc.fillText(text, x + 100D, y);

            y += fontSize * 1.5D;
        }

        y += fontSize * 1.5D;

        // Highest Memory
        gc.setFill(getSettings().getColorText());
        gc.fillText("Highest Memory", x, y);
        text = String.format("%8s %8s %8s", "PID", "MEM%", "CPU%");
        gc.fillText(text, x + 100D, y);

        y += fontSize * 1.5D;

        for (ProcessInfo processInfo : infos.getSortedByMemoryUsage(3))
        {
            gc.fillText(processInfo.getName(), x, y);

            double memoryUsage = (double) processInfo.getResidentBytes() / totalSystemMemory;

            text = String.format("%8d %8.2f %8.2f", processInfo.getPid(), memoryUsage * 100D, processInfo.getCpuUsage() * 100D);
            gc.fillText(text, x + 100D, y);

            y += fontSize * 1.5D;
        }

        double height = y + 5D;
        drawDebugBorder(gc, width, height);

        return height;
    }

    /**
     * @see de.freese.jconky.monitor.Monitor#updateValue()
     */
    @Override
    public void updateValue()
    {
        this.processInfos = getSystemMonitor().getProcessInfos();
    }
}
