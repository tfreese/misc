/**
 * Created: 13.07.2012
 */

package de.freese.sonstiges.eai.camel.csv;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * @author Thomas Freese
 */
public class PriceAggregationStrategy implements AggregationStrategy
{
	/**
	 * Erstellt ein neues {@link PriceAggregationStrategy} Object.
	 */
	public PriceAggregationStrategy()
	{
		super();
	}

	/**
	 * @see org.apache.camel.processor.aggregate.AggregationStrategy#aggregate(org.apache.camel.Exchange,
	 *      org.apache.camel.Exchange)
	 */
	@Override
	public Exchange aggregate(final Exchange oldExchange, final Exchange newExchange)
	{
		ProductOrder order = oldExchange.getIn().getBody(ProductOrder.class);
		Integer total = newExchange.getIn().getBody(Integer.class);

		if (total != null)
		{
			order.setTotal(total.intValue());
		}

		return oldExchange;
	}
}
