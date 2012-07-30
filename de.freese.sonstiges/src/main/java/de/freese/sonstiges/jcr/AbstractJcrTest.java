/**
 * Created: 17.12.2011
 */

package de.freese.sonstiges.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractJcrTest
{
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 
	 */
	private Session session = null;

	/**
	 * Erstellt ein neues {@link AbstractJcrTest} Object.
	 */
	public AbstractJcrTest()
	{
		super();
	}

	/**
	 * @param repository {@link Repository}
	 * @throws Throwable Falls was schief geht.
	 */
	protected void doExample(final Repository repository) throws Throwable
	{
		// Repository repository = new TransientRepository();
		// Credentials credentials = new SimpleCredentials("tommy", "password".toCharArray());
		// Session session = repository.login(credentials);
		this.session = repository.login();

		String user = this.session.getUserID();
		String repoName = repository.getDescriptor(Repository.REP_NAME_DESC);
		getLogger().info("Logged in as {} to a {} repository.", user, repoName);

		Node root = this.session.getRootNode();

		// Prüfen, ob Node schon existiert.
		try
		{
			root.getNode("test/hello/world");
		}
		catch (PathNotFoundException ex)
		{
			getLogger().error(null, ex);

			// Node anlegen.
			Node test = root.addNode("test");
			Node hello = test.addNode("hello");
			Node world = hello.addNode("world");
			world.setProperty("message", "Hello, World!");
			this.session.save();
		}

		// Node auslesen.
		Node node = root.getNode("test/hello/world");
		getLogger().info(node.getPath());
		getLogger().info(node.getProperty("message").getString());

		// Remove content
		// root.getNode("hello").remove();
		// session.save();

		dump(root);

		getLogger().info("Invoke Query");
		// Query
		// String roseTitle = rn.getProperty
		// ("wiki:encyclopedia/wiki:entry[1]/wiki:title").getString()
		QueryManager queryManager = this.session.getWorkspace().getQueryManager();
		Query query =
				queryManager.createQuery(
						"select * from [nt:unstructured] where message like 'Hello%'",
						Query.JCR_SQL2);

		QueryResult result = query.execute();
		NodeIterator it = result.getNodes();

		getLogger().info("Query Results:");

		while (it.hasNext())
		{
			Node n = it.nextNode();

			getLogger().info(n.getName());
			getLogger().info(n.getProperty("message").getString());
		}
	}

	/**
	 * Recursively outputs the contents of the given node.
	 * 
	 * @param node {@link Node}
	 * @throws RepositoryException Falls was schief geht.
	 */
	protected void dump(final Node node) throws RepositoryException
	{
		// First output the node path
		getLogger().info("Dump Node {}", node.getPath());

		// Skip the virtual (and large!) jcr:system subtree
		if (node.getName().equals("jcr:system"))
		{
			return;
		}

		// Then output the properties
		PropertyIterator properties = node.getProperties();

		while (properties.hasNext())
		{
			Property property = properties.nextProperty();

			if (property.getDefinition().isMultiple())
			{
				// A multi-valued property, print all values
				Value[] values = property.getValues();

				for (Value value : values)
				{
					getLogger().info("{} = {}", property.getPath(), value.getString());
				}
			}
			else
			{
				// A single-valued property
				getLogger().info("{} = {}", property.getPath(), property.getString());
			}
		}

		// Finally output all the child nodes recursively
		NodeIterator nodes = node.getNodes();

		while (nodes.hasNext())
		{
			dump(nodes.nextNode());
		}
	}

	/**
	 * @return {@link Logger}
	 */
	protected Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * 
	 */
	protected void shutdown()
	{
		if (this.session != null)
		{
			this.session.logout();
		}
	}
}
