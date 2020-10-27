// Created: 12.05.2017
package de.freese.jsensors.backend;

import de.freese.jsensors.SensorValue;
import de.freese.jsensors.lifecycle.LifeCycle;

/**
 * Ein {@link Backend} speichert die gemessenen Sensorwerte.
 *
 * @author Thomas Freese
 */
public interface Backend extends LifeCycle
{
    /**
     * Speichert den Sensorwert.
     *
     * @param sensorValue {@link SensorValue}
     */
    public void save(SensorValue sensorValue);
}
