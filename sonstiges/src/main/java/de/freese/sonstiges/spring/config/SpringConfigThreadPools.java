/**
 * Created: 12.12.2011
 */

package de.freese.sonstiges.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Konfiguration für Threadpools.
 * 
 * @author Thomas Freese
 */
@Configuration
public class SpringConfigThreadPools
{
	/**
	 * Erstellt ein neues {@link SpringConfigThreadPools} Object.
	 */
	public SpringConfigThreadPools()
	{
		super();
	}

	/**
	 * Konfiguration entspricht Executors.newCachedThreadPool.<br>
	 * Threads werden erzeugt so viele wie benötigt werden und leben max. 60 Sekunden,<br>
	 * wenn es nix zu tun gibt.<br>
	 * Min. 1 Thread, max. 10.
	 * 
	 * @return {@link ThreadPoolTaskExecutor}
	 */
	@Bean
	public ThreadPoolTaskExecutor parallelExecutor()
	{
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(10);
		executor.setKeepAliveSeconds(60);
		executor.setQueueCapacity(0);
		executor.setThreadPriority(1);

		return executor;
	}

	/**
	 * ScheduledExecutor für max. 10 Tasks.
	 * 
	 * @return {@link ScheduledExecutorFactoryBean}
	 */
	@Bean
	public ScheduledExecutorFactoryBean scheduledExecutor()
	{
		ScheduledExecutorFactoryBean executor = new ScheduledExecutorFactoryBean();
		executor.setPoolSize(10);
		executor.setThreadPriority(1);

		return executor;
	}

	/**
	 * ThreadPool mit nur einem einzigen Thread, max. 10 Tasks können in der Queue sein.
	 * 
	 * @return {@link ThreadPoolTaskExecutor}
	 */
	@Bean
	public ThreadPoolTaskExecutor serialExecutor()
	{
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(1);
		executor.setKeepAliveSeconds(0);
		executor.setQueueCapacity(10);
		executor.setThreadPriority(1);

		return executor;
	}
}
