/**
 * Created: 13.07.2012
 */

package de.freese.sonstiges.eai.camel.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class ReadProductOrderSplitter
{
	/**
	 * Erstellt ein neues {@link ReadProductOrderSplitter} Object.
	 */
	public ReadProductOrderSplitter()
	{
		super();
	}

	/**
	 * @param body {@link List}
	 * @return {@link List}
	 */
	public List<ProductOrder> split(final List<Map<Class<?>, ProductOrder>> body)
	{
		List<ProductOrder> result = new ArrayList<>();

		for (Map<Class<?>, ProductOrder> bodyMap : body)
		{
			result.addAll(bodyMap.values());
			// for (ProductOrder order : bodyMap.values())
			// {
			// result.add(order);
			// }
		}

		return result;
	}
}
