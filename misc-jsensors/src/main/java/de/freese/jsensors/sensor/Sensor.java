// Created: 12.05.2017
package de.freese.jsensors.sensor;

import java.util.List;
import de.freese.jsensors.LifeCycle;
import de.freese.jsensors.backend.Backend;

/**
 * {@link Sensor} der einen zu messenden Wert liefert.<br>
 * Wurde kein Name gesetzt, wird die BeanID des Spring-ApplicationContextes verwendet.
 *
 * @author Thomas Freese
 */
public interface Sensor extends LifeCycle
{
    /**
     * Messen des Wertes.
     */
    public void scan();

    /**
     * Setzt die Liste der {@link Backend}s für das Speichern des Sensorwertes.
     *
     * @param backends {@link List}
     */
    public void setBackends(List<Backend> backends);

    /**
     * Setzt den Namen des Sensors.
     *
     * @param name String; optional; Default = BeanID
     */
    public void setName(final String name);
}
