/**
 * Created: 13.07.2012
 */

package de.freese.sonstiges.eai.camel.csv;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * @author Thomas Freese
 */
public class OrderAggregationStrategy implements AggregationStrategy
{
	/**
	 * Erstellt ein neues {@link OrderAggregationStrategy} Object.
	 */
	public OrderAggregationStrategy()
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
		// TODO Auto-generated method stub
		return null;
	}
}
