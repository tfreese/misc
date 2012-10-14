/**
 * 05.04.2008
 */
package de.freese.queryengine;

import java.util.List;

import javax.sql.DataSource;

import de.freese.base.core.model.row.RowMetaAndData;
import de.freese.queryengine.condition.FieldCondition;
import de.freese.queryengine.condition.Operator;
import de.freese.queryengine.engine.DefaultQueryEngine;
import de.freese.queryengine.engine.IQueryEngine;
import de.freese.queryengine.performer.JDBCQueryPerformer;
import de.freese.queryengine.performer.JDBCSession;
import de.freese.queryengine.transformer.SQLQueryTransformer;

/**
 * Testklasse fuer SQL Queries.
 * 
 * @author Thomas Freese
 */
public class TestSQLQueryEngine
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		DataSource dataSource = JDBCUtils.getDataSource();

		JDBCSession.setDataSource(dataSource);

		IQueryEngine queryEngine =
				new DefaultQueryEngine(new SQLQueryTransformer(), new JDBCQueryPerformer());

		// Person Query
		Query queryPerson = new Query("Person");
		queryPerson.addCondition(new FieldCondition(Operator.BETWEEN, "ALTER", Long.valueOf(20),
				Long.valueOf(40)));

		// queryPerson.addCondition(new FieldCondition(Operator.NOT_EQUALS, "ALTER",
		// Long.valueOf(33)));
		// queryPerson.addCondition(new FieldCondition(Operator.LIKE, "VORNAME", "'Hei%'"));
		queryPerson.setFilter("NAME", "VORNAME", "ALTER");
		queryPerson.setOrderBy("VORNAME asc");

		// Address Query
		Query queryAddresse = new Query("Addresse");
		queryAddresse
				.addCondition(new FieldCondition(Operator.GREATER, "PLZ", Long.valueOf(30000)));
		queryAddresse.setFilter("PLZ", "ORT");
		queryAddresse.setOrderBy("ORT asc");

		QueryRequest queryRequest = new QueryRequest();
		queryRequest.addQuery(queryPerson);
		queryRequest.addQuery(queryAddresse);
		queryRequest.addQueryLink(new QueryLink(queryPerson, "ID", queryAddresse, "PERSON_ID"));

		List<RowMetaAndData> queryResult = queryEngine.execute(queryRequest);

		for (RowMetaAndData rowMetaAndData : queryResult)
		{
			System.out.println(rowMetaAndData.toString());
		}
	}
}
