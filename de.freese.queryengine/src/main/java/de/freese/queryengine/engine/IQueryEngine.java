/**
 *
 */
package de.freese.queryengine.engine;

import java.util.List;

import de.freese.base.core.model.row.RowMetaAndData;
import de.freese.queryengine.QueryRequest;

/**
 * Interface einer QueryEngine.
 * 
 * @author Thomas Freese
 */
public interface IQueryEngine
{
	/**
	 * Fuehrt die Query aus und liefert das Ergebniss.
	 * 
	 * @param queryRequest {@link QueryRequest}
	 * @return {@link List}
	 */
	public List<RowMetaAndData> execute(QueryRequest queryRequest);
}
