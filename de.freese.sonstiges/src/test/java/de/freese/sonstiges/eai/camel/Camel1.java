/**
 * Created: 13.07.2012
 */

package de.freese.sonstiges.eai.camel;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * @author Thomas Freese
 */
public class Camel1
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		DefaultCamelContext camelContext = new DefaultCamelContext();
		camelContext.setTracing(Boolean.FALSE);
		camelContext.addRoutes(new Example2Route());
		camelContext.addRoutes(new Example3Route());
		camelContext.start();

		ProducerTemplate template = camelContext.createProducerTemplate();
		template.sendBody("direct:say_hello", "Tommy");

		Thread.sleep(1000000);
		camelContext.stop();
	}
}
