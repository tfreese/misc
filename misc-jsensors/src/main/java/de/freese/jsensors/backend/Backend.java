// Created: 12.05.2017
package de.freese.jsensors.backend;

import de.freese.jsensors.SensorValue;

/**
 * Ein {@link Backend} nimmt die gemessenen Sensorwerte entgegen und speichert oder verabeitet diese weiter.
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
    public void store(SensorValue sensorValue);
}
