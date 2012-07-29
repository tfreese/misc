package router;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class Router implements OutputChannel
{
	/**
	 * @author Thomas Freese
	 */
	private class RouterWorkThread implements Runnable
	{
		/**
		 * 
		 */
		private OutputChannel[] destinations;

		/**
		 * 
		 */
		private Message message;

		/**
		 * 
		 */
		private Thread runner;

		/**
		 * Creates a new RouterWorkThread object.
		 * 
		 * @param newMessage {@link Message}
		 * @param newDestinations {@link OutputChannel}[]
		 */
		private RouterWorkThread(final Message newMessage, final OutputChannel[] newDestinations)
		{
			super();

			this.message = newMessage;
			this.destinations = newDestinations;
			this.runner = new Thread(this);
			this.runner.start();
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			for (OutputChannel destination : this.destinations)
			{
				try
				{
					destination.sendMessage(this.message);
				}
				catch (RemoteException exc)
				{
					System.err.println("Unable to send message to " + destination);
				}
			}
		}
	}

	/**
	 * 
	 */
	private static final String ROUTER_SERVICE_NAME = "router";

	/**
	 * 
	 */
	private Map<InputChannel, OutputChannel[]> links = new HashMap<>();

	/**
	 * Creates a new Router object.
	 */
	public Router()
	{
		super();

		try
		{
			UnicastRemoteObject.exportObject(this);
			Naming.rebind(ROUTER_SERVICE_NAME, this);
		}
		catch (Exception exc)
		{
			System.err.println("Error using RMI to register the Router " + exc);
		}
	}

	/**
	 * @param source {@link InputChannel}
	 * @param destinations {@link OutputChannel}[]
	 */
	public void addRoute(final InputChannel source, final OutputChannel[] destinations)
	{
		this.links.put(source, destinations);
	}

	/**
	 * @see router.OutputChannel#sendMessage(router.Message)
	 */
	@Override
	public synchronized void sendMessage(final Message message)
	{
		Object key = message.getSource();
		OutputChannel[] destinations = this.links.get(key);

		new RouterWorkThread(message, destinations);
	}
}
