/**
 * 20.12.2006
 */
package de.freese.jpa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.hibernate.SessionFactory;
import org.hibernate.ejb.EntityManagerFactoryImpl;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.Statistics;
import org.springframework.util.StopWatch;

import de.freese.jpa.model.Address;
import de.freese.jpa.model.Person;
import de.freese.jpa.util.JPAUtil;

/**
 * @author Thomas Freese
 */
public class TestAnnotationJPA
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht
	 */
	public static void main(final String[] args) throws Exception
	{
		TestAnnotationJPA testJPA = new TestAnnotationJPA();

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
	private StopWatch timer = new StopWatch();

	/**
	 * Creates a new {@link TestAnnotationJPA} object.
	 */
	public TestAnnotationJPA()
	{
		super();
	}

	/**
	 * 
	 */
	public void close()
	{
		// EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
		// EntityTransaction tx = entityManager.getTransaction();
		// tx.begin();
		//
		// Query query = entityManager.createNativeQuery("SHUTDOWN COMPACT");
		// query.executeUpdate();
		//
		// tx.commit();
		// entityManager.close();
		JPAUtil.getEntityManagerFactory().close();
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void deleteAll()
	{
		EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();

		tx.begin();

		Query query = entityManager.createQuery("from " + Person.class.getName());

		List<Person> result = query.getResultList();

		// int i = 0;
		for (Person person : result)
		{
			entityManager.remove(person);

			// if ((i++ % 20) == 0)
			// { //20, same as the JDBC batch size
			// //flush a batch of inserts and release memory:
			// entityManager.flush();
			// entityManager.clear();
			// }
		}

		tx.commit();
		entityManager.close();
	}

	/**
	 * @param persons {@link List}
	 */
	private void dumpPersons(final List<Person> persons)
	{
		// PreLoad-Pattern
		// ExpressionParser parser = new SpelExpressionParser();
		// StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
		// evaluationContext.setRootObject(persons);
		// parser.parseExpression("elementData.![addresses[0]]").getValue(evaluationContext,
		// Object.class);

		for (Person person : persons)
		{
			System.out.println("Person: " + person);

			// evaluationContext.setRootObject(person);
			// parser.parseExpression("addresses[0]").getValue(evaluationContext, Address.class);

			for (Address address : person.getAddresses())
			{
				System.out.println("\tAddress: " + address);
				System.out.println("\t\tPerson: " + address.getPerson());
			}
		}

		System.out.println();
	}

	/**
	 * 
	 */
	public void insert()
	{
		EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();

		tx.begin();

		try
		{
			for (int i = 0; i < 3; i++)
			{
				Person person1 = new Person();

				person1.setName("" + System.currentTimeMillis());
				person1.setVorName("" + System.currentTimeMillis());

				Address address = new Address();
				address.setStreet("1" + System.currentTimeMillis());
				person1.addAddress(address);
				// address.setPerson(person1);

				Thread.sleep(50);

				address = new Address();
				address.setStreet("2" + System.currentTimeMillis());
				person1.addAddress(address);
				// address.setPerson(person1);

				entityManager.persist(person1);

				// if ((i % 20) == 0)
				// { //20, same as the JDBC batch size
				// //flush a batch of inserts and release memory:
				// entityManager.flush();
				// entityManager.clear();
				// }
				// if (false)
				// {
				// throw new Exception("RollbackTest");
				// }

			}

			tx.commit();
		}
		catch (Throwable th)
		{
			th.printStackTrace();

			System.err.println("Rollback TX");
			tx.rollback();
		}
		finally
		{
			entityManager.close();
		}
	}

	/**
	 * @param persons {@link List}
	 */
	@SuppressWarnings("unchecked")
	private void serializePersons(final List<Person> persons)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			oos.writeObject(persons);
			oos.close();

			byte[] bytes = baos.toByteArray();

			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);

			List<Person> data = (List<Person>) ois.readObject();

			Address a = new Address();
			a.setStreet("d");
			data.get(0).addAddress(a);

			dumpPersons(data);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void show()
	{
		EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
		EntityTransaction tx = entityManager.getTransaction();

		tx.begin();

		Query query = entityManager.createQuery("from " + Person.class.getName());

		query.setHint("org.hibernate.cacheable", Boolean.TRUE);

		this.timer.start();
		List<Person> result = query.getResultList();

		this.timer.stop();
		System.out.println(this.timer.shortSummary());
		// this.timer.reset();

		dumpPersons(result);
		// serializePersons(result);

		tx.commit();
		// entityManager.clear();
		entityManager.close();

		// serializePersons(result);
	}

	/**
	 * 
	 */
	public void statistics()
	{
		SessionFactory sessionFactory =
				((EntityManagerFactoryImpl) JPAUtil.getEntityManagerFactory()).getSessionFactory();

		Statistics stats = sessionFactory.getStatistics();

		double queryCacheHitCount = stats.getQueryCacheHitCount();
		double queryCacheMissCount = stats.getQueryCacheMissCount();
		double queryCacheHitRatio = queryCacheHitCount / (queryCacheHitCount + queryCacheMissCount);

		System.out.println("SQL Query Hit Count: " + queryCacheHitCount);
		System.out.println("SQL Query Miss Count: " + queryCacheMissCount);
		System.out.println("SQL Query Hit ratio %: " + (queryCacheHitRatio * 100));

		Class<?>[] clazzes = new Class[]
		{
				Person.class, Address.class
		};

		for (Class<?> clazz : clazzes)
		{
			String className = clazz.getName();

			EntityStatistics entityStats = stats.getEntityStatistics(className);
			long inserts = entityStats.getInsertCount();
			long updates = entityStats.getUpdateCount();
			long deletes = entityStats.getDeleteCount();
			long fetches = entityStats.getFetchCount();
			long loads = entityStats.getLoadCount();
			long changes = inserts + updates + deletes;

			System.out.println(className + " fetches " + fetches + " times");
			System.out.println(className + " loads " + loads + " times");
			System.out.println(className + " inserts " + inserts + " times");
			System.out.println(className + " updates " + updates + " times");
			System.out.println(className + " deletes " + deletes + " times");
			System.out.println(className + " changed " + changes + " times");
		}
	}
}
