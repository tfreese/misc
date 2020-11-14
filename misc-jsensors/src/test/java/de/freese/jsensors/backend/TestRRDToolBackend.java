// Created: 28.10.2020
package de.freese.jsensors.backend;

import java.nio.file.Paths;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.jsensors.backend.file.RRDToolBackend;
import de.freese.jsensors.sensor.ConstantSensor;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@Disabled("RRDTool ist nicht immer vorhanden")
class TestRRDToolBackend
{
    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testRRDToolBackend() throws Exception
    {
        Sensor sensor = new ConstantSensor("TEST_SENSOR_RRDTOOL", "123.456");

        RRDToolBackend rrdToolBackend = new RRDToolBackend();
        rrdToolBackend.setPath(Paths.get("logs", "sensors.rrd"));
        rrdToolBackend.setExclusive(true);
        rrdToolBackend.setBatchSize(2);
        rrdToolBackend.start();

        sensor.setBackend(rrdToolBackend);

        sensor.scan();
        sensor.scan();
        sensor.scan();

        rrdToolBackend.stop();
    }
}
