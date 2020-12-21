// Created: 03.12.2020
package de.freese.jconky.painter;

import de.freese.jconky.model.HostInfo;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 */
public class HostMonitorPainter extends AbstractMonitorPainter
{
    /**
     * @see de.freese.jconky.painter.MonitorPainter#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        HostInfo hostInfo = getContext().getHostInfo();

        gc.setFont(getSettings().getFont());

        double fontSize = getSettings().getFontSize();

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize * 1.25D;
        paintText(gc, String.format("%s - %s on %s", hostInfo.getName(), hostInfo.getVersion(), hostInfo.getArchitecture()), x, y);

        double height = y + 5D;
        drawDebugBorder(gc, width, height);

        return height;
    }
}
