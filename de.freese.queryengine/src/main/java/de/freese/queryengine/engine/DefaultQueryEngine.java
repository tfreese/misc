/**
 * 05.04.2008
 */
package de.freese.queryengine.engine;

import de.freese.queryengine.performer.IQueryPerformer;
import de.freese.queryengine.transformer.IQueryTransformer;

/**
 * Defaultimplementierung einer QueryEngine.
 *
 * @author Thomas Freese
 */
public class DefaultQueryEngine extends AbstractQueryEngine
{
    /**
     * Creates a new DefaultQueryEngine object.
     *
     * @param queryTransformer {@link IQueryTransformer}
     * @param queryPerformer {@link IQueryPerformer}
     */
    public DefaultQueryEngine(IQueryTransformer queryTransformer, IQueryPerformer queryPerformer)
    {
        super(queryTransformer, queryPerformer);
    }
}
