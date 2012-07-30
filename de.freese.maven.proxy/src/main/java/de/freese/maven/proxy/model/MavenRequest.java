/**
 * Created: 29.12.2011
 */

package de.freese.maven.proxy.model;

/**
 * Enthält die Inhalte für ein Maven HTTP Request.
 * 
 * @author Thomas Freese
 */
public class MavenRequest
{
	/**
	 * 
	 */
	private HTTPHeader httpHeader = null;

	/**
	 * Erstellt ein neues {@link MavenRequest} Object.
	 */
	public MavenRequest()
	{
		super();
	}

	/**
	 * @return {@link HTTPHeader}
	 */
	public HTTPHeader getHttpHeader()
	{
		return this.httpHeader;
	}

	/**
	 * @param httpHeader {@link HTTPHeader}
	 */
	public void setHttpHeader(final HTTPHeader httpHeader)
	{
		this.httpHeader = httpHeader;
	}
}
