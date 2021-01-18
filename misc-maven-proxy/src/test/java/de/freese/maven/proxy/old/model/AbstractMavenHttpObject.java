// Created: 27.03.2018
package de.freese.maven.proxy.old.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisklasse für ein HTTP Request / Response und enthält die Daten des HTTP Headers.<br>
 *
 * <pre>
 *   HTTP/1.1 200 OK
 *   Server: Artifactory/2.4.2
 *   Content-Length: 144
 *   Content-Type: application/xml
 *   Last-Modified: Wed, 28 Dec 2011 10:24:17 GMT
 *   ETag: dec1ee1f8b021c38414e8d05175261c3d826d413
 *   X-Checksum-Sha1: dec1ee1f8b021c38414e8d05175261c3d826d413
 *   X-Checksum-Md5: 0d2e440c64465bcc5cc3177c75776e72
 *
 *   HTTP/1.1 200 OK
 *   Server: Artifactory/2.4.2
 *   Content-Length: 170296
 *   Content-Type: application/java-archive
 *   Last-Modified: Wed, 12 Oct 2011 15:17:59 GMT
 *   ETag: 11619aad3676b9e877b783babe9d4a291c7aea45
 *   X-Checksum-Sha1: 11619aad3676b9e877b783babe9d4a291c7aea45
 *   X-Checksum-Md5: 8e055e568b029fae070b1c0eeaaa810d
 *
 *   HTTP/1.1 404 Maven metadata not found for 'de/freese/base/0.0.1-SNAPSHOT/maven-metadata.xml'.
 *   Server: Artifactory/2.4.2
 *   Content-Type: text/html;charset=ISO-8859-1
 *   Cache-Control: must-revalidate,no-cache,no-store
 *   Content-Length: 1580
 *
 *   GET /artifactory/remote-repos/de/freese/base/0.0.1-SNAPSHOT/maven-metadata.xml HTTP/1.1
 *   Accept-Encoding: gzip
 *   Pragma: no-cache
 *   User-Agent: Apache-Maven/3.0.3 (Java 1.7.0_01; Windows 7 6.1)
 *   Host: localhost:8088
 *   Accept: text/html, image/gif, image/jpeg, *; q=.2, *\/*; q=.2
 *   Connection: keep-alive
 * </pre>
 *
 * @author Thomas Freese
 */
public abstract class AbstractMavenHttpObject
{
    /**
    *
    */
    protected static final String HEADER_CONNECTION = "Connection";

    /**
    *
    */
    protected static final String HEADER_CONTENT_LENGTH = "Content-Length";

    /**
    *
    */
    protected static final String HEADER_HOST = "Host";

    /**
    *
    */
    protected static final String HEADER_SERVER = "Server";

    /**
    *
    */
    protected static final String HEADER_USER_AGENT = "User-Agent";

    /**
    *
    */
    protected final static int HTTP_NOT_FOUND = 404;

    /**
    *
    */
    public final static int HTTP_OK = 200;

    /**
    *
    */
    private final Map<String, String> headers = new LinkedHashMap<>();

    /**
     * HTTP/1.1
     */
    private String httpProtocol;

    /**
    *
    */
    private String httpUri;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erzeugt eine neue Instanz von {@link AbstractMavenHttpObject}.
     */
    public AbstractMavenHttpObject()
    {
        super();
    }

    /**
     * @return {@link Map}
     */
    public Map<String, String> getHeaders()
    {
        return this.headers;
    }

    /**
     * HTTP/1.1
     *
     * @return String
     */
    public String getHttpProtocol()
    {
        return this.httpProtocol;
    }

    /**
     * @return String
     */
    public String getHttpUri()
    {
        return this.httpUri;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liest die Header bis zur ersten leeren Zeile des Readers.<br>
     * Dieser wird nicht geschlossen.
     *
     * @param reader {@link BufferedReader}
     * @throws IOException Falls was schief geht.
     */
    public void readHeader(final BufferedReader reader) throws IOException
    {
        // String line = null;
        //
        // while (((line = reader.readLine()) != null) && !line.isEmpty())
        // {
        // setHeader(line);
        // }
        reader.lines().forEach(this::setHeader);
    }

    /**
     * KEY: VALUE
     *
     * @param headerLine String
     */
    public void setHeader(final String headerLine)
    {
        if ((headerLine == null) || headerLine.isBlank())
        {
            return;
        }

        String[] tokens = headerLine.split(": ");

        setHeader(tokens[0], tokens[1]);
    }

    /**
     * @param headerName String
     * @param headerValue String
     */
    public void setHeader(final String headerName, final String headerValue)
    {
        Objects.requireNonNull(headerName, "headerName required");
        Objects.requireNonNull(headerValue, "headerValue required");

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug(headerName + ": " + headerValue);
        }

        this.headers.put(headerName, headerValue);
    }

    /**
     * HTTP/1.1
     *
     * @param httpProtocol String
     */
    protected void setHttpProtocol(final String httpProtocol)
    {
        this.httpProtocol = httpProtocol;
    }

    /**
     * @param httpUri String
     */
    protected void setHttpUri(final String httpUri)
    {
        this.httpUri = httpUri;
    }

    /**
     * @param value String
     */
    public void setServerValue(final String value)
    {
        setHeader(HEADER_SERVER, value);
    }
}
