/**
 * 05.04.2008
 */
package de.freese.queryengine.transformer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.freese.queryengine.Query;
import de.freese.queryengine.QueryLink;
import de.freese.queryengine.QueryRequest;
import de.freese.queryengine.condition.FieldCondition;
import de.freese.queryengine.condition.ICondition;
import de.freese.queryengine.condition.Operator;

/**
 * Transformer fuer SQL Queries der QueryEngine.
 * 
 * @author Thomas Freese
 */
public class SQLQueryTransformer implements IQueryTransformer
{
	/**
     * 
     */
	private static final Map<String, String> sqlOperatorMapping = new HashMap<>();

	static
	{
		sqlOperatorMapping.put(Operator.AND.getOperator(), "and");
		sqlOperatorMapping.put(Operator.BETWEEN.getOperator(), "between");
		sqlOperatorMapping.put(Operator.EQUALS.getOperator(), "=");
		sqlOperatorMapping.put(Operator.GREATER.getOperator(), ">");
		sqlOperatorMapping.put(Operator.LIKE.getOperator(), "like");
		sqlOperatorMapping.put(Operator.LOWER.getOperator(), "<");
		sqlOperatorMapping.put(Operator.IN.getOperator(), "in");
		sqlOperatorMapping.put(Operator.NOT.getOperator(), "not");
		sqlOperatorMapping.put(Operator.OR.getOperator(), "or");
		sqlOperatorMapping.put(Operator.EQUALS_GREATER.getOperator(), ">=");
		sqlOperatorMapping.put(Operator.EQUALS_LOWER.getOperator(), "<=");
		sqlOperatorMapping.put(Operator.NOT_BETWEEN.getOperator(), "not between");
		sqlOperatorMapping.put(Operator.NOT_EQUALS.getOperator(), "<>");
	}

	// /**
	// *
	// */
	// private final Map aliasMap = new HashMap();

	/**
     * 
     */
	private QueryRequest queryRequest = null;

	/**
	 * Creates a new {@link SQLQueryTransformer} object.
	 */
	public SQLQueryTransformer()
	{
		super();
	}

	/**
	 * Erzeugt die Header-Klausel: Select Fields from TABLE
	 * 
	 * @param builder {@link StringBuilder}
	 */
	protected void createHeader(final StringBuilder builder)
	{
		builder.append("select ");

		List<Query> queries = this.queryRequest.getQueries();

		if (queries.size() > 0)
		{
			for (Iterator<Query> iterator = queries.iterator(); iterator.hasNext();)
			{
				Query query = iterator.next();

				createQueryValues(builder, query);

				if (iterator.hasNext())
				{
					builder.append(",");
				}
			}
		}

		builder.append(" from ");

		if (queries.size() > 0)
		{
			for (Iterator<Query> iterator = queries.iterator(); iterator.hasNext();)
			{
				Query query = iterator.next();

				createQueryTables(builder, query);

				if (iterator.hasNext())
				{
					builder.append(",");
				}
			}
		}
	}

	/**
	 * @see de.freese.queryengine.transformer.IQueryTransformer#createNativQuery()
	 */
	@Override
	public Object createNativQuery()
	{
		StringBuilder builder = new StringBuilder(2000);

		createHeader(builder);
		createWhereClause(builder);
		createOrderBy(builder);

		return builder.toString();
	}

	/**
	 * Erzeugt die order by-Klausel: order by FIELDS
	 * 
	 * @param builder {@link StringBuilder}
	 */
	protected void createOrderBy(final StringBuilder builder)
	{
		List<Query> queries = this.queryRequest.getQueries();

		if (queries.size() > 0)
		{
			builder.append(" order by ");

			for (int i = 0; i < queries.size(); i++)
			{
				Query query = queries.get(i);

				createQueryOrderBy(builder, query);

				if ((i < (queries.size() - 1)))
				{
					Query nextQuery = queries.get(i + 1);

					if (nextQuery.getOrderBy() != null)
					{
						builder.append(",");
					}
				}
			}
		}
	}

