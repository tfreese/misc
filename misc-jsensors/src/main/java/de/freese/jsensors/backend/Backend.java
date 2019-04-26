// Created: 12.05.2017
package de.freese.jsensors.backend;

/**
 * Ein {@link Backend} speichert die gemessenen Sensorwerte.
 *
 * @author Thomas Freese
 */
public interface Backend
{
    /**
     * Speichert den Sensorwert.
     *
     * @param sensorValue {@link SensorValue}
     */
    public void save(SensorValue sensorValue);
}
