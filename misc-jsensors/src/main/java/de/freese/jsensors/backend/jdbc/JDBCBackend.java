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
import de.freese.jsensors.backend.AbstractBatchBackend;
import de.freese.jsensors.backend.Backend;
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
    private final DataSource dataSource;

    /**
     *
     */
    private final String tableName;

    /**
     * Erstellt ein neues {@link JDBCBackend} Object.
     *
     * @param dataSource {@link DataSource}
     * @param tableName String
     */
    public JDBCBackend(final DataSource dataSource, final String tableName)
    {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
        this.tableName = Objects.requireNonNull(tableName, "tableName required");
    }

    /**
     * Erzeugt die Tabelle, falls diese noch nicht existiert.
     *
     * @param tableName String
     * @throws SQLException Falls was schief geht.
     */
    protected void createTableIfNotExist(final String tableName) throws SQLException
    {
        try (Connection con = this.dataSource.getConnection())
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
                    joiner.add("NAME VARCHAR(20) NOT NULL");
                    joiner.add("VALUE VARCHAR(20) NOT NULL");
                    joiner.add("TIMESTAMP BIGINT NOT NULL");

                    sql.append(joiner);

                    stmt.execute(sql.toString());

                    // Index
                    String index = String.format("CREATE UNIQUE INDEX %s_UNQ ON %s (NAME, TIMESTAMP);", tableName, tableName);
                    stmt.execute(index);

                    // Diese Indices, existieren durch de UNIQUE INDEX.
                    // index = String.format("CREATE INDEX NAME_IDX ON %s (NAME);", tableName);
                    // stmt.execute(index);
                    //
                    // index = String.format("CREATE INDEX TIMESTAMP_IDX ON %s (TIMESTAMP);", tableName);
                    // stmt.execute(index);
                }
            }
        }
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        super.start();

        try
        {
            createTableIfNotExist(this.tableName);
        }
        catch (SQLException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBatchBackend#storeValues(java.util.List)
     */
    @Override
    protected void storeValues(final List<SensorValue> values) throws Exception
    {
        if ((values == null) || values.isEmpty())
        {
            return;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(this.tableName);
        sql.append(" (NAME, VALUE, TIMESTAMP)");
        sql.append(" VALUES (?, ?, ?)");

        try (Connection con = this.dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql.toString()))
        {
            con.setAutoCommit(false);

            for (SensorValue sensorValue : values)
            {
                pstmt.setString(1, sensorValue.getName());
                pstmt.setString(2, sensorValue.getValue());
                pstmt.setLong(3, sensorValue.getTimestamp());

                pstmt.addBatch();
                // pstmt.clearParameters();
            }

            pstmt.executeBatch();

            con.commit();
        }
    }
}
