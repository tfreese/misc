// Created: 02.06.2017
package de.freese.jsensors.backend.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.sql.DataSource;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.LifeCycle;

/**
 * {@link Backend} für die Ausgabe der Sensorwerte in einer Datenbank.<br>
 * Jeder {@link Sensor} hat seine eigene Tabelle.<br>
 * Format: TIMESTAMP;VALUE
 *
 * @author Thomas Freese
 */
public class JDBCBackend extends AbstractBackend implements LifeCycle
{
    /**
    *
    */
    private int batchSize = 5;

    /**
    *
    */
    private List<SensorValue> buffer;

    /**
     *
     */
    private DataSource dataSource;

    /**
    *
    */
    private boolean exclusive;

    /**
    *
    */
    private String tableName;

    /**
     * Erzeugt die Tabelle, falls diese noch nicht existiert.
     *
     * @param tableName String
     * @throws SQLException Falls was schief geht.
     */
    protected void createTableIfNotExist(final String tableName) throws SQLException
    {
        try (Connection con = getDataSource().getConnection())
        {
            DatabaseMetaData dbmd = con.getMetaData();
            boolean exist = false;

            try (ResultSet tables = dbmd.getTables(null, null, tableName, new String[]
            {
                    "TABLE"
            }))
            {
                if (tables.next())
                {
                    // Table exist.
                    exist = true;
                }
            }

            if (!exist)
            {
                getLogger().info("Create table: {}", tableName);

                try (Statement stmt = con.createStatement())
                {
                    // Create Table.
                    StringBuilder sql = new StringBuilder();
                    sql.append("CREATE TABLE ").append(tableName);

                    StringJoiner joiner = new StringJoiner(", ", " (", ")");

                    if (!isExclusive())
                    {
                        joiner.add("NAME VARCHAR(20) NOT NULL");
                        joiner.add("VALUE VARCHAR(20) NOT NULL");
                        joiner.add("TIMESTAMP BIGINT NOT NULL");
                    }
                    else
                    {
                        joiner.add("VALUE VARCHAR(20) NOT NULL");
                        joiner.add("TIMESTAMP BIGINT PRIMARY KEY NOT NULL");
                    }

                    sql.append(joiner);

                    stmt.execute(sql.toString());

                    if (!isExclusive())
                    {
                        // Index
                        String index = String.format("CREATE UNIQUE INDEX %s_UNQ ON %s (NAME, TIMESTAMP);", tableName, tableName);

                        stmt.execute(index);
                    }
                }
            }
        }
    }

    /**
     * @return {@link List}
     */
    private synchronized List<SensorValue> flush()
    {
        List<SensorValue> list = this.buffer;
        this.buffer = null;

        return list;
    }

    /**
     * @return int
     */
    private int getBatchSize()
    {
        return this.batchSize;
    }

    /**
     * Liefert die {@link DataSource}.
     *
     * @return {@link DataSource}
     */
    private DataSource getDataSource()
    {
        return this.dataSource;
    }

    /**
     * @return String
     */
    private String getTableName()
    {
        return this.tableName;
    }

    /**
     * Die Datei ist exklusiv nur für einen Sensor.
     *
     * @return boolean
     */
    protected boolean isExclusive()
    {
        return this.exclusive;
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#saveValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void saveValue(final SensorValue sensorValue) throws Exception
    {
        if (sensorValue == null)
        {
            return;
        }

        if (this.buffer == null)
        {
            this.buffer = new ArrayList<>();
        }

        this.buffer.add(sensorValue);

        if (this.buffer.size() >= getBatchSize())
        {
            saveValues(flush());
        }
    }

    /**
     * @param values {@link List}
     * @throws Exception Falls was schief geht.
     */
    private void saveValues(final List<SensorValue> values) throws Exception
    {
        if (values == null)
        {
            return;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(getTableName());

        if (isExclusive())
        {
            sql.append(" (VALUE, TIMESTAMP)");
            sql.append(" VALUES (?, ?)");
        }
        else
        {
            sql.append(" (NAME, VALUE, TIMESTAMP)");
            sql.append(" VALUES (?, ?, ?)");
        }

        try (Connection con = getDataSource().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql.toString()))
        {
            con.setAutoCommit(false);

            for (SensorValue sensorValue : values)
            {
                if (isExclusive())
                {
                    pstmt.setString(1, sensorValue.getValue());
                    pstmt.setLong(2, sensorValue.getTimestamp());
                }
                else
                {
                    pstmt.setString(1, sensorValue.getName());
                    pstmt.setString(2, sensorValue.getValue());
                    pstmt.setLong(3, sensorValue.getTimestamp());
                }

                pstmt.addBatch();
                // pstmt.clearParameters();
            }

            pstmt.executeBatch();

            con.commit();
        }
    }

    /**
     * @param batchSize int
     */
    public void setBatchSize(final int batchSize)
    {
        this.batchSize = batchSize;
    }

    /**
     * Setzt die {@link DataSource}.
     *
     * @param dataSource {@link DataSource}
     */
    public void setDataSource(final DataSource dataSource)
    {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
    }

    /**
     * Die Tabelle ist exklusiv nur für einen Sensor.
     *
     * @param exclusive boolean
     */
    public void setExclusive(final boolean exclusive)
    {
        this.exclusive = exclusive;
    }

    /**
     * @param tableName String
     */
    public void setTableName(final String tableName)
    {
        this.tableName = Objects.requireNonNull(tableName, "tableName required");
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        Objects.requireNonNull(getDataSource(), "dataSource required");

        if (getBatchSize() < 1)
        {
            throw new IllegalArgumentException("batchSize must be >= 1");
        }

        if ((getTableName() == null) || getTableName().isBlank())
        {
            throw new IllegalArgumentException("tableName must not null or blank");
        }

        try
        {
            createTableIfNotExist(getTableName());
        }
        catch (SQLException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        try
        {
            saveValues(flush());
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        this.dataSource = null;
    }
}
