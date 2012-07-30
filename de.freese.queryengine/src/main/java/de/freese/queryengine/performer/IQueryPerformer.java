/**
 *
 */
package de.freese.queryengine.performer;

import java.util.List;

import de.freese.base.model.row.RowMetaAndData;
import de.freese.queryengine.Query;
import de.freese.queryengine.transformer.IQueryTransformer;

/**
 * Fuehrt eine konkrete Query aus, die der {@link IQueryTransformer} aus der {@link Query} generiert
 * hat.
 * 
 * @author Thomas Freese
 */
public interface IQueryPerformer
{
	/**
	 * Fuehrt eine konkrete Query aus.
	 * 
	 * @param nativeQuery Object; Konkretes Queryobject fuer den QueryPerformer
	 * @return {@link List}
	 */
	public List<RowMetaAndData> executeQuery(Object nativeQuery);
}
