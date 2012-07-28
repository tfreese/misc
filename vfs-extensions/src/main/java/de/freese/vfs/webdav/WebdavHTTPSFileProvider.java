/**
 * Created: 24.03.2012
 */

package de.freese.vfs.webdav;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticationData;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.provider.FileProvider;
import org.apache.commons.vfs2.provider.GenericFileName;
import org.apache.commons.vfs2.util.UserAuthenticatorUtils;

/**
 * {@link FileProvider} mit Unterstützung für HTTPS Verbindungen.
 * 
 * @author Thomas Freese
 */
public class WebdavHTTPSFileProvider extends AbstractOriginatingFileProvider
{
	/**
	 * 
	 */
	private static final UserAuthenticationData.Type[] AUTHENTICATOR_TYPES =
			new UserAuthenticationData.Type[]
			{
					UserAuthenticationData.USERNAME, UserAuthenticationData.PASSWORD
			};

	/**
	 * 
	 */
	static final Collection<Capability> CAPABILITIES = Collections.unmodifiableCollection(Arrays
			.asList(new Capability[]
			{
					Capability.CREATE,
					Capability.DELETE,
					Capability.RENAME,
					Capability.GET_TYPE,
					Capability.LIST_CHILDREN,
					Capability.READ_CONTENT,
					Capability.URI,
					Capability.WRITE_CONTENT,
					Capability.GET_LAST_MODIFIED,
					Capability.ATTRIBUTES,
					Capability.RANDOM_ACCESS_READ,
					Capability.DIRECTORY_READ_CONTENT,
			}));

	/**
	 * Erstellt ein neues {@link WebdavHTTPSFileProvider} Object.
	 */
	public WebdavHTTPSFileProvider()
	{
		super();

		setFileNameParser(new WebdavHTTPSFileNameParser());
	}

	/**
	 * @see org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider#doCreateFileSystem(org.apache.commons.vfs2.FileName,
	 *      org.apache.commons.vfs2.FileSystemOptions)
	 */
	@Override
	protected FileSystem doCreateFileSystem(final FileName rootName,
											final FileSystemOptions fileSystemOptions)
		throws FileSystemException
	{

		final GenericFileName name = (GenericFileName) rootName;
		FileSystemOptions fsOpts =
				(fileSystemOptions == null) ? new FileSystemOptions() : fileSystemOptions;

		UserAuthenticationData authData = null;
		HttpClient httpClient = null;

		try
		{
			HostConfiguration hostConfig = new HostConfiguration();
			hostConfig.setHost(name.getHostName(), name.getPort());

			HttpConnectionManagerParams params = new HttpConnectionManagerParams();
			int maxHostConnections = getConfigBuilder().getMaxConnectionsPerHost(fileSystemOptions);
			params.setMaxConnectionsPerHost(hostConfig, maxHostConnections);
			int maxTotalConnections = getConfigBuilder().getMaxTotalConnections(fileSystemOptions);
			params.setMaxTotalConnections(maxTotalConnections);

			HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
			connectionManager.setParams(params);

			httpClient = new HttpClient(connectionManager);
			httpClient.setHostConfiguration(hostConfig);

			authData = UserAuthenticatorUtils.authenticate(fsOpts, AUTHENTICATOR_TYPES);

			String username =
					UserAuthenticatorUtils.toString(UserAuthenticatorUtils.getData(authData,
							UserAuthenticationData.USERNAME,
							UserAuthenticatorUtils.toChar(name.getUserName())));
			String password =
					UserAuthenticatorUtils.toString(UserAuthenticatorUtils.getData(authData,
							UserAuthenticationData.PASSWORD,
							UserAuthenticatorUtils.toChar(name.getPassword())));

			if (username != null)
			{
				Credentials creds = new UsernamePasswordCredentials(username, password);
				httpClient.getState().setCredentials(AuthScope.ANY, creds);
			}

			Cookie[] cookies = getConfigBuilder().getCookies(fileSystemOptions);

			if (cookies != null)
			{
				httpClient.getState().addCookies(cookies);
			}

			//
			// if (fileSystemOptions != null)
			// {
			// String proxyHost = getConfigBuilder().getProxyHost(fileSystemOptions);
			// int proxyPort = getConfigBuilder().getProxyPort(fileSystemOptions);
			//
			// if ((proxyHost != null) && (proxyHost.length() > 0) && (proxyPort > 0))
			// {
			// config.setProxy(proxyHost, proxyPort);
			// }
			//
			// UserAuthenticator proxyAuth =
			// getConfigBuilder().getProxyAuthenticator(fileSystemOptions);
			//
			// if (proxyAuth != null)
			// {
			// UserAuthenticationData authDataProxy =
			// UserAuthenticatorUtils.authenticate(proxyAuth,
			//
			// new UserAuthenticationData.Type[]
			// {
			// UserAuthenticationData.USERNAME,
			// UserAuthenticationData.PASSWORD
			// });
			//
			// if (authDataProxy != null)
			// {
			// final UsernamePasswordCredentials proxyCreds =
			// new UsernamePasswordCredentials(
			// UserAuthenticatorUtils.toString(UserAuthenticatorUtils
			// .getData(authDataProxy,
			// UserAuthenticationData.USERNAME, null)),
			// UserAuthenticatorUtils.toString(UserAuthenticatorUtils
			// .getData(authDataProxy,
			// UserAuthenticationData.PASSWORD, null)));
			//
			// AuthScope scope = new AuthScope(proxyHost, AuthScope.ANY_PORT);
			// httpClient.getState().setProxyCredentials(scope, proxyCreds);
			// }
			//
			// if (getConfigBuilder().isPreemptiveAuth(fileSystemOptions))
			// {
			// HttpClientParams httpClientParams = new HttpClientParams();
			// httpClientParams.setAuthenticationPreemptive(true);
			// httpClient.setParams(httpClientParams);
			// }
			// }

			// httpClient.executeMethod(new HeadMethod());
		}
		catch (final Exception ex)
		{
			throw new FileSystemException("vfs.provider.http/connect.error", new Object[]
			{
				name.getHostName()
			}, ex);
		}
		finally
		{
			UserAuthenticatorUtils.cleanup(authData);
		}

		return new WebdavHTTPSFileSystem(name, httpClient, fsOpts);
	}

	/**
	 * @see org.apache.commons.vfs2.provider.FileProvider#getCapabilities()
	 */
	@Override
	public Collection<Capability> getCapabilities()
	{
		return CAPABILITIES;
	}

	/**
	 * @see org.apache.commons.vfs2.provider.AbstractFileProvider#getConfigBuilder()
	 */
	@Override
	public WebdavHTTPSFileSystemConfigBuilder getConfigBuilder()
	{
		return WebdavHTTPSFileSystemConfigBuilder.getInstance();
	}
}
