package de.freese.sonstiges.template.velocity;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * @author Thomas Freese
 */
public class Velocity
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		new Velocity("templates/velocity/example.vtl");
	}

	/**
	 * Erstellt ein neues {@link Velocity} Object.
	 * 
	 * @param templateFile String
	 */
	public Velocity(final String templateFile)
	{
		super();

		try
		{
			/*
			 * setup
			 */
			Properties properties = new Properties();
			properties.put("runtime.log", "velocity_example.log");
			properties.put("resource.loader", "classpath");
			properties.put("classpath.resource.loader.description",
					"Velocity Classpath Resource Loader");
			properties.put("classpath.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			properties.put("classpath.resource.loader.path", ".");
			properties.put("classpath.resource.loader.cache", "false");
			properties.put("classpath.resource.loader.modificationCheckInterval", "0");

			// VelocityEngine ve = new VelocityEngine("velocity.properties");
			VelocityEngine ve = new VelocityEngine(properties);
			ve.init();

			/*
			 * Make a context object and populate with the data. This is where the Velocity engine
			 * gets the data to resolve the references (ex. $list) in the template
			 */
			VelocityContext context = new VelocityContext();
			context.put("names", getNames());
			context.put("Math", Math.class);
			context.put("PI", Double.valueOf(Math.PI));

			/*
			 * get the Template object. This is the parsed version of your template input file. Note
			 * that getTemplate() can throw ResourceNotFoundException : if it doesn't find the
			 * template ParseErrorException : if there is something wrong with the VTL Exception :
			 * if something else goes wrong (this is generally indicative of as serious problem...)
			 */
			Template template = null;

			try
			{
				template = ve.getTemplate(templateFile);
			}
			catch (ResourceNotFoundException rnfe)
			{
				System.err.println("Example : error : cannot find template " + templateFile);
			}
			catch (ParseErrorException pee)
			{
				System.err
						.println("Example : Syntax error in template " + templateFile + ":" + pee);
			}

			/*
			 * Now have the template engine process your template using the data placed into the
			 * context. Think of it as a 'merge' of the template and the data to produce the output
			 * stream.
			 */
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

			if (template != null)
			{
				template.merge(context, writer);
			}

			/*
			 * flush and cleanup
			 */
			writer.flush();
			writer.close();
		}
		catch (Exception ex)
		{
			System.err.println(ex);
		}
	}

	/**
	 * @return {@link List}
	 */
	public List<String> getNames()
	{
		List<String> list = new ArrayList<>();

		list.add("ArrayList element 1");
		list.add("ArrayList element 2");
		list.add("ArrayList element 3");
		list.add("ArrayList element 4");

		return list;
	}
}
