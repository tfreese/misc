/**
 * Created: 29.12.2011
 */

package de.freese.maven.proxy.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Objects;

/**
 * Enthält die Inhalte für ein Maven HTTP Response.
 *
 * @author Thomas Freese
 */
public class MavenResponse extends AbstractMavenHttpObject
{
    /**
     * Liest die erste Zeile aus dem Reader und erzeugt den Response inklusive Header.<br>
     * Der Reader wird nicht geschlossen.
     *
     * @param reader {@link MavenResponse}
     * @return {@link MavenRequest}
     * @throws IOException Falls was schief geht.
     */
    public static MavenResponse create(final BufferedReader reader) throws IOException
    {
        String line = reader.readLine();

        if ((line == null) || line.isEmpty())
        {
            return null;
        }

        String[] command = line.split(" ", 3);

        String httpProtocol = command[0];
        int httpCode = Integer.parseInt(command[1]);
        String httpMessage = command[2];

        MavenResponse response = new MavenResponse(httpProtocol, httpCode, httpMessage);
        response.readHeader(reader);

        return response;
    }

    /**
     * Erzeugt den Response direkt aus der HttpURLConnection.<br>
     * Bei HEAD-Requests ist der InputStream leer.
     *
     * @param connection {@link HttpURLConnection}
     * @return {@link MavenRequest}
     * @throws IOException Falls was schief geht.
     */
    public static MavenResponse create(final HttpURLConnection connection) throws IOException
    {
        String httpProtocol = connection.getURL().getProtocol();
        int httpCode = connection.getResponseCode();
        String httpMessage = connection.getResponseMessage();

        MavenResponse response = new MavenResponse(httpProtocol, httpCode, httpMessage);

        // Header field 0 is the status line for most HttpURLConnections, but not on GAE
        String headerName = connection.getHeaderFieldKey(0);

        if ((headerName != null) && !headerName.isEmpty())
        {
            response.setHeader(headerName, connection.getHeaderField(0));
        }

        int i = 1;

        while (true)
        {
            headerName = connection.getHeaderFieldKey(i);

            if ((headerName == null) || headerName.isEmpty())
            {
                break;
            }

            response.setHeader(headerName, connection.getHeaderField(i));
            i++;
        }

        return response;
    }

    /**
    *
    */
    private final int httpCode;

    /**
     *
     */
    private final String httpMessage;

    /**
     *
     */
    private byte[] resource = null;

    /**
     * Erstellt ein neues {@link MavenResponse} Object.
     *
     * @param httpProtocol String
     * @param httpCode int
     * @param httpMessage String
     */
    private MavenResponse(final String httpProtocol, final int httpCode, final String httpMessage)
    {
        super();

        setHttpProtocol(Objects.requireNonNull(httpProtocol, "httpProtocol required"));
        this.httpCode = httpCode;
        this.httpMessage = Objects.requireNonNull(httpMessage, "httpMessage required");
    }

    /**
     * @return int
     */
    public int getContentLength()
    {
        String value = getHeaders().get(HEADER_CONTENT_LENGTH);

        return value == null ? 0 : Integer.parseInt(value);
    }

    /**
     * @return int
     */
    public int getHttpCode()
    {
        return this.httpCode;
    }

    /**
     * @return String
     */
    public String getHttpMessage()
    {
        return this.httpMessage;
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
        String context = getHttpUri();
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
     * @param value String
     */
    public void setContentLengthValue(final String value)
    {
        setHeader(HEADER_CONTENT_LENGTH, value);
    }

    /**
     * @see de.freese.maven.proxy.model.AbstractMavenHttpObject#setHttpUri(java.lang.String)
     */
    @Override
    public void setHttpUri(final String httpUri)
    {
        super.setHttpUri(httpUri);
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
        StringBuilder sb = new StringBuilder(1024);

        // HTTP/1.1 200 OK
        sb.append(getHttpProtocol()).append(" ").append(getHttpCode()).append(" ").append(getHttpMessage());
        sb.append(System.lineSeparator());

        // Headers
        getHeaders().forEach((header, value) ->
        {
            sb.append(header).append(": ").append(value);
            sb.append(System.lineSeparator());
        });

        return sb.toString();
    }
}
