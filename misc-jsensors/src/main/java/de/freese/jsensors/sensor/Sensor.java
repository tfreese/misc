// Created: 12.05.2017
package de.freese.jsensors.sensor;

import de.freese.jsensors.backend.Backend;

/**
 * {@link Sensor} der einen zu messenden Wert liefert.<br>
 *
 * @author Thomas Freese
 */
public interface Sensor
{
    /**
     * Liefert den Namen des Sensors.<br>
     *
     * @return String
     */
    public String getName();

    /**
     * Messen des Wertes.
     */
    public void scan();

    /**
     * Setzt das {@link Backend} für das Speichern des Sensorwertes.
     *
     * @param backend {@link Backend}
     */
    public void setBackend(Backend backend);

    /**
     * Dieser Sensor soll eine eigene Datei oder Datenbank-Tabelle bekommen.
     *
     * @param exclusive boolean
     */
    public void setExclusive(boolean exclusive);
}
