package cloudsession;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class CloudSessionCache implements CloudSession
{
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 
	 */
	public static final String TIMEOUT = "timeout";

	/**
	 * 
	 */
	private final CloudSession cs;

	/**
	 * 
	 */
	private int defaultSessionLivetimeInSecs = 0;

	/**
	 * 
	 */
	private Map<String, Map<String, Object>> hash = Collections
			.synchronizedMap(new HashMap<String, Map<String, Object>>());

	/**
	 * Erstellt ein neues {@link CloudSessionCache} Object.
	 * 
	 * @param cs {@link CloudSession}
	 * @param defaultSessionLivetimeInSecs int
	 */
	public CloudSessionCache(final CloudSession cs, final int defaultSessionLivetimeInSecs)
	{
		super();

		this.cs = cs;
		this.defaultSessionLivetimeInSecs = defaultSessionLivetimeInSecs;
		this.logger.error("");
	}

	/**
	 * @param sessionID String
	 * @param name String
	 * @param entry {@link Map}
	 * @return Object
	 */
	private Object checkValueInCloudAndUpdateLocal(final String sessionID, final String name,
													final Map<String, Object> entry)
	{
		Object cloudSessionValue = this.cs.getSessionValue(sessionID, name);

		if (cloudSessionValue != null)
		{
			this.logger.info("found value [{}] in persistent cache!", cloudSessionValue);

			if (entry == null)
			{
				// set local entry and update timeout in cloud
				setSessionValue(sessionID, name, cloudSessionValue);
			}
			else
			{
				// renew all timeouts
				renewTimeout(sessionID, entry);
			}
		}
		else
		{
			// remove session information
			remove(sessionID);
		}

		return cloudSessionValue;
	}

	/**
	 * @see cloudsession.CloudSession#getSessionValue(java.lang.String, java.lang.String)
	 */
	@Override
	public Object getSessionValue(final String sessionID, final String name)
	{
		// search in my cache
		Map<String, Object> entry = this.hash.get(sessionID);

		if (entry == null)
		{
			// if not found in cache ...
			this.logger.info("no entry [{},{}] found in memory cache!", sessionID, name);

			return checkValueInCloudAndUpdateLocal(sessionID, name, entry);

		}
		else if (timeoutReached((Long) entry.get(TIMEOUT)))
		{
			this.logger.info("entry [{}] has timeout -> check if present in cloud!", sessionID);

			return checkValueInCloudAndUpdateLocal(sessionID, name, entry);

		}
		else
		{
			this.logger.info("entry [{},{}] found in memory cache!", sessionID, name);
		}

		// value found in local cache and timeout not reached -> renew timeout
		renewTimeout(sessionID, entry);

		return entry.get(name);
	}

	/**
	 * @see cloudsession.CloudSession#remove(java.lang.String)
	 */
	@Override
	public void remove(final String sessionID)
	{
		this.cs.remove(sessionID);
		this.hash.remove(sessionID);
	}

	/**
	 * @param sessionID String
	 * @param entry {@link Map}
	 */
	private void renewTimeout(final String sessionID, final Map<String, Object> entry)
	{
		// calculate new timeout
		long timeout = System.currentTimeMillis() + (this.defaultSessionLivetimeInSecs * 1000);
		// renew cloud session timeout
		this.cs.setSessionValue(sessionID, TIMEOUT, new Long(timeout));

		this.logger.info("setting entry [{},{},{}]", new Object[]
		{
				sessionID, TIMEOUT, Long.valueOf(timeout)
		});

		// renew this cache timeout
		entry.put(TIMEOUT, new Long(timeout));
	}

	/**
	 * @see cloudsession.CloudSession#setSessionValue(java.lang.String, java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setSessionValue(final String sessionID, final String name, final Object value)
	{
		this.logger.info("setting entry [{},{},{}]", new Object[]
		{
				sessionID, name, value
		});

		Map<String, Object> entry = this.hash.get(sessionID);

		if (entry == null)
		{
			entry = Collections.synchronizedMap(new HashMap<String, Object>());
			this.hash.put(sessionID, entry);
		}

		// update value in my cache
		entry.put(name, value);

		// set value in cloud
		this.cs.setSessionValue(sessionID, name, value);

		renewTimeout(sessionID, entry);
	}

	/**
	 * @param timeout Long
	 * @return boolean
	 */
	private boolean timeoutReached(final Long timeout)
	{
		if (timeout != null)
		{
			long tLong = timeout.longValue();

			if (System.currentTimeMillis() > tLong)
			{
				return true;
			}
		}

		return false;
	}
}
