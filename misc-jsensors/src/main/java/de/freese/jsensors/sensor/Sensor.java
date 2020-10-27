// Created: 12.05.2017
package de.freese.jsensors.sensor;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.lifecycle.LifeCycle;

/**
 * {@link Sensor} der einen zu messenden Wert liefert.<br>
 *
 * @author Thomas Freese
 */
public interface Sensor extends LifeCycle
{
    /**
     * Liefert den Namen des Sensors.<br>
     * Dieser würde ggf. umgewandelt, siehe {@link #setName(String)}.
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

    /**
     * Setzt den Namen des Sensors.<br>
     * Der Name wird in ein Datei- und Datenbankverträgliches Format umgewandelt.
     *
     * @param name String
     */
    public void setName(final String name);
}
