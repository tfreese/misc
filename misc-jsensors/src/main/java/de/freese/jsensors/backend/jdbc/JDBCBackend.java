// Created: 02.06.2017
package de.freese.jsensors.backend.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import javax.sql.DataSource;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.batch.AbstractBatchBackend;
import de.freese.jsensors.sensor.Sensor;

/**
 * {@link Backend} für die Ausgabe der Sensorwerte in einer Datenbank.<br>
 * Jeder {@link Sensor} hat seine eigene Tabelle.<br>
 * Format: TIMESTAMP;VALUE
 *
 * @author Thomas Freese
 */
public class JDBCBackend extends AbstractBatchBackend
{
    /**
     *
     */
    private DataSource dataSource;

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
     * @see de.freese.jsensors.backend.batch.AbstractBatchBackend#saveValues(java.util.List)
     */
    @Override
    protected void saveValues(final List<SensorValue> values) throws Exception
    {
        if ((values == null) || values.isEmpty())
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
     * Setzt die {@link DataSource}.
     *
     * @param dataSource {@link DataSource}
     */
    public void setDataSource(final DataSource dataSource)
    {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
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
        super.start();

        Objects.requireNonNull(getDataSource(), "dataSource required");

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
        super.stop();

        this.dataSource = null;
    }
}
