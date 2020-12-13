// Created: 13.12.2020
package de.freese.jconky.painter;

import de.freese.jconky.Context;
import de.freese.jconky.Settings;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * @author Thomas Freese
 */
public abstract class AbstractMonitorPainter implements MonitorPainter
{
    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @param height double
     */
    protected void drawDebugBorder(final GraphicsContext gc, final double width, final double height)
    {
        if (getSettings().isDebug())
        {
            // gc.setLineDashes();
            gc.setStroke(Color.RED);
            gc.strokeRect(0, 0, width, height);
        }
    }

    /**
     * @return {@link Context}
     */
    protected Context getContext()
    {
        return Context.getInstance();
    }

    /**
     * @return {@link Settings}
     */
    protected Settings getSettings()
    {
        return Settings.getInstance();
    }
}
