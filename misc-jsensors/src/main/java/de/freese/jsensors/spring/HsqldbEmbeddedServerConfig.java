// Created: 08.12.2017
package de.freese.jsensors.spring;

import javax.annotation.Resource;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.SocketUtils;
import org.springframework.util.StringUtils;

/**
 * Spring-Konfiguration für jSensors mit einem Embedded HSQLDB-Server.
 *
 * @author Thomas Freese
 */
@Configuration
@Profile("HsqldbEmbeddedServer")
@PropertySources(@PropertySource("classpath:hikari-pool.properties"))
public class HsqldbEmbeddedServerConfig implements ApplicationListener<ContextClosedEvent> // , DisposableBean
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
     *
     */
    @Resource
    private DataSource dataSource = null;

    /**
     * Erzeugt eine neue Instanz von {@link HsqldbEmbeddedServerConfig}.
     */
    public HsqldbEmbeddedServerConfig()
    {
        super();
    }

    // /**
    // * @param dataSource {@link DataSource}
    // * @throws Exception Falls was schief geht.
    // */
    // protected void close(final DataSource dataSource) throws Exception
    // {
    // if (dataSource instanceof DisposableBean)
    // {
    // ((DisposableBean) dataSource).destroy();
    // }
    // else if (dataSource instanceof HikariDataSource)
    // {
    // ((HikariDataSource) dataSource).close();
    // }
    // // else if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource)
    // // {
    // // ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).close(true);
    // // }
    // }

    /**
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(final ContextClosedEvent event)
    {
        LOGGER.info("ContextClosedEvent");

        // Spring schickt dieses Event am Anfang des Shutdowns, das wäre zu früh für den HSQLDB Server.
        // Beans werden in umgekehrter Reihenfolge ihrere Erzeugung zerstört.
        // shutdown();

        // if (SpringContext.getApplicationContext().containsBean("hsqldbEmbeddedServerConfig"))
        // {
        // HsqldbEmbeddedServerConfig bean = SpringContext.getBean("hsqldbEmbeddedServerConfig");
        // bean.shutdown();
        // }
    }

    /**
     * Wird in HsqldbServerAutoConfiguration gemacht. Server.shutdown überschrieben.
     */
    // @PreDestroy
    // public void shutdown()
    // {
    // LOGGER.info("shutdown compact HSQLDB-Server");
    //
    // if (this.dataSource == null)
    // {
    // return;
    // }
    //
    // try
    // {
    // // Wenn der Server runtergefahren wird, kann die Connection auch nicht mehr geschlossen werden !
    // // try (
    // Connection con = this.dataSource.getConnection();
    // Statement stmt = con.createStatement();
    // // )
    // {
    // stmt.execute("SHUTDOWN COMPACT");
    //
    // LOGGER.info("...finished shutdown compact");
    // }
    // }
    // catch (Exception ex)
    // {
    // LOGGER.warn(ex.getMessage());
    // }
    //
    // try
    // {
    // close(this.dataSource);
    // }
    // catch (Exception ex)
    // {
    // LOGGER.warn(ex.getMessage());
    // }
    // }
}
