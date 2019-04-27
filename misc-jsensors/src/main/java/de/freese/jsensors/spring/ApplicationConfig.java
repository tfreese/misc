// Created: 10.02.2017
package de.freese.jsensors.spring;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

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
    @ConditionalOnMissingBean(ExecutorService.class)
    public ThreadPoolExecutorFactoryBean executorService()
    {
        LOGGER.info("no ExecutorService exist, create a ExecutorService");

        int coreSize = Runtime.getRuntime().availableProcessors();
        int maxSize = coreSize * 2;
        int queueSize = maxSize;

        ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
        bean.setCorePoolSize(coreSize);
        bean.setMaxPoolSize(maxSize);
        bean.setKeepAliveSeconds(60);
        bean.setQueueCapacity(queueSize);
        bean.setThreadPriority(Thread.NORM_PRIORITY);
        bean.setThreadNamePrefix("task-");
        bean.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        bean.setAllowCoreThreadTimeOut(true);
        bean.setExposeUnconfigurableExecutor(true);

        return bean;
    }

    /**
     * @return {@link ScheduledExecutorFactoryBean}
     */
    @Bean
    @ConditionalOnMissingBean(ScheduledExecutorService.class)
    public ScheduledExecutorFactoryBean scheduledExecutorService()
    {
        LOGGER.info("no ScheduledExecutorService exist, create a ScheduledExecutorService");

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
    @Bean("taskExecutor")
    @ConditionalOnMissingBean(AsyncTaskExecutor.class)
    public AsyncTaskExecutor springTaskExecutor(@Qualifier("executorService") final ExecutorService executorService)
    {
        LOGGER.info("no TaskExecutor exist, create a ConcurrentTaskExecutor");

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
        LOGGER.info("no TaskScheduler exist, create a ConcurrentTaskScheduler");

        TaskScheduler bean = new ConcurrentTaskScheduler(executorService, scheduledExecutorService);

        return bean;
    }
}
