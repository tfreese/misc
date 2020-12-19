// Created: 19.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class NetworkInfo
{
    /**
     *
     */
    private final long bytesReceived;

    /**
     *
     */
    private final long bytesTransmitted;

    /**
     *
     */
    private final String interfaceName;

    /**
     *
     */
    private final String ip;

    /**
     * Erstellt ein neues {@link NetworkInfo} Object.
     *
     * @param interfaceName String
     * @param ip String
     * @param bytesReceived long
     * @param bytesTransmitted long
     */
    public NetworkInfo(final String interfaceName, final String ip, final long bytesReceived, final long bytesTransmitted)
    {
        super();

        this.interfaceName = interfaceName;
        this.ip = ip;
        this.bytesReceived = bytesReceived;
        this.bytesTransmitted = bytesTransmitted;
    }

    /**
     * @return long
     */
    public long getBytesReceived()
    {
        return this.bytesReceived;
    }

    /**
     * @return long
     */
    public long getBytesTransmitted()
    {
        return this.bytesTransmitted;
    }

    /**
     * @return String
     */
    public String getInterfaceName()
    {
        return this.interfaceName;
    }

    /**
     * @return String
     */
    public String getIp()
    {
        return this.ip;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("interfaceName=").append(this.interfaceName);
        builder.append(", ip=").append(this.ip);
        builder.append(", bytesTransmitted=").append(this.bytesTransmitted);
        builder.append(", bytesReceived=").append(this.bytesReceived);
        builder.append("]");

        return builder.toString();
    }
}
