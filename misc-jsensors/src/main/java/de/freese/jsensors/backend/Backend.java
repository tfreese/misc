// Created: 12.05.2017
package de.freese.jsensors.backend;

import de.freese.jsensors.SensorValue;

/**
 * Ein {@link Backend} speichert die gemessenen Sensorwerte.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface Backend
{
    /**
     * Speichert den Sensorwert.
     *
     * @param sensorValue {@link SensorValue}
     */
    public void save(SensorValue sensorValue);
}
