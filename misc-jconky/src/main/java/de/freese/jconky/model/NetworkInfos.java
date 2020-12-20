// Created: 19.12.2020
package de.freese.jconky.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class NetworkInfos
{
    /**
    *
    */
    private static final NetworkInfo DEFAUL_NETWORK_INFO = new NetworkInfo();

    /**
     *
     */
    private final Map<String, NetworkInfo> interfaces;

    /**
     *
     */
    private final NetworkProtocolInfo protocolInfo;

    /**
     * Erstellt ein neues {@link NetworkInfos} Object.
     */
    public NetworkInfos()
    {
        this(new HashMap<>(), new NetworkProtocolInfo());
    }

    /**
     * Erstellt ein neues {@link NetworkInfos} Object.
     *
     * @param interfaces {@link Map}
     * @param protocolInfo {@link NetworkProtocolInfo}
     */
    public NetworkInfos(final Map<String, NetworkInfo> interfaces, final NetworkProtocolInfo protocolInfo)
    {
        super();

        this.interfaces = interfaces;
        this.protocolInfo = protocolInfo;
    }

    /**
     * @param ip String
     * @return {@link NetworkInfo}
     */
    public NetworkInfo getByIp(final String ip)
    {
        return this.interfaces.values().stream().filter(ni -> ni.getIp().equals(ip)).findFirst().orElse(DEFAUL_NETWORK_INFO);
    }

    /**
     * @param interfaceName String
     * @return {@link NetworkInfo}
     */
    public NetworkInfo getByName(final String interfaceName)
    {
        return this.interfaces.computeIfAbsent(interfaceName, key -> DEFAUL_NETWORK_INFO);
    }

    /**
     * @return {@link NetworkProtocolInfo}
     */
    public NetworkProtocolInfo getProtocolInfo()
    {
        return this.protocolInfo;
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
