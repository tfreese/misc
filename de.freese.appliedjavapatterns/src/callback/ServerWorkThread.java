package callback;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @author Thomas Freese
 */
public class ServerWorkThread implements Runnable
{
	/**
     * 
     */
	private Command command;

	/**
     * 
     */
	private Thread processingThread;

	/**
     * 
     */
	private Socket requestSocket;

	/**
	 * Creates a new {@link ServerWorkThread} object.
	 * 
	 * @param clientRequestSocket {@link Socket}
	 */
	public ServerWorkThread(final Socket clientRequestSocket)
	{
		super();

		this.requestSocket = clientRequestSocket;
		this.processingThread = new Thread(this);
		this.processingThread.start();
	}

	/**
	 * @return {@link Command}
	 */
	public Command getCommand()
	{
		return this.command;
	}

	/**
	 * @return {@link Socket}
	 */
	protected Socket getRequestSocket()
	{
		return this.requestSocket;
	}

	/**
     * 
     */
	protected void processCommand()
	{
		// Empty
	}

	/**
     * 
     */
	private void retrieveCommand()
	{
		try
		{
			ObjectInputStream in = new ObjectInputStream(this.requestSocket.getInputStream());
			Object request = in.readObject();

			this.requestSocket.close();

			if (request instanceof Command)
			{
				this.command = (Command) request;
			}
		}
		catch (ClassNotFoundException exc)
		{
			// Ignore
		}
		catch (IOException exc)
		{
			// Ignore
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		retrieveCommand();
		processCommand();
	}
}
