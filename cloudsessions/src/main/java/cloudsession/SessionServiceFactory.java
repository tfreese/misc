package cloudsession;

/**
 * @author Thomas Freese
 */
public class SessionServiceFactory
{
	/**
	 * 
	 */
	private static CloudSession session = null;

	/**
	 * 
	 */
	private static int defaultSessionLivetimeInSecs = 15;

	/**
	 * @return {@link CloudSession}
	 */
	public static CloudSession getService()
	{
		return getService(defaultSessionLivetimeInSecs);
	}

	/**
	 * @param defaultSessionLivetimeInSecs int
	 * @return {@link CloudSession}
	 */
	public static CloudSession getService(final int defaultSessionLivetimeInSecs)
	{
		if (session == null)
		{
			CloudSession cs = new DummySessionService();
			// CloudSession cs = new AmazonSessionService();

			session = new CloudSessionCache(cs, defaultSessionLivetimeInSecs);
		}

		return session;
	}
}
