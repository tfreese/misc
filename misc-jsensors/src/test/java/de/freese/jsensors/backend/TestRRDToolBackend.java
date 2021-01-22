// Created: 28.10.2020
package de.freese.jsensors.backend;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Paths;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.jsensors.SensorRegistry;
import de.freese.jsensors.backend.file.RRDToolBackend;
import de.freese.jsensors.sensor.ConstantSensor;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
// @Disabled("RRDTool ist nicht immer vorhanden")
class TestRRDToolBackend
{
    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testRRDToolBackend() throws Exception
    {
        SensorRegistry registry = new SensorRegistry();

        Sensor sensor = new ConstantSensor("test.sensor.rrdtool", "123.456");
        sensor.bindTo(registry);

        RRDToolBackend rrdToolBackend = new RRDToolBackend();
        rrdToolBackend.setPath(Paths.get("logs", "sensors.rrd"));
        rrdToolBackend.setBatchSize(2);

        registry.bind("test.sensor.rrdtool", rrdToolBackend);
        registry.start();

        sensor.measure();
        sensor.measure();
        sensor.measure();

        registry.stop();

        assertTrue(true);
    }
}
