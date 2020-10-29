// Created: 02.06.2017
package de.freese.jsensors.backend.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.sql.DataSource;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.LifeCycle;
import de.freese.jsensors.utils.Utils;

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
    private DataSource dataSource;

    /**
    *
    */
    private final Set<String> existingTables = Collections.synchronizedSet(new TreeSet<>());

    /**
     * Erzeugt die Tabelle, falls diese noch nicht existiert.
     *
     * @param tableName String
     * @throws SQLException Falls was schief geht.
     */
    private void createTableIfNotExist(final String tableName) throws SQLException
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
                    sql.append("(");
                    sql.append("TIMESTAMP BIGINT PRIMARY KEY NOT NULL");
                    sql.append(", VALUE VARCHAR(20) NOT NULL");
                    sql.append(")");

                    stmt.execute(sql.toString());
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
     * @see de.freese.jsensors.backend.AbstractBackend#saveValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void saveValue(final SensorValue sensorValue) throws Exception
    {
        String tableName = Utils.sensorNameToTableName(sensorValue.getName());

        if (!this.existingTables.contains(tableName))
        {
            synchronized (this.existingTables)
            {
                // DoubleCheckLock
                if (!this.existingTables.contains(tableName))
                {
                    createTableIfNotExist(tableName);

                    this.existingTables.add(tableName);
                }
            }
        }

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName);
        sql.append(" (TIMESTAMP, VALUE)");
        sql.append(" VALUES (?, ?)");

        try (Connection con = getDataSource().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql.toString()))
        {
            con.setAutoCommit(false);

            pstmt.setLong(1, sensorValue.getTimestamp());
            pstmt.setString(2, sensorValue.getValue());
            pstmt.executeUpdate();

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
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        Objects.requireNonNull(getDataSource(), "dataSource required");
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        // getDataSource().close()
        this.dataSource = null;
    }
}
