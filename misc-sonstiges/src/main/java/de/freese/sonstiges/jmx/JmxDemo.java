// Erzeugt: 13.07.2016
package de.freese.sonstiges.jmx;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.springframework.jmx.export.MBeanExporter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

/**
 * @author Thomas Freese
 */
public class JmxDemo
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

        MBeanServer server = null;

        // Siehe JmxUtils, MBeanExporter von Spring
        // agendID null means any registered server, but "" specifically means the platform server
        // String agentID = null;
        // if (!"".equals(agentID))
        // {
        // List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(agentID);
        //
        // if (CollectionUtils.isNotEmpty(servers))
        // {
        // server = servers.get(0);
        // }
        // }
        //
        // if ((server == null) && !StringUtils.isNotBlank(agentID))
        // {
        // server = ManagementFactory.getPlatformMBeanServer();
        // // MBeanServerFactory.createMBeanServer(defaultDomain);
        // // MBeanServerFactory.newMBeanServer(defaultDomain);
        // }
        ObjectName objectName = new ObjectName("bean:name=dateBean");
        IDateBean bean = new DateBean();

        // try
        // {
        // server.registerMBean(new StandardMBean(bean, IDateBean.class, false), objectName);
        // }
        // catch (InstanceAlreadyExistsException iaeex)
        // {
        // server.unregisterMBean(objectName);
        // server.registerMBean(new StandardMBean(bean, IDateBean.class, false), objectName);
        // }
        Map<String, Object> beans = new HashMap<>();
        beans.put(objectName.getCanonicalName(), bean);

        //
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
        config.setJdbcUrl("jdbc:hsqldb:mem:jmx");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        config.setScheduledExecutor(scheduledExecutorService);
        // config.setRegisterMbeans(true);
        // config.setPoolName(poolName);

        @SuppressWarnings("resource")
        HikariDataSource dataSource = new HikariDataSource(config);
        HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();

        // server.registerMBean(poolMXBean, ObjectName.getInstance("bean:name=dataSource"));
        beans.put("bean:name=dataSource", poolMXBean);

        MBeanExporter mBeanExporter = new MBeanExporter();
        // mBeanExporter.setServer(server);
        mBeanExporter.setBeans(beans);

        mBeanExporter.afterPropertiesSet();
        mBeanExporter.afterSingletonsInstantiated();

        server = mBeanExporter.getServer();

        System.out.println(server.invoke(new ObjectName("bean:name=dateBean"), "getDate", null, null));
        System.out.println(server.getAttribute(new ObjectName("bean:name=dateBean"), "Date"));

        final MBeanServer mbs = server;

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try
            {
                try (Connection con = dataSource.getConnection();
                     Statement stmt = con.createStatement();
                     ResultSet rs = stmt.executeQuery("VALUES (NOW())"))
                {
                    // SYSDATE; rs.getDate(1)
                    rs.next();
                    System.out.println(rs.getTime(1));
                }
            }
            catch (Exception ex)
            {
                System.err.println(ex);
            }
        }, 1L, 3L, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try
            {
                ObjectName poolName = ObjectName.getInstance("bean:name=dataSource");

                System.out.printf("ActiveConnections = %d%n", mbs.getAttribute(poolName, "ActiveConnections"));
                System.out.printf("IdleConnections = %d%n", mbs.getAttribute(poolName, "IdleConnections"));
            }
            catch (Exception ex)
            {
                dataSource.close();

                System.err.println(ex);
            }
        }, 1L, 3L, TimeUnit.SECONDS);
    }
}
