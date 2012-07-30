/**
 * Created: 29.12.2011
 */

package de.freese.maven.proxy.model;

/**
 * Enthält die Inhalte für ein Maven HTTP Response.
 * 
 * @author Thomas Freese
 */
public class MavenResponse
{
	/**
	 * 
	 */
	private byte[] resource = null;

	/**
	 * 
	 */
	private HTTPHeader httpHeader = null;

	/**
	 * Erstellt ein neues {@link MavenResponse} Object.
	 */
	public MavenResponse()
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
	 * @return byte[]
	 */
	public byte[] getResource()
	{
		return this.resource;
	}

	/**
	 * Liefert die Anzahl Bytes der Daten.
	 * 
	 * @return int
	 */
	public int getResourceLength()
	{
		if (hasResource())
		{
			return getResource().length;
		}

		return 0;
	}

	/**
	 * Liefert den HTTP Content-Type der Resource.
	 * 
	 * @return String
	 */
	public String getResourceType()
	{
		String context = getHttpHeader().getContext();
		String contentType = "text/html";

		if (context.endsWith(".xml"))
		{
			contentType = "application/xml";
		}
		else if (context.endsWith(".jar"))
		{
			contentType = "application/java-archive";
		}

		return contentType;
	}

	/**
	 * Liefert true, wenn die Resource Daten enthält.
	 * 
	 * @return boolean
	 */
	public boolean hasResource()
	{
		if ((this.resource != null) && (this.resource.length > 0))
		{
			return true;
		}

		return false;
	}

	/**
	 * @param httpHeader {@link HTTPHeader}
	 */
	public void setHttpHeader(final HTTPHeader httpHeader)
	{
		this.httpHeader = httpHeader;
	}

	/**
	 * @param resource byte[]
	 */
	public void setResource(final byte[] resource)
	{
		this.resource = resource;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getHttpHeader().getContext();
	}
}
