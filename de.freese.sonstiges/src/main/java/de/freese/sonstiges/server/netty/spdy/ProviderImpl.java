/**
 * Created: 10.11.2012
 */

package de.freese.sonstiges.server.netty.spdy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jetty.npn.NextProtoNego.ServerProvider;

/**
 * {@link ServerProvider} implementation die SPDY2, SPDY3 HTTP1.1 und HTTP1.0 unterstuetzt.
 * 
 * @author Norman Maurer <norman@apache.org>
 * @author Thomas Freese
 */
public class ProviderImpl implements ServerProvider
{
	/**
	 * 
	 */
	private String protocol = null;

	/**
	 * Erstellt ein neues {@link ProviderImpl} Object.
	 */
	public ProviderImpl()
	{
		super();
	}

	/**
	 * @return String
	 */
	public String getSelectedProtocol()
	{
		return this.protocol;
	}

	/**
	 * @see org.eclipse.jetty.npn.NextProtoNego.ServerProvider#protocols()
	 */
	@Override
	public List<String> protocols()
	{
		return Collections.unmodifiableList(Arrays.asList("spdy/2", "spdy/3", "http/1.0",
				"http/1.1"));
	}

	/**
	 * @see org.eclipse.jetty.npn.NextProtoNego.ServerProvider#protocolSelected(java.lang.String)
	 */
	@Override
	public void protocolSelected(final String protocol)
	{
		this.protocol = protocol;
	}

	/**
	 * @see org.eclipse.jetty.npn.NextProtoNego.ServerProvider#unsupported()
	 */
	@Override
	public void unsupported()
	{
		// Als standard protocol HTTP 1.1 verwenden.
		this.protocol = "http/1.1";
	}
}
