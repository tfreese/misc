/**
 * Created: 24.03.2012
 */

package de.freese.vfs;

import java.io.IOException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;

/**
 * @author Thomas Freese
 */
public class MainWebDav
{
	/**
	 * @param args String[]
	 * @throws IOException Falls was schief geht.
	 * @throws DavException Falls was schief geht.
	 */
	public static void main(final String[] args) throws IOException, DavException
	{
		// Config
		HostConfiguration hostConfig = new HostConfiguration();
		hostConfig.setHost("https://sd2dav.1und1.de");

		HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();

		int maxHostConnections = 20;
		params.setMaxConnectionsPerHost(hostConfig, maxHostConnections);
		connectionManager.setParams(params);

		HttpClient client = new HttpClient(connectionManager);
		Credentials creds = new UsernamePasswordCredentials("...", "...");
		client.getState().setCredentials(AuthScope.ANY, creds);
		client.setHostConfiguration(hostConfig);

		// List
		String host = "https://sd2dav.1und1.de";
		String resourcePath = "/maven";
		DavMethod pFind =
				new PropFindMethod(host + resourcePath, DavConstants.PROPFIND_ALL_PROP,
						DavConstants.DEPTH_1);
		client.executeMethod(pFind);

		MultiStatus multiStatus = pFind.getResponseBodyAsMultiStatus();
		MultiStatusResponse[] responses = multiStatus.getResponses();
		MultiStatusResponse currResponse;
		// List files = new ArrayList();
		System.out.println("Folders and files in " + resourcePath + ":");

		for (MultiStatusResponse response : responses)
		{
			currResponse = response;

			if (!(currResponse.getHref().equals(resourcePath) || currResponse.getHref().equals(
					resourcePath + "/")))
			{
				System.out.println("\tChild: " + currResponse.getHref());
			}
		}

		// Close
	}
}
