package servlets;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cloudsession.CloudSession;
import cloudsession.SessionServiceFactory;

/**
 * @author Thomas Freese
 */
public class NonStickySessionTest extends HttpServlet
{
	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
     * 
     */
	private static final int CACHE_LIVETIME_SECONDS = 5;

	/**
     * 
     */
	private static final String creationTime = "creationTime";

	/**
     * 
     */
	private static final String JSESSIONID = "JSESSIONID=";

	/**
     * 
     */
	private static final String lastAccessTime = "lastAccessTime";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 
     */
	private static final String user = "user";

	/**
	 * Erstellt ein neues {@link NonStickySessionTest} Object.
	 */
	public NonStickySessionTest()
	{
		super();
	}

	/**
	 * @param creationTime long
	 * @return String
	 */
	private String formatDate(final long creationTime)
	{
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(creationTime);

		return gc.getTime().toString();
	}

	/**
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void service(final HttpServletRequest request, final HttpServletResponse response)
	{
		if (request.getParameter("invalidate") != null)
		{
			request.getSession().invalidate();

			try
			{
				response.getOutputStream().write("<html>\n".getBytes());
				response.getOutputStream().write("Session invalidated\n".getBytes());
				response.getOutputStream().write("</html>".getBytes());
			}
			catch (IOException ex)
			{
				this.logger.error(null, ex);
			}

			return;
		}

		// print request headers
		StringBuilder html = new StringBuilder();
		html.append("<html>").append("\n");
		html.append("<font size=\"2\" face=\"courier\">").append("\n");
		// out +=
		// "Request Headers<br/>"+printHeaders(request)+"<br/><br/>"+printParameters(request)+
		// "<br/>session:"+request.getSession()+"<br/>";
		// out += "</font>";

		// check session ID from request
		String cookieSessionID = request.getHeader("cookie");

		if (cookieSessionID.startsWith(JSESSIONID))
		{
			CloudSession cs = SessionServiceFactory.getService(CACHE_LIVETIME_SECONDS);
			HttpSession session = null;

			if (request.getSession() != null)
			{
				session = request.getSession();

				// TODO SessionSwitch ss = new SessionSwitch(session);

				Long lat = (Long) cs.getSessionValue(cookieSessionID, lastAccessTime);

				if (lat != null)
				{
					html.append("lastAccessTime: [").append(new Date(lat.longValue()))
							.append("]<br/>").append("\n");
					html.append("verbleibende Zeit im cache: [")
							.append((((CACHE_LIVETIME_SECONDS * 1000) + lat.longValue()) - System
									.currentTimeMillis())).append("] millis<br/>").append("\n");
				}
				else
				{
					html.append("lastAccessTime not in cache!!!<br/>").append("\n");
				}

				cs.setSessionValue(cookieSessionID, creationTime,
						Long.valueOf(session.getCreationTime()));
				cs.setSessionValue(cookieSessionID, lastAccessTime,
						Long.valueOf(System.currentTimeMillis()));

				html.append("cookieSessionID:[").append(cookieSessionID)
						.append("]<br/>\ncreationTime:[")
						.append(formatDate(session.getCreationTime())).append("]<br/>")
						.append("\n");
				html.append("current Time:[").append(formatDate(System.currentTimeMillis()))
						.append("]<br/>").append("\n");

				html.append("<br/>request.getSession().getId() : [").append(session.getId())
						.append("]").append("\n");
				html.append("<br/>cookieSessionID              : [").append(cookieSessionID)
						.append("]<br/>").append("\n");

				if (!session.getId().equals(cookieSessionID))
				{
					// check if cookie session has no timeout
					// TODO
					// if no timeout set new cookieSessionID and delete old cookieSessionID
					Long val = (Long) cs.getSessionValue(cookieSessionID, creationTime);

					if (val != null)
					{
						cs.setSessionValue(JSESSIONID + session.getId(), creationTime, val);
						// TODO cs.remove(cookieSessionID);
					}
				}
			}

			String reqUser = request.getParameter(user);

			if (reqUser != null)
			{
				// Train t = new Train();
				// t.setHour(4);
				// t.setItemName("wert");
				// t.setMinute(56);
				// t.setTrainName("trainName");
				// cs.setSessionValue(cookieSessionID, "train", t);
				//
				cs.setSessionValue(cookieSessionID, user, reqUser);
				html.append("set to session").append(cookieSessionID).append(" user:")
						.append(reqUser).append("<br/>").append("\n");
				//
			}
			else
			{
				String csUser = (String) cs.getSessionValue(cookieSessionID, user);
				html.append("get from session").append(cookieSessionID).append(":").append(csUser)
						.append("<br/>").append("\n");
				// Train t = (Train)cs.getSessionValue(cookieSessionID, "train");
			}
		}

		html.append("<br/><a href=\"./Session?invalidate=true\">kill session</a>").append("\n");
		html.append("</html>");

		try
		{
			response.getOutputStream().print(html.toString());
		}
		catch (IOException ex)
		{
			this.logger.error(null, ex);
		}
	}

	// private String printParameters(HttpServletRequest request) {
	// Enumeration<?> names = request.getParameterNames();
	// String res = "";
	// while (names.hasMoreElements()) {
	// String nextName = (String)names.nextElement();
	// res += nextName + ":" + request.getParameter(nextName)+"<br/>";
	// }
	// return res;
	// }
	//
	// private String printHeaders(HttpServletRequest request) {
	// Enumeration<?> names = request.getHeaderNames();
	// String res = "";
	// while (names.hasMoreElements()) {
	// String nextName = (String)names.nextElement();
	// res += nextName + ":[" + request.getHeader(nextName)+"]<br/>";
	// }
	// return res;
	// }
}
