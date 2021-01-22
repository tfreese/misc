// Created: 27.10.2020
package de.freese.jsensors.backend;

import java.util.Objects;
import java.util.function.Consumer;
import de.freese.jsensors.SensorValue;

/**
 * @author Thomas Freese
 */
public class ConsumerBackend extends AbstractBackend
{
    /**
     *
     */
    private final Consumer<SensorValue> consumer;

    /**
     * Erstellt ein neues {@link ConsumerBackend} Object.
     *
     * @param consumer {@link Consumer}
     */
    public ConsumerBackend(final Consumer<SensorValue> consumer)
    {
        super();

        this.consumer = Objects.requireNonNull(consumer, "consumer required");
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#storeValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void storeValue(final SensorValue sensorValue) throws Exception
    {
        this.consumer.accept(sensorValue);
    }
}
