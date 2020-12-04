// Created: 03.12.2020
package de.freese.jconky.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.jconky.Settings;
import de.freese.jconky.system.SystemMonitor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * @author Thomas Freese
 */
public abstract class AbstractMonitor implements Monitor
{
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @param height double
     */
    protected void drawDebugBorder(final GraphicsContext gc, final double width, final double height)
    {
        if (getSettings().isDebug())
        {
            gc.setStroke(Color.RED);
            gc.strokeRect(0, 0, width, height);
        }
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link Settings}
     */
    protected Settings getSettings()
    {
        return Settings.getInstance();
    }

    /**
     * @return {@link SystemMonitor}
     */
    protected SystemMonitor getSystemMonitor()
    {
        return getSettings().getSystemMonitor();
    }
}
