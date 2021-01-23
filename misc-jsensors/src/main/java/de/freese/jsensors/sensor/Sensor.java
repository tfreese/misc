// Created: 12.05.2017
package de.freese.jsensors.sensor;

import java.util.List;
import de.freese.jsensors.backend.Backend;

/**
 * {@link Sensor} zum Erfassen eines oder mehrerer Werte.<br>
 *
 * @author Thomas Freese
 */
public interface Sensor
{
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
     * Werte ermitteln und an das Backend übergeben.
     */
    public void measure();

    /**
     * {@link Backend} an dem der Sensor die Werte übergibt.
     *
     * @param backend {@link Backend}
     */
    public void setBackend(Backend backend);
}
