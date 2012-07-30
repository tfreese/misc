/*
 * (c) 2004, Nuno Santos, nfsantos@sapo.pt relased under terms of the GNU public license
 * http://www.gnu.org/licenses/licenses.html#TOCGPL
 */
package de.freese.nio.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import de.freese.nio.server.handler.ReadWriteSelectorHandler;

/**
 * Uses non-blocking operations to read and write from a socket. Internally, this class uses a
 * selector to receive read and write events from the underlying socket. Methods on this class
 * should be called only by the selector's thread (including the constructor). If necessary, use
 * Selector.invokeLater() to dispatch a invocation to the selector's thread.
 * 
 * @author Nuno Santos
 */
final public class PacketChannel implements ReadWriteSelectorHandler
{
	/**
	 * The associated selector.
	 */
	protected final SelectorThread selectorThread;

	/** The socket where read and write operations are performed. */
	private final SocketChannel socketChannel;

	/** Used for reading from the socket. */
	private ByteBuffer inBuffer;

	/**
	 * The buffer with the packet currently being sent. This class can only send one packet at a
	 * time, there are no queueing mechanisms.
	 */
	private ByteBuffer outBuffer = null;

	/**
	 *
	 */
	private final int localPort;

	/**
	 * Creates and initializes a new instance. Read interest is enabled by the constructor, so
	 * callers should be ready to star receiving packets.
	 * 
	 * @param socketChannel Socket to be wrapped.
	 * @param selectorThread Selector to be used for managing IO events.
	 * @throws IOException Falls was schief geht.
	 */
	public PacketChannel(final SocketChannel socketChannel, final SelectorThread selectorThread)
		throws IOException
	{

		this.selectorThread = selectorThread;
		this.socketChannel = socketChannel;
		this.localPort = this.socketChannel.socket().getLocalPort();

		// Creates the reading buffer
		// The size is the same as the size of the TCP sockets receive buffer.
		// We will never read more than that at a time.
		this.inBuffer =
				ByteBuffer.allocateDirect(this.socketChannel.socket().getReceiveBufferSize());

		// Quick and dirty hack. When a buffer is created by the first time
		// it is empty, with
		// this.inBuffer.position(this.inBuffer.capacity());

		// inBuffer.flip();

		// Registers with read interest.
		selectorThread.registerChannelInterestNow(this.socketChannel, SelectionKey.OP_READ, this);
	}

	/**
	 * 
	 */
	public void close()
	{
		try
		{
			this.socketChannel.close();
		}
		catch (IOException ex)
		{
			// Ignore
		}
	}

	/**
	 * @see de.freese.nio.server.handler.ReadWriteSelectorHandler#configHandle()
	 */
	@Override
	public void configHandle() throws IOException
	{
		// TODO Auto-generated method stub
	}

	/**
	 * Disable interest in reading.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	public void disableReading() throws IOException
	{
		this.selectorThread.removeChannelInterestNow(this.socketChannel, SelectionKey.OP_READ);
	}

	/**
	 * @see de.freese.nio.server.handler.ReadWriteSelectorHandler#handleRead()
	 */
	@Override
	public void handleRead()
	{
		try
		{
			// Reads from the socket
			// Returns -1 if it has reached end-of-stream
			int readBytes = this.socketChannel.read(this.inBuffer);
			// End of stream???
			if (readBytes == -1)
			{
				// End of stream. Closing channel...
				close();

				System.out.println("[" + this + "] Disconnected.");

				return;
			}

			// Nothing else to be read?
			if (readBytes == 0)
			{
				// There was nothing to read. Shouldn't happen often, but
				// it is not an error, we can deal with it. Ignore this event
				// and reactivate reading.
				reactivateReading();
				return;
			}

			// There is some data in the buffer. Processes it.
			this.inBuffer.flip();
			processInBuffer();
		}
		catch (IOException ex)
		{
			// Serious error. Close socket.
			System.err.println("[" + this + "] Error: " + ex.getMessage());

			close();
		}
	}

	/**
	 * @see de.freese.nio.server.handler.ReadWriteSelectorHandler#handleWrite()
	 */
	@Override
	public void handleWrite()
	{
		try
		{
			// Writes to the socket as much as possible. Since this is a
			// non-blocking operation, we don't know in advance how many
			// bytes will actually be written.
			// int written =
			this.socketChannel.write(this.outBuffer);

			// Check if there are more to be written.
			if (this.outBuffer.hasRemaining())
			{
				// There is. Reactivate interest in writing. We will try again
				// when the socket is ready.
				requestWrite();
			}
			else
			{
				// outBuffer was completly written. Notifies listener
				// ByteBuffer sentPacket = this.outBuffer;
				this.outBuffer = null;

				resumeReading();
			}
		}
		catch (IOException ex)
		{
			System.err.println("[" + this + "] Error: " + ex.getMessage());

			close();
		}
	}

	/**
	 * Processes the internal buffer, converting it into packets if enough data is available.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	private void processInBuffer() throws IOException
	{
		// ByteBuffer packet = this.protocolDecoder.decode(this.inBuffer);
		// A packet may or may not have been fully assembled, depending
		// on the data available in the buffer
		// if (packet == null)
		if (this.inBuffer.hasRemaining())
		{
			// Partial packet received. Must wait for more data. All the contents
			// of inBuffer were processed by the protocol decoder. We can
			// delete it and prepare for more data.
			this.inBuffer.clear();
			reactivateReading();
		}
		else
		{
			// A packet was reassembled.
			sendPacket(this.inBuffer);
			// The inBuffer might still have some data left. Perhaps
			// the beginning of another packet. So don't clear it. Next
			// time reading is activated, we start by processing the inBuffer
			// again.
		}
	}

	/**
	 * Enables interest in reading.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	private void reactivateReading() throws IOException
	{
		this.selectorThread.addChannelInterestNow(this.socketChannel, SelectionKey.OP_READ);
	}

	/**
	 * Activates interest in writing.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	private void requestWrite() throws IOException
	{
		this.selectorThread.addChannelInterestNow(this.socketChannel, SelectionKey.OP_WRITE);
	}

	/**
	 * Activates reading from the socket. This method is non-blocking.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	public void resumeReading() throws IOException
	{
		processInBuffer();
	}

	/**
	 * Sends a packet using non-blocking writes. One packet cannot be sent before the previous one
	 * has been dispatched. The caller must ensure this. This class keeps a reference to buffer
	 * given as argument while sending it. So it is important not to change this buffer after
	 * calling this method.
	 * 
	 * @param packet The packet to be sent.
	 */
	public void sendPacket(final ByteBuffer packet)
	{
		// keeps a reference to the packet. In production code this should copy
		// the contents of the buffer.
		this.outBuffer = packet;
		handleWrite();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "(" + this.localPort + ")";
	}
}