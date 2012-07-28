/**
 * Created: 17.12.2011
 */

package de.freese.sonstiges.jcr;

import java.util.Hashtable;

import javax.jcr.Repository;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.jackrabbit.core.jndi.RegistryHelper;

/**
 * @author Thomas Freese
 */
public class TestJackrabbit extends AbstractJcrTest
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		// FileDataStore dataStore = new FileDataStore();
		// dataStore.init(".");
		//
		// StringInputStream sis = new StringInputStream("Test-" + System.currentTimeMillis());
		//
		// DataRecord dataRecord = dataStore.addRecord(sis);
		// DataIdentifier dataIdentifier = dataRecord.getIdentifier();
		//
		// dataRecord = dataStore.getRecordIfStored(dataIdentifier);
		//
		// System.out.println(dataRecord.getStream());

		// !!! ACHTUNG !!!
		// Jackrabbit benötigt org.apache.lucen:lucene-core:3.1.0 !!!

		AbstractJcrTest jcrTest = new TestJackrabbit();

		String configFile = "jackrabbit-config.xml"; // Default repository.xml
		String repHomeDir = "repositoryJackrabbit";

		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.jackrabbit.core.jndi.provider.DummyInitialContextFactory");

		env.put(Context.PROVIDER_URL, "localhost");

		InitialContext ctx = new InitialContext(env);
		RegistryHelper.registerRepository(ctx, "repo", configFile, repHomeDir, true);

		Repository repository = (Repository) ctx.lookup("repo");

		try
		{
			jcrTest.doExample(repository);
		}
		catch (Throwable th)
		{
			jcrTest.getLogger().error(null, th);
		}
		finally
		{
			jcrTest.shutdown();
		}
	}

	/**
	 * Erstellt ein neues {@link TestJackrabbit} Object.
	 */
	public TestJackrabbit()
	{
		super();
	}
}
