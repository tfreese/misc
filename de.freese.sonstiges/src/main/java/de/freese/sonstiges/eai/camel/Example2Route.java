/**
 * Created: 13.07.2012
 */

package de.freese.sonstiges.eai.camel;

import org.apache.camel.builder.RouteBuilder;

/**
 * JavaMagazin 11.2011
 * 
 * @author Thomas Freese
 */
public class Example2Route extends RouteBuilder
{
	/**
	 * Erstellt ein neues {@link Example2Route} Object.
	 */
	public Example2Route()
	{
		super();
	}

	/**
	 * @see org.apache.camel.builder.RouteBuilder#configure()
	 */
	@Override
	public void configure() throws Exception
	{
		//@formatter:off
		from("direct:say_hello")
			.routeId("Route1")
			.threads()
			.setBody(constant("Hello ").append(body()))
			.log(body().toString()).to("stream:out");
		//@formatter:on
	}
}
