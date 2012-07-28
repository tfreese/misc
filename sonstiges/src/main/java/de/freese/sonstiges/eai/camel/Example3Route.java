/**
 * Created: 13.07.2012
 */

package de.freese.sonstiges.eai.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;

import de.freese.sonstiges.eai.camel.csv.PriceAggregationStrategy;
import de.freese.sonstiges.eai.camel.csv.ProductOrder;
import de.freese.sonstiges.eai.camel.csv.ReadProductOrderSplitter;

/**
 * JavaMagazin 11.2011
 * 
 * @author Thomas Freese
 */
public class Example3Route extends RouteBuilder
{
	/**
	 * Erstellt ein neues {@link Example3Route} Object.
	 */
	public Example3Route()
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
		from("file:src/test/resources/?fileName=customer.csv&noop=true")
			.routeId("Route2")
			.threads()
//			.setHeader("key", bean(UUID.class, "randomUUID")) 
			.unmarshal()
			.bindy(BindyType.Csv, "de.freese.sonstiges.eai.camel.csv")
			.split().method(ReadProductOrderSplitter.class)			
			.log("Read: '${body}'")
			.enrich("direct:calculate_total",new PriceAggregationStrategy())
			.log("Enriched: '${body}'\n")
			.marshal().bindy(BindyType.Csv, "de.freese.sonstiges.eai.camel.csv")
			.to("file:.?fileName=customerTotals.csv&fileExist=Append")
			;
		//@formatter:on

		//@formatter:off
		from("direct:calculate_total")
			.routeId("EnrichRoute")
			.threads()
			.throttle(1).timePeriodMillis(2000) // Nur alle 2 Sekunden ein Objekt.
			.process(new Processor()
			{
				/**
				 * 
				 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
				 */
				@Override
				public void process(final Exchange exchange) throws Exception
				{
					ProductOrder order = exchange.getIn().getBody(ProductOrder.class);
					
					int total = order.getQuantity() + order.getPrice(); 
//					order.setTotal(total);					
					exchange.getOut().setBody(Integer.valueOf(total));
				}
			})
//			.setBody(simple("${body.quantity} + ${body.price}"))
			;
		//@formatter:on		
	}
}
