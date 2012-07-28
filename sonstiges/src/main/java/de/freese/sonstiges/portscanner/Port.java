package de.freese.sonstiges.portscanner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scannt einen Port eines Hosts.
 * 
 * @author Thomas Freese
 */
public class Port implements Runnable
{
	/**
	 * 
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Port.class);

	/**
	 *
	 */
	private final InetAddress host;

	/**
	 * 
	 */
	private final Map<Integer, Port> openPorts;

	/**
	 *
	 */
	private final int port;

	/**
	 * Creates a new {@link Port} object.
	 * 
	 * @param openPorts {@link Map}
	 * @param host {@link InetAddress}
	 * @param port int
	 */
	public Port(final Map<Integer, Port> openPorts, final InetAddress host, final int port)
	{
		super();

		this.openPorts = openPorts;
		this.host = host;
		this.port = port;
	}

	/**
	 * Scannt den Port.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		Socket socket = null;

		try
		{
			LOGGER.info("Scan Port {}:{}", this.host, Integer.valueOf(this.port));

			socket = new Socket(this.host, this.port);

			this.openPorts.put(Integer.valueOf(this.port), this);
		}
		catch (IOException ex)
		{
			// NOOP
			// LOGGER.warn(ex.getMessage());
		}
		finally
		{
			if (socket != null)
			{
				try
				{
					socket.close();
				}
				catch (Exception ex)
				{
					// NOOP
				}
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.format("%s:%d", this.host, Integer.valueOf(this.port));
	}
}
