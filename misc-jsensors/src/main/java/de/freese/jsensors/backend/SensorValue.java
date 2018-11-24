// Created: 07.12.2017
package de.freese.jsensors.backend;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * @author Thomas Freese
 */
class SensorValue
{
    /**
    *
    */
    private final LocalDateTime localDateTime;

    /**
     *
     */
    private final String sensor;

    /**
     *
     */
    private final long timestamp;

    /**
     *
     */
    private final String value;

    /**
     * Erzeugt eine neue Instanz von {@link SensorValue}.
     *
     * @param value String
     * @param timestamp long
     * @param sensor String
     */
    public SensorValue(final String value, final long timestamp, final String sensor)
    {
        super();

        this.value = value;
        this.sensor = sensor;
        this.timestamp = timestamp <= 0 ? System.currentTimeMillis() : timestamp;

        this.localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(this.timestamp), TimeZone.getDefault().toZoneId());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SensorValue [");
        builder.append("sensor=").append(this.sensor);
        builder.append(", value=").append(this.value);
        builder.append(", timestamp=").append(this.localDateTime);
        builder.append("]");

        return builder.toString();
    }

    /**
     * @return {@link LocalDateTime}
     */
    protected LocalDateTime getLocalDateTime()
    {
        return this.localDateTime;
    }

    /**
     * @return String
     */
    protected String getSensor()
    {
        return this.sensor;
    }

    /**
     * @return long
     */
    protected long getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * @return String
     */
    protected String getValue()
    {
        return this.value;
    }
}