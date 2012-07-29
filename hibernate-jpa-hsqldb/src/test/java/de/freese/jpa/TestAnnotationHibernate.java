/**
 * 20.12.2006
 */
package de.freese.jpa;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.util.StopWatch;

import de.freese.jpa.model.Person;
import de.freese.jpa.util.HibernateUtil;

/**
 * @author Thomas Freese; thomas&#169;freese-home.de
 */
public class TestAnnotationHibernate
{
	/**
     * 
     */
	private StopWatch timer = new StopWatch();

	/**
	 * Creates a new {@link TestAnnotationHibernate} object.
	 */
	public TestAnnotationHibernate()
	{
		super();
	}

	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht
	 */
	public static void main(final String[] args) throws Exception
	{
		TestAnnotationHibernate testJPA = new TestAnnotationHibernate();

		// testJPA.deleteAll();

		// testJPA.insert();

		testJPA.show();
		Thread.currentThread();
		Thread.sleep(3000);
		testJPA.show();
		Thread.currentThread();
		Thread.sleep(3000);
		testJPA.show();

		testJPA.statistics();

		testJPA.close();
	}

	/**
	 * 
	 */
	public void close()
	{
		// Session session = HibernateUtil.getSessionFactory().openSession();
		//
		// Query query = session.createSQLQuery("SHUTDOWN COMPACT");
		// query.executeUpdate();
		// session.close();
		HibernateUtil.getSessionFactory().close();
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void deleteAll()
	{
		Session session = HibernateUtil.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(Person.class);
		List<Person> result = criteria.list();

		for (Person person : result)
		{
			session.delete(person);
		}

		session.close();
	}

	/**
	 * 
	 */
	public void insert()
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		try
		{
			for (int i = 0; i < 100; i++)
			{
				Person person1 = new Person();

				person1.setName("" + System.currentTimeMillis());
				person1.setVorName("" + System.currentTimeMillis());
				session.persist(person1);

				// if ((i % 10) == 0)
				// { //10, same as the JDBC batch size
				// //flush a batch of inserts and release memory:
				// session.flush();
				// session.clear();
				// }
				if (false)
				{
					throw new Exception("RollbackTest");
				}

				Thread.currentThread();
				Thread.sleep(50);
			}

			tx.commit();
		}
		catch (Throwable th)
		{
			th.printStackTrace();

			// tx.rollback();
		}
		finally
		{
			session.close();
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void show()
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();

		Criteria criteria = session.createCriteria(Person.class);

		criteria.setCacheable(true);

		this.timer.start();
		List<Person> result = criteria.list();

		this.timer.stop();
		System.out.println(this.timer.shortSummary());
		// this.timer.reset();

		for (Person person : result)
		{
			// System.out.println(person);
			person.toString();
		}

		tx.commit();
		session.close();
	}

	/**
	 * 
	 */
	public void statistics()
	{
		Statistics stats = HibernateUtil.getSessionFactory().getStatistics();

		double queryCacheHitCount = stats.getQueryCacheHitCount();
		double queryCacheMissCount = stats.getQueryCacheMissCount();
		double queryCacheHitRatio = queryCacheHitCount / (queryCacheHitCount + queryCacheMissCount);

		System.out.println("SQL Query Hit Count: " + queryCacheHitCount);
		System.out.println("SQL Query Miss Count: " + queryCacheMissCount);
		System.out.println("SQL Query Hit ratio %: " + (queryCacheHitRatio * 100));

		EntityStatistics entityStats = stats.getEntityStatistics(Person.class.getName());
		long inserts = entityStats.getInsertCount();
		long updates = entityStats.getUpdateCount();
		long deletes = entityStats.getDeleteCount();
		long fetches = entityStats.getFetchCount();
		long loads = entityStats.getLoadCount();
		long changes = inserts + updates + deletes;

		System.out.println(Person.class.getName() + " fetches " + fetches + " times");
		System.out.println(Person.class.getName() + " loads " + loads + " times");
		System.out.println(Person.class.getName() + " inserts " + inserts + " times");
		System.out.println(Person.class.getName() + " updates " + updates + " times");
		System.out.println(Person.class.getName() + " deletes " + deletes + " times");
		System.out.println(Person.class.getName() + " changed " + changes + " times");
	}
}
