package cloudsession;

import util.Utilities;
import util.XmlSerializer;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchDeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.DeletableItem;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;

/**
 * @author Thomas Freese
 */
public class AmazonSessionService implements CloudSession
{
	/**
	 * 
	 */
	private static String SESSIONS_DOMAIN = "Sessions";

	/**
	 * 
	 */
	private final AmazonSimpleDB sdb;

	/**
	 * Erstellt ein neues {@link AmazonSessionService} Object.
	 */
	public AmazonSessionService()
	{
		super();

		this.sdb = new AmazonSimpleDBClient(new Utilities().getCreds());
	}

	/**
	 * @see cloudsession.CloudSession#getSessionValue(java.lang.String, java.lang.String)
	 */
	@Override
	public Object getSessionValue(final String sessionID, final String name)
	{
		GetAttributesResult gar =
				this.sdb.getAttributes(new GetAttributesRequest().withDomainName(SESSIONS_DOMAIN)
						.withItemName(sessionID));

		for (Attribute a : gar.getAttributes())
		{
			if (a.getName().equals(name))
			{
				return XmlSerializer.fromXML(a.getValue());
			}
		}

		return null;
	}

	/**
	 * @see cloudsession.CloudSession#remove(java.lang.String)
	 */
	@Override
	public void remove(final String sessionID)
	{
		this.sdb.batchDeleteAttributes(new BatchDeleteAttributesRequest().withDomainName(
				SESSIONS_DOMAIN).withItems(new DeletableItem().withName(sessionID)));
	}

	/**
	 * @see cloudsession.CloudSession#setSessionValue(java.lang.String, java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setSessionValue(final String sessionID, final String name, final Object value)
	{
		ReplaceableAttribute replAttr =
				new ReplaceableAttribute().withName(name).withValue(XmlSerializer.toXML(value))
						.withReplace(Boolean.TRUE);

		this.sdb.batchPutAttributes(new BatchPutAttributesRequest().withDomainName(SESSIONS_DOMAIN)
				.withItems(new ReplaceableItem().withName(sessionID).withAttributes(replAttr)));
	}
}
