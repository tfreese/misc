/**
 *
 */
package de.freese.queryengine.transformer;

import de.freese.queryengine.QueryRequest;
import de.freese.queryengine.performer.IQueryPerformer;

/**
 * Formattiert die Query, sodass die vom {@link IQueryPerformer} ausgefuehrt werden kann.
 * 
 * @author Thomas Freese
 */
public interface IQueryTransformer
{
	/**
	 * Setzen der {@link QueryRequest}.
	 * 
	 * @param queryRequest {@link QueryRequest}
	 */
	public void setQuery(QueryRequest queryRequest);

	/**
	 * Liefert das konkrete Queryobject fuer den {@link IQueryPerformer}.
	 * 
	 * @return Object
	 */
	public Object createNativQuery();
}
