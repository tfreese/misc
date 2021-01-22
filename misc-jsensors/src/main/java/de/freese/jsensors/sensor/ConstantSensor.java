// Created: 28.10.2020
package de.freese.jsensors.sensor;

import java.util.List;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class ConstantSensor extends AbstractSensor
{
    /**
    *
    */
    private final String name;

    /**
     *
     */
    private final String value;

    /**
     * Erstellt ein neues {@link ConstantSensor} Object.
     *
     * @param name String
     * @param value String
     */
    public ConstantSensor(final String name, final String value)
    {
        super();

        this.name = Objects.requireNonNull(name, "name required");
        this.value = Objects.requireNonNull(value, "value required");
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#getNames()
     */
    @Override
    public List<String> getNames()
    {
        return List.of(this.name);
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#measureImpl()
     */
    @Override
    protected void measureImpl() throws Exception
    {
        store(this.name, this.value);
    }
}
