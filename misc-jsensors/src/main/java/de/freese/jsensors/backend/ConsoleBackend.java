// Created: 27.10.2020
package de.freese.jsensors.backend;

import de.freese.jsensors.SensorValue;

/**
 * @author Thomas Freese
 */
public class ConsoleBackend extends AbstractBackend
{
    /**
     * @see de.freese.jsensors.backend.AbstractBackend#storeValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void storeValue(final SensorValue sensorValue) throws Exception
    {
        System.out.println(sensorValue);
    }
}