	/**
	 * Erzeugt die order by-Klausel: ALIAS.COLUMN
	 * 
	 * @param builder StringBuffer
	 * @param query {@link Query}
	 */
	protected void createQueryOrderBy(final StringBuilder builder, final Query query)
	{
		if (query.getOrderBy() != null)
		{
			Object[] orderBy = query.getOrderBy();

			for (int i = 0; i < orderBy.length; i++)
			{
				builder.append(getAlias(query));
				builder.append(".");
				builder.append(orderBy[i]);

				if (i < (query.getOrderBy().length - 1))
				{
					builder.append(",");
				}
			}
		}
	}

	/**
	 * Erzeugt die from-Klausel: ALIAS AS TABLE
	 * 
	 * @param builder {@link StringBuilder}
	 * @param query {@link Query}
	 */
	protected void createQueryTables(final StringBuilder builder, final Query query)
	{
		builder.append(getAlias(query));
		builder.append(" as ");
		builder.append(query.getTarget());
	}

	/**
	 * Erzeugt die values-Klausel: ALIAS.COLUMN
	 * 
	 * @param builder {@link StringBuilder}
	 * @param query {@link Query}
	 */
	protected void createQueryValues(final StringBuilder builder, final Query query)
	{
		if (query.getFilter() == null)
		{
			builder.append(getAlias(query));
			builder.append(".* ");
		}
		else
		{
			Object[] filter = query.getFilter();

			for (int i = 0; i < filter.length; i++)
			{
				builder.append(getAlias(query));
				builder.append(".");
				builder.append(filter[i]);

				if (i < (filter.length - 1))
				{
					builder.append(",");
				}
			}
		}
	}

	/**
	 * Erzeugt die where-Klausel (Queryverknuepfung): where PARENT_FILED = CHILD_FIELD
	 * 
	 * @param builder {@link StringBuilder}
	 * @param queryLink {@link QueryLink}
	 */
	protected void createWhereClause(final StringBuffer builder, final QueryLink queryLink)
	{
		builder.append(getAlias(queryLink.getParentQuery()));
		builder.append(".");
		builder.append(queryLink.getParentField());

		builder.append(getSQLOperator(Operator.EQUALS));

		builder.append(getAlias(queryLink.getChildQuery()));
		builder.append(".");
		builder.append(queryLink.getChildField());
	}

	/**
	 * Erzeugt die where-Klausel: where CONDITIONS
	 * 
	 * @param builder {@link StringBuilder}
	 */
	protected void createWhereClause(final StringBuilder builder)
	{
		// Verknuepfungen zwischen Queries abarbeiten
		List<QueryLink> queryLinks = this.queryRequest.getQueryLinks();

		if (queryLinks.size() > 0)
		{
			builder.append(" where ");

			for (Iterator<QueryLink> iterator = queryLinks.iterator(); iterator.hasNext();)
			{
				QueryLink queryLink = iterator.next();

				createWhereClause(builder, queryLink);

				if (iterator.hasNext())
				{
					builder.append(",");
				}
			}
		}

		// Bedingungen abarbeiten
		List<Query> queries = this.queryRequest.getQueries();

		if (queries.size() > 0)
		{
			if (queryLinks.size() == 0)
			{
				builder.append(" where ");
			}
			else
			{
				builder.append(" ");
				builder.append(getSQLOperator(Operator.AND));
			}

			for (int i = 0; i < queries.size(); i++)
			{
				Query query = queries.get(i);

				createWhereClause(builder, query);

				if (i < (queries.size() - 1))
				{
					Query nextQuery = queries.get(i + 1);

					if (nextQuery.getConditions().size() > 0)
					{
						builder.append(" ");
						builder.append(getSQLOperator(Operator.AND));
					}
				}
			}
		}
	}

