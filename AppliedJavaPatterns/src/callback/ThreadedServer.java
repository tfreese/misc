package callback;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Thomas Freese
 */
public class ThreadedServer
{
	/**
     * 
     */
	private static final int DEFAULT_SERVER_PORT = 2001;

	/**
     * 
     */
	private int serverPort = DEFAULT_SERVER_PORT;

	/**
     * 
     */
	private boolean shutdown;

	/**
	 * Erstellt ein neues {@link ThreadedServer} Object.
	 */
	private ThreadedServer()
	{
		super();
	}

	/**
	 * @return int
	 */
	public int getServerPort()
	{
		return this.serverPort;
	}

	/**
	 * @return boolean
	 */
	public boolean isShutdown()
	{
		return this.shutdown;
	}

	/**
     * 
     */
	public void runServer()
	{
		try
		{
			ServerSocket mainServer = new ServerSocket(this.serverPort);

			while (!this.shutdown)
			{
				Socket requestSocket = mainServer.accept();

				new ServerWorkThread(requestSocket);
			}
		}
		catch (IOException exc)
		{
			// Ignore
		}
	}

	/**
	 * @param newServerPort int
	 */
	public void setServerPort(final int newServerPort)
	{
		this.serverPort = newServerPort;
	}

	/**
	 * @param isShutdown boolean
	 */
	public void setShutdown(final boolean isShutdown)
	{
		this.shutdown = isShutdown;
	}
}
