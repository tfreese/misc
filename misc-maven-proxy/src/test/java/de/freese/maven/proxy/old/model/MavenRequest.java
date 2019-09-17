/**
 * Created: 29.12.2011
 */

package de.freese.maven.proxy.old.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

/**
 * Enthält die Inhalte für ein Maven HTTP Request.
 *
 * @author Thomas Freese
 */
public class MavenRequest extends AbstractMavenHttpObject
{
    /**
     * Liest die erste Zeile aus dem Reader und erzeugt den Request inklusive Header.<br>
     * Der Reader wird nicht geschlossen.
     *
     * @param reader {@link BufferedReader}
     * @return {@link MavenRequest}
     * @throws IOException Falls was schief geht.
     */
    public static MavenRequest create(final BufferedReader reader) throws IOException
    {
        String line = reader.readLine();

        if ((line == null) || line.isEmpty())
        {
            return null;
        }

        String[] command = line.split(" ", 3);

        String httpMethod = command[0];
        String httpUri = command[1];
        String httpProtocol = command[2];

        MavenRequest request = new MavenRequest(httpMethod, httpUri, httpProtocol);
        request.readHeader(reader);

        return request;
    }

    /**
    *
    */
    private final String httpMethod;

    /**
     * Erstellt ein neues {@link MavenRequest} Object.
     *
     * @param httpMethod String
     * @param httpUri String
     * @param httpProtocol String
     */
    private MavenRequest(final String httpMethod, final String httpUri, final String httpProtocol)
    {
        super();

        this.httpMethod = Objects.requireNonNull(httpMethod, "httpMethod required");
        setHttpUri(Objects.requireNonNull(httpUri, "httpUri required"));
        setHttpProtocol(Objects.requireNonNull(httpProtocol, "httpProtocol required"));
    }

    /**
     * @return String
     */
    public String getHttpMethod()
    {
        return this.httpMethod;
    }

    /**
     * keep-alive, close
     *
     * @param value String
     */
    public void setConnectionValue(final String value)
    {
        setHeader(HEADER_CONNECTION, value);
    }

    /**
     * @param value String
     */
    public void setHostValue(final String value)
    {
        setHeader(HEADER_HOST, value);
    }

    /**
     * @param value String
     */
    public void setUserAgentValue(final String value)
    {
        setHeader(HEADER_USER_AGENT, value);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(1024);

        // GET /artifactory/remote-repos/de/freese/base/0.0.1-SNAPSHOT/maven-metadata.xml HTTP/1.1
        sb.append(getHttpMethod()).append(" ").append(getHttpUri()).append(" ").append(getHttpProtocol());
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
