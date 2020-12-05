// Created: 03.12.2020
package de.freese.jconky.monitor;

import de.freese.jconky.model.HostInfo;
import javafx.scene.canvas.GraphicsContext;

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
        gc.setFont(getSettings().getFont());

        double fontSize = getSettings().getFontSize();

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize * 1.25D;
        String text = String.format("%s - %s on %s", this.hostInfo.getName(), this.hostInfo.getVersion(), this.hostInfo.getArchitecture());
        gc.setFill(getSettings().getColorText());
        gc.fillText(text, x, y);

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
        HostInfo newInfo = getSystemMonitor().getHostInfo();

        this.hostInfo = newInfo;
    }
}
