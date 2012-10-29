/**
 * Created: 17.12.2011
 */

package de.freese.sonstiges.jcr;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.jcr.Repository;

import org.modeshape.common.collection.Problem;
import org.modeshape.jcr.JcrConfiguration;
import org.modeshape.jcr.JcrEngine;

/**
 * @author Thomas Freese
 */
public class TestModeShape extends AbstractJcrTest
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		AbstractJcrTest jcrTest = new TestModeShape();

		File configFile = new File("modeshape-config.xml");
		System.out.println(configFile.getAbsolutePath());

		JcrConfiguration configuration = new JcrConfiguration();
		configuration.loadFrom(configFile);

		JcrEngine engine = configuration.build();

		engine.start();

		if (engine.getProblems().hasProblems())
		{
			for (Problem problem : engine.getProblems())
			{
				System.err.println(problem.getMessageString());
			}

			throw new RuntimeException("Could not start due to problems");
		}

		Repository repository = engine.getRepository("test");

		try
		{
			jcrTest.doExample(repository);
		}
		catch (Throwable th)
		{
			jcrTest.getLogger().error(null, th);
		}
		finally
		{
			jcrTest.shutdown();

			engine.shutdown();
			engine.awaitTermination(5, TimeUnit.SECONDS);
		}

		// TODO Für modeshape 3.0.0
		// AbstractJcrTest jcrTest = new TestModeShape();
		//
		// ModeShapeEngine engine = new ModeShapeEngine();
		// engine.start();
		//
		// File configFile = new File("modeshape-config.xml");
		// System.out.println(configFile.getAbsolutePath());
		//
		// RepositoryConfiguration config = RepositoryConfiguration.read(configFile);
		// Problems problems = config.validate();
		//
		// if (problems.hasErrors())
		// {
		// System.err.println("Problems starting the engine.");
		// System.err.println(problems);
		// System.exit(-1);
		// }
		//
		// Repository repository = engine.deploy(config);
		//
		// // Test
		// repository = engine.getRepository("test");
		//
		// try
		// {
		// jcrTest.doExample(repository);
		// }
		// catch (Throwable th)
		// {
		// jcrTest.getLogger().error(null, th);
		// }
		// finally
		// {
		// jcrTest.shutdown();
		//
		// engine.shutdown().get();
		// }
	}

	/**
	 * Erstellt ein neues {@link TestModeShape} Object.
	 */
	public TestModeShape()
	{
		super();
	}
}
