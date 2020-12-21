// Created: 10.12.2020
package de.freese.jconky.painter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.freese.jconky.model.NetworkInfo;
import de.freese.jconky.model.NetworkInfos;
import de.freese.jconky.model.NetworkProtocolInfo;
import de.freese.jconky.model.Values;
import de.freese.jconky.util.JConkyUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * @author Thomas Freese
 */
public class NetworkMonitorPainter extends AbstractMonitorPainter
{
    /**
    *
    */
    private Map<String, Values<Double>> downloadMap = new HashMap<>();

    /**
    *
    */
    private final Stop[] gradientStops;

    /**
    *
    */
    private Map<String, Values<Double>> uploadMap = new HashMap<>();

    /**
     * Erstellt ein neues {@link NetworkMonitorPainter} Object.
     */
    public NetworkMonitorPainter()
    {
        super();

        this.gradientStops = new Stop[]
        {
                new Stop(0D, Color.WHITE), new Stop(1D, getSettings().getColorValue())
        };
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @param networkInfo {@link NetworkInfo}
     * @return double
     */
    private double paintInterface(final GraphicsContext gc, final double width, final NetworkInfo networkInfo)
    {
        double fontSize = getSettings().getFontSize();

        double x = 0D;
        double y = 0D;
        paintTextAndValue(gc, "Download:", JConkyUtils.toHumanReadableSize(networkInfo.getDownloadPerSecond()), x, y);

        x = width - (fontSize * 10.5D);
        paintTextAndValue(gc, "Upload:", JConkyUtils.toHumanReadableSize(networkInfo.getUploadPerSecond()), x, y);

        int graphWidth = (int) (width / 2) - 10;
        int graphHeight = 40;

        x = 0D;
        y += fontSize - 4D;

        gc.save();
        gc.translate(x, y);
        paintInterfaceGraph(gc, graphWidth, graphHeight, this.downloadMap.computeIfAbsent(networkInfo.getInterfaceName(), key -> new Values<>()));
        gc.restore();

        gc.save();
        gc.translate(x + graphWidth + 20D, y);
        paintInterfaceGraph(gc, graphWidth, graphHeight, this.uploadMap.computeIfAbsent(networkInfo.getInterfaceName(), key -> new Values<>()));
        gc.restore();

        y += graphHeight + fontSize + 3;
        paintTextAndValue(gc, String.format("%s: Total:", networkInfo.getInterfaceName()), JConkyUtils.toHumanReadableSize(networkInfo.getBytesReceived()), x,
                y);

        x = width - (fontSize * 10.5D);
        paintTextAndValue(gc, "Total:", JConkyUtils.toHumanReadableSize(networkInfo.getBytesTransmitted()), x, y);

        return y;
    }

    /**
     * @param gc {@link GraphicsContext}
     * @param width double
     * @param height double
     * @param values {@link Values}
     */
    private void paintInterfaceGraph(final GraphicsContext gc, final double width, final double height, final Values<Double> values)
    {
        List<Double> valueList = values.getLastValues((int) width);

        double minValue = 0D;
        // double maxValue = 28D * 1024D * 1024D; // 28 MB/s als max. bei 200er Leitung.
        double maxValue = values.getMaxValue();
        double minNorm = 0D;
        double maxNorm = height - 2;

        // gc.setFill(new LinearGradient(0D, height - 2, 0D, 0D, false, CycleMethod.NO_CYCLE, this.gradientStops));
        gc.setStroke(new LinearGradient(0D, height - 2, 0D, 0D, false, CycleMethod.NO_CYCLE, this.gradientStops));

        double xOffset = width - valueList.size(); // Diagramm von rechts aufbauen.
        // double xOffset = 0D; // Diagramm von links aufbauen.

        for (int i = 0; i < valueList.size(); i++)
        {
            double value = valueList.get(i);
            double x = i + xOffset;
            double y = minNorm + (((value - minValue) * (maxNorm - minNorm)) / (maxValue - minValue));

            // gc.fillRect(x, height - 1 - y, 1, y);
            gc.strokeLine(x, height - 1 - y, x, height - 1);
        }

        // drawDebugBorder(gc, width, height);
    }

    /**
     * @see de.freese.jconky.painter.MonitorPainter#paintValue(javafx.scene.canvas.GraphicsContext, double)
     */
    @Override
    public double paintValue(final GraphicsContext gc, final double width)
    {
        NetworkInfos networkInfos = getContext().getNetworkInfos();
        NetworkInfo eth0 = networkInfos.getByName("eth0");

        this.downloadMap.computeIfAbsent(eth0.getInterfaceName(), key -> new Values<>()).addValue(eth0.getDownloadPerSecond());
        this.uploadMap.computeIfAbsent(eth0.getInterfaceName(), key -> new Values<>()).addValue(eth0.getUploadPerSecond());

        NetworkProtocolInfo protocolInfo = networkInfos.getProtocolInfo();
        String externalIp = getContext().getExternalIp();

        gc.setFont(getSettings().getFont());

        double fontSize = getSettings().getFontSize();

        double x = getSettings().getMarginInner().getLeft();
        double y = fontSize * 1.25D;
        paintTitle(gc, String.format("Network: %s -> %s", eth0.getIp(), externalIp), x, y, width);

        x = getSettings().getMarginInner().getLeft();
        y += fontSize * 1.5D;

        gc.save();
        gc.translate(x, y);
        y += paintInterface(gc, width - x - getSettings().getMarginInner().getRight(), eth0);
        gc.restore();

        y += fontSize * 1.2D;
        paintTextAndValue(gc, "TCP-Connections:", Integer.toString(protocolInfo.getTcpConnections()), x, y);

        double height = y + 5D;
        drawDebugBorder(gc, width, height);

        return height;
    }
}
