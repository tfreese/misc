/**
 *
 */
package de.freese.queryengine.engine;

import java.util.List;

import de.freese.base.core.model.row.RowMetaAndData;
import de.freese.queryengine.QueryRequest;
import de.freese.queryengine.performer.IQueryPerformer;
import de.freese.queryengine.transformer.IQueryTransformer;

/**
 * Basisklasse einer QueryEngine.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractQueryEngine implements IQueryEngine
{
	/**
     * 
     */
	private final IQueryPerformer queryPerformer;

	/**
     * 
     */
	private final IQueryTransformer queryTransformer;

	/**
	 * Creates a new AbstractQueryEngine object.
	 * 
	 * @param queryTransformer IQueryTransformer
	 * @param queryPerformer IQueryPerformer
	 * @throws IllegalArgumentException Falls was schief geht.
	 */
	public AbstractQueryEngine(final IQueryTransformer queryTransformer,
			final IQueryPerformer queryPerformer)
	{
		super();

		if (queryTransformer == null)
		{
			throw new IllegalArgumentException("QueryTransformer ist null !");
		}

		if (queryPerformer == null)
		{
			throw new IllegalArgumentException("QueryPerformer ist null !");
		}

		this.queryTransformer = queryTransformer;
		this.queryPerformer = queryPerformer;
	}

	/**
	 * @see de.freese.queryengine.engine.IQueryEngine#execute(de.freese.queryengine.QueryRequest)
	 */
	@Override
	public List<RowMetaAndData> execute(final QueryRequest queryRequest)
	{
		if (queryRequest == null)
		{
			throw new IllegalArgumentException("QueryRequest ist null !");
		}

		this.queryTransformer.setQuery(queryRequest);

		Object nativeQuery = this.queryTransformer.createNativQuery();

		List<RowMetaAndData> queryResult = this.queryPerformer.executeQuery(nativeQuery);

		return queryResult;
	}
}
