/*
 * (c) 2004, Nuno Santos, nfsantos@sapo.pt relased under terms of the GNU public license
 * http://www.gnu.org/licenses/licenses.html#TOCGPL
 */
package de.freese.nio.beispiele.nuno.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import de.freese.nio.beispiele.nuno.io.SelectorThread;

/**
 * A simple server for demonstrating the IO Multiplexing framework in action. After accepting a
 * connection, it will read packets as defined by the SimpleProtocolDecoder class and echo them
 * back. This server can accept and manage large numbers of incoming connections. For added fun
 * remove the System.out statements and try it with several thousand (>10.000) clients. You might
 * have to increase the maximum number of sockets allowed by the operating system.
 * 
 * @author Nuno Santos
 */
public class Server implements AcceptorListener, PacketChannelListener
{
	/**
	 * 
	 */
	private final SelectorThread st;

	/**
	 * Starts the server.
	 * 
	 * @param listenPort The port where to listen for incoming connections.
	 * @throws Exception Falls was schief geht.
	 */
	public Server(final int listenPort) throws Exception
	{
		super();

		this.st = new SelectorThread();
		Acceptor acceptor = new Acceptor(listenPort, this.st, this);
		acceptor.openServerSocket();
		System.out.println("Listening on port: " + listenPort);
	}

	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		int listenPort = Integer.parseInt(args[0]);
		new Server(listenPort);
	}

	// ////////////////////////////////////////
	// Implementation of the callbacks from the
	// Acceptor and PacketChannel classes
	// ////////////////////////////////////////
	/**
	 * A new client connected. Creates a PacketChannel to handle it.
	 * 
	 * @param acceptor {@link Acceptor}
	 * @param sc {@link SocketChannel}
	 */
	@Override
	public void socketConnected(final Acceptor acceptor, final SocketChannel sc)
	{
		System.out.println("[" + acceptor + "] Socket connected: " + sc.socket().getInetAddress());
		try
		{
			// We should reduce the size of the TCP buffers or else we will
			// easily run out of memory when accepting several thousands of
			// connctions
			sc.socket().setReceiveBufferSize(2 * 1024);
			sc.socket().setSendBufferSize(2 * 1024);
			// The contructor enables reading automatically.
			PacketChannel pc = new PacketChannel(sc, this.st, new SimpleProtocolDecoder(), this);
			pc.resumeReading();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @see de.freese.nio.beispiele.nuno.handlers.AcceptorListener#socketError(de.freese.nio.beispiele.nuno.handlers.Acceptor,
	 *      java.lang.Exception)
	 */
	@Override
	public void socketError(final Acceptor acceptor, final Exception ex)
	{
		System.out.println("[" + acceptor + "] Error: " + ex.getMessage());
	}

	/**
	 * @see de.freese.nio.beispiele.nuno.handlers.PacketChannelListener#packetArrived(de.freese.nio.beispiele.nuno.handlers.PacketChannel,
	 *      java.nio.ByteBuffer)
	 */
	@Override
	public void packetArrived(final PacketChannel pc, final ByteBuffer pckt)
	{
		// System.out.println("[" + pc.toString() + "] Packet received. Size: " + pckt.remaining());
		pc.sendPacket(pckt);
	}

	/**
	 * @see de.freese.nio.beispiele.nuno.handlers.PacketChannelListener#socketException(de.freese.nio.beispiele.nuno.handlers.PacketChannel,
	 *      java.lang.Exception)
	 */
	@Override
	public void socketException(final PacketChannel pc, final Exception ex)
	{
		System.out.println("[" + pc.toString() + "] Error: " + ex.getMessage());
	}

	/**
	 * @see de.freese.nio.beispiele.nuno.handlers.PacketChannelListener#socketDisconnected(de.freese.nio.beispiele.nuno.handlers.PacketChannel)
	 */
	@Override
	public void socketDisconnected(final PacketChannel pc)
	{
		System.out.println("[" + pc.toString() + "] Disconnected.");
	}

	/**
	 * The answer to a request was sent. Prepare to read the next request.
	 * 
	 * @param pc {@link PacketChannel}
	 * @param pckt {@link ByteBuffer}
	 */
	@Override
	public void packetSent(final PacketChannel pc, final ByteBuffer pckt)
	{
		try
		{
			pc.resumeReading();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
