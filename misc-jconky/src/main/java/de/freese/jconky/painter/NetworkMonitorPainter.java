// Created: 10.12.2020
package de.freese.jconky.painter;

import de.freese.jconky.model.NetworkInfo;
import de.freese.jconky.model.NetworkInfos;
import javafx.scene.canvas.GraphicsContext;

/**
 * @author Thomas Freese
 */
public class NetworkMonitorPainter extends AbstractMonitorPainter
{
    /**
     * @param gc {@link GraphicsContext}
     * @param y double
     * @param width double
     * @param networkInfo {@link NetworkInfo}
     */
    private double paintInterface(final GraphicsContext gc, final double y, final double width, final NetworkInfo networkInfo)
    {
        double x = getSettings().getMarginInner().getLeft();

        // TODO

        return y;
    }

    /**
     * @see de.freese.jconky.painter.MonitorPainter#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        NetworkInfos infos = getContext().getNetworkInfos();
        String externalIp = getContext().getExternalIp();

        gc.setFont(getSettings().getFont());

        double fontSize = getSettings().getFontSize();

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize * 1.25D;
        gc.setFill(getSettings().getColorTitle());
        gc.fillText("Network", x, y);

        x = fontSize * 6.5D;
        y = fontSize;
        gc.setStroke(getSettings().getColorTitle());
        gc.setLineDashes(5D);
        gc.strokeLine(x, y, width - getSettings().getMarginInner().getRight(), y);
        gc.setLineDashes();

        y += fontSize * 1.5D;

        y += paintInterface(gc, y, width, infos.getByName("eth0"));

        double height = y + 5D;
        drawDebugBorder(gc, width, height);

        return height;
    }
}
