// Created: 08.12.2017
package de.freese.jsensors.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.util.SocketUtils;
import org.springframework.util.StringUtils;

/**
 * Spring-Konfiguration für jSensors mit einem Embedded HSQLDB-Server.
 *
 * @author Thomas Freese
 */
// @Configuration
@Profile("HsqldbEmbeddedServer")
@PropertySources(@PropertySource("classpath:hikari-pool.properties"))
public class HsqldbEmbeddedServerConfig implements ApplicationListener<ApplicationContextEvent>
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HsqldbEmbeddedServerConfig.class);

    static
    {
        if (!StringUtils.hasText(System.getProperty("hsqldbPort")))
        {
            int port = SocketUtils.findAvailableTcpPort();

            // Damit die Placeholder in Properties funktionieren: ${hsqldbPort}
            System.setProperty("hsqldbPort", Integer.toString(port));
        }
    }

    /**
     * Erzeugt eine neue Instanz von {@link HsqldbEmbeddedServerConfig}.
     */
    public HsqldbEmbeddedServerConfig()
    {
        super();
    }

    /**
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(final ApplicationContextEvent event)
    {
        if (event instanceof ContextStartedEvent)
        {
            LOGGER.info("ContextStartedEvent");
        }
        else if (event instanceof ContextClosedEvent)
        {
            LOGGER.info("ContextClosedEvent");
        }
        else
        {
            LOGGER.info(event.getClass().getSimpleName());
        }

        // Spring schickt dieses Event am Anfang des Shutdowns, das wäre zu früh für den HSQLDB Server.
        // Beans werden in umgekehrter Reihenfolge ihrere Erzeugung zerstört.
        // shutdown();

        // if (SpringContext.getApplicationContext().containsBean("hsqldbEmbeddedServerConfig"))
        // {
        // HsqldbEmbeddedServerConfig bean = SpringContext.getBean("hsqldbEmbeddedServerConfig");
        // bean.shutdown();
        // }
    }
}
