// Created: 03.12.2020
package de.freese.jconky.monitor;

import de.freese.jconky.model.HostInfo;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

/**
 * @author Thomas Freese
 */
public class HostInfoMonitor extends AbstractMonitor
{
    /**
     *
     */
    private HostInfo hostInfo = new HostInfo();

    /**
     * @see de.freese.jconky.monitor.Monitor#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        Font font = new Font(getSettings().getFontName(), getSettings().getFontSize());
        gc.setFont(font);

        gc.setFill(getSettings().getColorText());

        double y = getSettings().getFontSize() * 1.5D;

        String text = String.format("%s - %s on %s", this.hostInfo.getName(), this.hostInfo.getVersion(), this.hostInfo.getArchitecture());
        gc.fillText(text, 10D, y);

        double height = getSettings().getFontSize() * 2D;

        drawDebugBorder(gc, width, height);

        return height;
    }

    /**
     * @see de.freese.jconky.monitor.Monitor#updateValue()
     */
    @Override
    public void updateValue()
    {
        HostInfo newInfo = getSystemMonitor().getHostInfo();

        this.hostInfo = newInfo;
    }
}
