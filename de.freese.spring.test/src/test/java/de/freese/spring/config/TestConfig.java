/**
 * Created: 11.12.2011
 */

package de.freese.spring.config;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.freese.spring.config.service.IUserService;

/**
 * @author Thomas Freese
 */
public class TestConfig
{
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Erstellt ein neues {@link TestConfig} Object.
	 */
	public TestConfig()
	{
		super();
	}

	/**
	 * @param context {@link AnnotationConfigApplicationContext}
	 */
	private void testEnv(final AnnotationConfigApplicationContext context)
	{
		context.register(SpringConfigApplication.class);
		context.refresh();
		context.registerShutdownHook();

		IUserService userService = context.getBean(IUserService.class);
		String user = userService.getUser();

		this.logger.info(user);
	}

	/**
	 * 
	 */
	@Test
	public void testProdEnv()
	{
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.getEnvironment().setActiveProfiles("prod");

		testEnv(ctx);
	}

	/**
	 * 
	 */
	@Test
	public void testTestEnv()
	{
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.getEnvironment().setActiveProfiles("test");

		testEnv(ctx);
	}
}
