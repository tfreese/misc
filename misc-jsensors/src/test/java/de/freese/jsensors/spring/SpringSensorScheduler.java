// Created: 12.05.2017
package de.freese.jsensors.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;

import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
// @Component
public class SpringSensorScheduler implements ApplicationContextAware
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringSensorScheduler.class);

    /**
    *
    */
    private ApplicationContext applicationContext = null;

    /**
     *
     */
    private final List<Sensor> sensors = new ArrayList<>();

    /**
     * Erzeugt eine neue Instanz von {@link SpringSensorScheduler}.
     */
    public SpringSensorScheduler()
    {
        super();
    }

    /**
     *
     */
    @PostConstruct
    public void initSensors()
    {
        Map<String, Sensor> beans = this.applicationContext.getBeansOfType(Sensor.class);

        this.sensors.addAll(beans.values());
    }

    /**
     * Messzyklus.
     */
    @Scheduled(initialDelay = 2 * 1000, fixedDelay = 5 * 1000) // Alle 5 Sekunden
    // initialDelayString = #{ T(java.lang.Math).random() * 10 }
    // @Scheduled(cron = "0 0 * * * MON-FRI") // Jede Stunde
    // @Scheduled(cron = "4,9,14,19,24,29,34,39,44,49,55,59 * * * *")
    // @Scheduled(cron = "0 */15 * * * MON-FRI") // Alle 15 Minuten
    // @Scheduled(cron = "*/30 * * * * MON-FRI") // Alle 30 Sekunden
    // @Async // ("executorService")
    public void measure()
    {
        LOGGER.info("SensorScheduler.scan()");

        this.sensors.forEach(Sensor::scan);
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}
