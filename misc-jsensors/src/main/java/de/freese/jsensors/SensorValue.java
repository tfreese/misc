// Created: 07.12.2017
package de.freese.jsensors;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author Thomas Freese
 */
public class SensorValue
{
    /**
    *
    */
    private Date date;

    /**
    *
    */
    private LocalDateTime localDateTime;

    /**
     *
     */
    private final String name;

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
     * @param name String
     * @param value String
     * @param timestamp long
     */
    public SensorValue(final String name, final String value, final long timestamp)
    {
        super();

        this.name = Objects.requireNonNull(name, "name required");
        this.value = Objects.requireNonNull(value, "value required");
        this.timestamp = timestamp;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof SensorValue))
        {
            return false;
        }

        SensorValue other = (SensorValue) obj;

        return Objects.equals(this.name, other.name) && (this.timestamp == other.timestamp) && Objects.equals(this.value, other.value);
    }

    /**
     * @return {@link Date}
     */
    public Date getDate()
    {
        if (this.date == null)
        {
            this.date = new Date(getTimestamp());
        }

        return this.date;
    }

    /**
     * @return {@link LocalDateTime}
     */
    public LocalDateTime getLocalDateTime()
    {
        if (this.localDateTime == null)
        {
            this.localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(this.timestamp), TimeZone.getDefault().toZoneId());
        }

        return this.localDateTime;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return long
     */
    public long getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * @return String
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.name, this.timestamp, this.value);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SensorValue [");
        builder.append("name=").append(getName());
        builder.append(", value=").append(getValue());
        builder.append(", timestamp=").append(getTimestamp());
        builder.append(", date=").append(getDate());
        builder.append("]");

        return builder.toString();
    }
}
