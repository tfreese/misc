// Created: 04.12.2020
package de.freese.jconky;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import de.freese.jconky.monitor.Monitor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 */
public class JConkyPainter
{
    /**
     *
     */
    private final Canvas canvas;

    /**
     *
     */
    private final GraphicsContext gc;

    /**
     *
     */
    private final List<Monitor> monitors = new ArrayList<>();

    /**
     * Erstellt ein neues {@link JConkyPainter} Object.
     *
     * @param canvas {@link Canvas}
     */
    public JConkyPainter(final Canvas canvas)
    {
        super();

        this.canvas = Objects.requireNonNull(canvas, "canvas required");
        this.gc = Objects.requireNonNull(canvas.getGraphicsContext2D(), "graphicsContext required");
    }

    /**
     * @param monitor {@link Monitor}
     */
    public void addMonitor(final Monitor monitor)
    {
        this.monitors.add(monitor);
        this.monitors.add(monitor);
        this.monitors.add(monitor);
    }

    /**
     *
     */
    public void paint()
    {
        double width = this.canvas.getWidth();
        double height = this.canvas.getHeight();

        this.gc.clearRect(0, 0, width, height);

        double totalY = 0D;

        for (Monitor monitor : this.monitors)
        {
            double monitorHeight = monitor.paintValue(this.gc, width);

            this.gc.translate(0, monitorHeight);

            totalY += monitorHeight;
        }

        // Koordinatenursprung wieder nach oben links verlegen um es komplett malen zu lassen.
        this.gc.translate(0, -totalY);
    }
}