// Created: 10.12.2020
package de.freese.jconky.painter;

import de.freese.jconky.model.NetworkInfo;
import de.freese.jconky.model.NetworkInfos;
import de.freese.jconky.model.NetworkProtocolInfo;
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

        gc.setFill(getSettings().getColorText());
        gc.fillText("Download:", x, y);

        return y;
    }

    /**
     * @see de.freese.jconky.painter.MonitorPainter#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        NetworkInfos networkInfos = getContext().getNetworkInfos();
        NetworkProtocolInfo protocolInfo = networkInfos.getProtocolInfo();
        String externalIp = getContext().getExternalIp();

        gc.setFont(getSettings().getFont());

        double fontSize = getSettings().getFontSize();

        NetworkInfo eth0 = networkInfos.getByName("eth0");

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize * 1.25D;
        gc.setFill(getSettings().getColorTitle());
        String text = String.format("Network: %s -> %s", eth0.getIp(), externalIp);
        gc.fillText(text, x, y);

        x = width - 20D;
        y = fontSize;
        gc.setStroke(getSettings().getColorTitle());
        gc.setLineDashes(5D);
        gc.strokeLine(x, y, width - getSettings().getMarginInner().getRight(), y);
        gc.setLineDashes();

        y += fontSize * 1.5D;

        y += paintInterface(gc, y, width, eth0);

        x = getSettings().getMarginInner().getLeft();
        y += fontSize * 1.5D;
        gc.setFill(getSettings().getColorText());
        gc.fillText("TCP-Connections:", x, y);
        x += fontSize * 10D;
        gc.setFill(getSettings().getColorValue());
        gc.fillText(Integer.toString(protocolInfo.getTcpConnections()), x, y);

        double height = y + 5D;
        drawDebugBorder(gc, width, height);

        return height;
    }
}
