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
     * @param value String Wert der Messung
     * @param timestamp long Timestamp der Messung
     * @param sensor String Name des Sensors
     */
    public void save(String value, long timestamp, String sensor);
}
