// Created: 10.02.2017
package de.freese.jsensors.spring;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

/**
 * Spring-Konfiguration für jSensors.
 *
 * @author Thomas Freese
 */
@Configuration
@ImportResource("classpath:sensors.xml")
public class ApplicationConfig
{
    // /**
    // * Liefert den {@link ExecutorService} des Backends.
    // *
    // * @param backend String
    // * @return {@link ExecutorService}
    // */
    // public static ExecutorService getExecutorServiceForBackend(final String backend)
    // {
    // ExecutorService es = null;
    // // es = SpringContext.getExecutorService();
    //
    // String beanID = backend + "_executorService";
    //
    // if (!SpringContext.containsBean(beanID))
    // {
    // ThreadPoolExecutorFactoryBean bean = createDefaultExecutorService(backend + "-");
    // bean.setBeanName(beanID);
    //
    // bean.afterPropertiesSet();
    // SpringContext.registerSingleton(beanID, bean);
    // }
    //
    // es = SpringContext.getBean(beanID, ExecutorService.class);
    //
    // return es;
    // }

    /**
     * @param threadNamePrefix String
     * @return {@link ThreadPoolExecutorFactoryBean}
     */
    private static ThreadPoolExecutorFactoryBean createDefaultExecutorService(final String threadNamePrefix)
    {
        int coreSize = Runtime.getRuntime().availableProcessors();
        int maxSize = coreSize * 2;
        int queueSize = maxSize;

        ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(coreSize);
        bean.setMaxPoolSize(maxSize);
        bean.setKeepAliveSeconds(60);
        bean.setQueueCapacity(queueSize);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix(threadNamePrefix);
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        bean.setAllowCoreThreadTimeOut(false);
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    /**
     * Erzeugt eine neue Instanz von {@link ApplicationConfig}
     */
    public ApplicationConfig()
    {
        super();
    }

    /**
     * @return {@link ThreadPoolExecutorFactoryBean}
     */
    @Bean
    @Primary
    public ThreadPoolExecutorFactoryBean executorService()
    {
        ThreadPoolExecutorFactoryBean bean = createDefaultExecutorService("thread-");

        return bean;
    }

    /**
     * @return {@link ThreadPoolExecutorFactoryBean}
     */
    @Bean
    public ThreadPoolExecutorFactoryBean executorServiceCsv()
    {
        ThreadPoolExecutorFactoryBean bean = createDefaultExecutorService("thread-csv-");
        bean.setCorePoolSize(1);
        bean.setMaxPoolSize(2);
        bean.setKeepAliveSeconds(600);
        bean.setQueueCapacity(100);

        return bean;
    }

    /**
     * @return {@link ThreadPoolExecutorFactoryBean}
     */
    @Bean
    public ThreadPoolExecutorFactoryBean executorServiceJdbc()
    {
        ThreadPoolExecutorFactoryBean bean = createDefaultExecutorService("thread-jdbc-");

        return bean;
    }

    /**
     * @return {@link ThreadPoolExecutorFactoryBean}
     */
    @Bean
    public ThreadPoolExecutorFactoryBean executorServiceRrd()
    {
        ThreadPoolExecutorFactoryBean bean = createDefaultExecutorService("thread-rrd-");
        bean.setCorePoolSize(1);
        bean.setMaxPoolSize(2);
        bean.setKeepAliveSeconds(600);
        bean.setQueueCapacity(100);

        return bean;
    }

    /**
     * @return {@link ScheduledExecutorFactoryBean}
     */
    @Bean
    // @Primary
    public ScheduledExecutorFactoryBean scheduledExecutorService()
    {
        int poolSize = Runtime.getRuntime().availableProcessors();

        ScheduledExecutorFactoryBean bean = new ScheduledExecutorFactoryBean();
        bean.setPoolSize(poolSize);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix("scheduler-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    /**
     * Wird für {@link EnableAsync} benötigt.
     *
     * @param executorService {@link ExecutorService}
     * @return {@link AsyncTaskExecutor}
     */
    @Bean(
    {
            "taskExecutor", "asyncTaskExecutor"
    })
    @ConditionalOnMissingBean(AsyncTaskExecutor.class)
    public AsyncTaskExecutor springTaskExecutor(@Qualifier("executorService") final ExecutorService executorService)
    {
        AsyncTaskExecutor bean = new ConcurrentTaskExecutor(executorService);

        return bean;
    }

    /**
     * Wird für {@link EnableScheduling} benötigt.
     *
     * @param executorService {@link ExecutorService}
     * @param scheduledExecutorService {@link ScheduledExecutorService}
     * @return {@link TaskScheduler}
     */
    @Bean("taskScheduler")
    @ConditionalOnMissingBean(TaskScheduler.class)
    public TaskScheduler springTaskScheduler(@Qualifier("executorService") final ExecutorService executorService,
                                             final ScheduledExecutorService scheduledExecutorService)
    {
        TaskScheduler bean = new ConcurrentTaskScheduler(executorService, scheduledExecutorService);

        return bean;
    }
}
