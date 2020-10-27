package de.freese.jsensors;

import javax.swing.SwingUtilities;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

/**
 * @author Thomas Freese
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@ComponentScan(basePackages = "de.freese.jsensors")
public class SensorSpringApplication
{
    /**
     * @param args String[]
     */
    @SuppressWarnings("resource")
    public static void main(final String[] args)
    {
        if (!StringUtils.hasText(System.getProperty("spring.profiles.active")))
        {
            System.setProperty("spring.profiles.active", "HsqldbEmbeddedServer");
            // System.setProperty("spring.profiles.active", "mariadb");
        }

        // "classpath:sensors.groovy",
        // "classpath:sensors.xml"
        // "file:${user.home}/sensors.xml"
        // SpringApplication.run(new Object[]
        // {
        // Application.class, "classpath:sensors.xml"
        // }, args);

        // ApplicationListener<ContextClosedEvent> listener = System.out::println;

        SwingUtilities.invokeLater(() -> {
            // @formatter:off
            new SpringApplicationBuilder(SensorSpringApplication.class) // , "classpath:sensors.xml"
                    .headless(true) // Default true
                    .web(WebApplicationType.NONE) // Wird eigentlich automatisch ermittelt.
                    .registerShutdownHook(true) // Default true
                    //.banner(new MyBanner())
                    //.listeners(new ApplicationPidFileWriter("jsensors.pid"))
//                    .listeners(listener)
                    .run(args);
            //.build();
            // @formatter:on
        });
        // application.run(args);

        // try (ConfigurableApplicationContext ctx = application.run(args))
        // {
        // ctx.registerShutdownHook();
        // }
        //
        // -Drun_in_ide=true
        // In der Runtime als Default VM-Argument setzen oder in der eclipse.ini
        if (Boolean.parseBoolean(System.getenv("run_in_ide")) || Boolean.parseBoolean(System.getProperty("run_in_ide", "false")))
        {
            System.out.println();
            System.out.println("******************************************************************************************************************");
            System.out.println("You're using an IDE, click in this console and press ENTER to call System.exit() and trigger the shutdown routine.");
            System.out.println("******************************************************************************************************************");

            try
            {
                System.in.read();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            System.exit(0);
        }
    }
}
