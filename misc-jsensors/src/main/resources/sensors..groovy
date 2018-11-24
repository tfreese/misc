beans {
    springContext(de.freese.jsensors.SpringContext) {}
    consoleLogBackend(de.freese.jsensors.backend.ConsoleLogBackend) {}
    
    sensor1(de.freese.jsensors.sensor.Sensor, "test1") {
        //someProperty = [1, 2, 3]
    }
}