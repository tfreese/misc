// Created: 29.10.2020
package de.freese.jsensors.backend.disruptor;

import de.freese.jsensors.SensorValue;

/**
 * @author Thomas Freese
 */
public class SensorEvent
{
    /**
     *
     */
    private SensorValue sensorValue;

    /**
     * Erstellt ein neues {@link SensorEvent} Object.
     */
    public SensorEvent()
    {
        super();
    }

    /**
     * @return {@link SensorValue}
     */
    public SensorValue getSensorValue()
    {
        return this.sensorValue;
    }

    /**
     * @param sensorValue {@link SensorValue}
     */
    public void setSensorValue(final SensorValue sensorValue)
    {
        this.sensorValue = sensorValue;
    }
}
