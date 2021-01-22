// Created: 12.05.2017
package de.freese.jsensors.sensor;

import java.util.List;
import de.freese.jsensors.SensorRegistry;

/**
 * {@link Sensor} zum Erfassen eines oder mehrerer Werte.<br>
 *
 * @author Thomas Freese
 */
public interface Sensor
{
    /**
     * Mit der {@link SensorRegistry} verknüpfen, zu der die Werte weitergeleitet werden.
     *
     * @param registry {@link SensorRegistry}
     */
    public void bindTo(SensorRegistry registry);

    /**
     * Liefert die Namen der Werte, welche der Sensor erfassen kann.<br>
     * Beispiel:<br>
     * - network.in<br>
     * - network.out<br>
     *
     * @return {@link List}
     */
    public List<String> getNames();

    /**
     * Werte ermitteln und an die Registry weiterleiten.
     */
    public void measure();
}
