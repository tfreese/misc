// Created: 27.03.2018
package de.freese.maven.proxy;

import java.nio.charset.Charset;

/**
 * Interface eines MavenProxies.
 *
 * @author Thomas Freese
 */
public interface MavenProxy
{
    /**
     * Zeichensatz für die Codierung.<br>
     * Default: ISO-8859-1
     *
     * @param charset {@link Charset}
     */
    public void setCharset(final Charset charset);

    /**
     * Setzt den Port.<br>
     * Default: 8080
     *
     * @param port int
     */
    public void setPort(int port);

    /**
     * Beenden des Proxies.
     */
    public void shutdown();

    /**
     * Startet den Proxy NACH dem Setzen aller notwendigen Parameter.
     */
    public void start();
}