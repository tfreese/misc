/**
 * 07.04.2008
 */
package de.freese.queryengine;

import java.util.ArrayList;
import java.util.List;

/**
 * Enthaelt die Querydefinition der QueryEngine.
 * 
 * @author Thomas Freese
 */
public final class QueryRequest
{
	/**
     * 
     */
	private final List<Query> queries = new ArrayList<>();

	/**
     * 
     */
	private final List<QueryLink> queryLinks = new ArrayList<>();

	/**
	 * Creates a new QueryRequest object.
	 */
	public QueryRequest()
	{
		super();
	}

	/**
	 * Hinzufuegen einer Query.
	 * 
	 * @param query {@link Query}
	 */
	public void addQuery(final Query query)
	{
		this.queries.add(query);
	}

	/**
	 * Hinzufuegen eines QueryLinks.
	 * 
	 * @param queryLink {@link QueryLink}
	 */
	public void addQueryLink(final QueryLink queryLink)
	{
		this.queryLinks.add(queryLink);
	}

	/**
	 * Liefert alle Queries des Requests.
	 * 
	 * @return List aus {@link Query}'s
	 */
	public List<Query> getQueries()
	{
		return this.queries;
	}

	/**
	 * Liefert alle QueryLinks des Requests.
	 * 
	 * @return List aus {@link QueryLink}s
	 */
	public List<QueryLink> getQueryLinks()
	{
		return this.queryLinks;
	}
}