	/**
	 * Erzeugt die where-Klausel einer {@link Query}.
	 * 
	 * @param builder {@link StringBuilder}
	 * @param query {@link Query}
	 */
	protected void createWhereClause(final StringBuilder builder, final Query query)
	{
		List<ICondition> conditions = query.getConditions();

		for (Iterator<ICondition> conditionIter = conditions.iterator(); conditionIter.hasNext();)
		{
			ICondition condition = conditionIter.next();

			if (condition instanceof FieldCondition)
			{
				createWhereClause(builder, query, (FieldCondition) condition);
			}
			else
			{
				continue;
			}

			if (conditionIter.hasNext())
			{
				builder.append(" ");
				builder.append(getSQLOperator(Operator.AND));
			}
		}
	}

	/**
	 * Erzeugt die where-Klausel einer {@link FieldCondition}.
	 * 
	 * @param builder {@link StringBuilder}
	 * @param query {@link Query}
	 * @param condition {@link FieldCondition}
	 */
	protected void createWhereClause(final StringBuilder builder, final Query query,
										final FieldCondition condition)
	{
		builder.append(" ");

		builder.append(getAlias(query));
		builder.append(".");
		builder.append(condition.getFieldName());

		builder.append(" ");

		if (Operator.BETWEEN.equals(condition.getOperator()))
		{
			builder.append(getSQLOperator(condition.getOperator()));
			builder.append(" ");
			builder.append(condition.getValue1());
			builder.append(" ");
			builder.append(getSQLOperator(Operator.AND));
			builder.append(" ");
			builder.append(condition.getValue2());
		}
		else
		{
			builder.append(getSQLOperator(condition.getOperator()));
			builder.append(" ");
			builder.append(condition.getValue1());
		}
	}

	/**
	 * Erzeugt die where-Klausel (Queryverknuepfung): where PARENT_FILED = CHILD_FIELD
	 * 
	 * @param builder {@link StringBuilder}
	 * @param queryLink {@link QueryLink}
	 */
	protected void createWhereClause(final StringBuilder builder, final QueryLink queryLink)
	{
		builder.append(getAlias(queryLink.getParentQuery()));
		builder.append(".");
		builder.append(queryLink.getParentField());

		builder.append(getSQLOperator(Operator.EQUALS));

		builder.append(getAlias(queryLink.getChildQuery()));
		builder.append(".");
		builder.append(queryLink.getChildField());
	}

	/**
	 * Erzeugt den Aliasnamen der Query.
	 * 
	 * @param query {@link Query}
	 * @return String
	 */
	protected String getAlias(final Query query)
	{
		String alias = query.getTarget().toString();

		return alias;

		// int anzahlAlias = 1;
		//
		// for (Iterator iter = this.aliasList.iterator(); iter.hasNext();)
		// {
		// String aliasKey = (String) iter.next();
		//
		// if (alias.equals(aliasKey.split("_")[0]))
		// {
		// if (seitenName.equals(seitenKey.split(DELIM)[1]))
		// {
		// anzahlSeitenNamen++;
		// }
		// }
		// }
		//
		// if (anzahlSeitenNamen > 1)
		// {
		// // Seitenname mit Nummer erweitern
		// return seitenName += ("(" + anzahlSeitenNamen + ")");
		// }
		// else
		// {
		// return seitenName;
		// }
	}

	/**
	 * Liefert das SQL Equivalent des {@link Operator}s.
	 * 
	 * @param operator {@link Operator}
	 * @return String
	 * @throws IllegalArgumentException Falls was schief geht.
	 */
	protected String getSQLOperator(final Operator operator)
	{
		String sqlOperator = sqlOperatorMapping.get(operator.getOperator());

		if (sqlOperator == null)
		{
			throw new IllegalArgumentException("Unbekannter SQL Operator");
		}

		return sqlOperator;
	}

	/**
	 * @see de.freese.queryengine.transformer.IQueryTransformer#setQuery(de.freese.queryengine.QueryRequest)
	 */
	@Override
	public void setQuery(final QueryRequest queryRequest)
	{
		this.queryRequest = queryRequest;
	}
}
