// Created: 28.10.2020
package de.freese.jsensors.sensor;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class ConstantSensor extends AbstractSensor
{
    /**
     *
     */
    private final String value;

    /**
     * Erstellt ein neues {@link ConstantSensor} Object.
     *
     * @param value String
     */
    public ConstantSensor(final String value)
    {
        super();

        this.value = Objects.requireNonNull(value, "value required");
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#scanValue()
     */
    @Override
    protected void scanValue() throws Exception
    {
        save(this.value);
    }
}
