// Created: 19.12.2020
package de.freese.jconky.model;

import java.util.Collections;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class NetworkInfos
{
    /**
    *
    */
    private final Map<String, NetworkInfo> interfaces;

    /**
     * Erstellt ein neues {@link NetworkInfos} Object.
     */
    public NetworkInfos()
    {
        this(Collections.emptyMap());
    }

    /**
     * Erstellt ein neues {@link NetworkInfos} Object.
     *
     * @param interfaces {@link Map}
     */
    public NetworkInfos(final Map<String, NetworkInfo> interfaces)
    {
        super();

        this.interfaces = interfaces;
    }

    /**
     * @param ip String
     * @return {@link NetworkInfo}
     */
    public NetworkInfo getByIp(final String ip)
    {
        return this.interfaces.values().stream().filter(ni -> ni.getIp().equals(ip)).findFirst().orElse(null);
    }

    /**
     * @param interfaceName String
     * @return {@link NetworkInfo}
     */
    public NetworkInfo getByName(final String interfaceName)
    {
        return this.interfaces.get(interfaceName);
    }

    /**
     * @return int
     */
    public int size()
    {
        return this.interfaces.size();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("size=").append(size());
        builder.append("]");

        return builder.toString();
    }
}
