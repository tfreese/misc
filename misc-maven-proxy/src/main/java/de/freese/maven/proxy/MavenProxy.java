// Created: 27.03.2018
package de.freese.maven.proxy;

/**
 * Interface eines MavenProxies.
 *
 * @author Thomas Freese
 */
public interface MavenProxy
{
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