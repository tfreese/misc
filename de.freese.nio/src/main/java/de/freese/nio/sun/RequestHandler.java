package de.freese.nio.sun;

/*
 * @(#)RequestHandler.java 1.3 05/11/17 Copyright (c) 2006 Sun Microsystems, Inc. All Rights
 * Reserved. Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met: -Redistribution of source code must
 * retain the above copyright notice, this list of conditions and the following disclaimer.
 * -Redistribution in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. Neither the name of Sun Microsystems, Inc. or the names of contributors may be used
 * to endorse or promote products derived from this software without specific prior written
 * permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR
 * IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO
 * EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS
 * OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this software is
 * not designed, licensed or intended for use in the design, construction, operation or maintenance
 * of any nuclear facility.
 */

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * Primary driver class used by non-blocking Servers to receive, prepare, send, and shutdown
 * requests.
 * 
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.3, 05/11/17
 */
class RequestHandler implements Handler
{
	/**
	 * 
	 */
	private static int created = 0;

	/**
	 * 
	 */
	private ChannelIO cio;

	/**
	 * 
	 */
	private ByteBuffer rbb = null;

	/**
	 * 
	 */
	private Reply reply = null;

	/**
	 * 
	 */
	private Request request = null;

	/**
	 * 
	 */
	private boolean requestReceived = false;

	/**
	 * Creates a new {@link RequestHandler} object.
	 * 
	 * @param cio {@link ChannelIO}
	 */
	RequestHandler(final ChannelIO cio)
	{
		this.cio = cio;

		// Simple heartbeat to let user know we're alive.
		synchronized (RequestHandler.class)
		{
			created++;

			if ((created % 50) == 0)
			{
				System.out.println(".");
				created = 0;
			}
			else
			{
				System.out.print(".");
			}
		}
	}

	/**
	 * Ensures that reply field is non-null
	 * 
	 * @throws IOException Falls was schief geht
	 */
	private void build() throws IOException
	{
		Request.Action action = this.request.action();

		if ((action != Request.Action.GET) && (action != Request.Action.HEAD))
		{
			this.reply =
					new Reply(Reply.Code.METHOD_NOT_ALLOWED, new StringContent(
							this.request.toString()));
		}

		this.reply = new Reply(Reply.Code.OK, new FileContent(this.request.uri()), action);
	}

	/**
	 * @see de.freese.nio.sun.Handler#handle(java.nio.channels.SelectionKey)
	 */
	@Override
	public void handle(final SelectionKey sk) throws IOException
	{
		try
		{
			if (this.request == null)
			{
				if (!receive(sk))
				{
					return;
				}

				this.rbb.flip();

				if (parse())
				{
					build();
				}

				try
				{
					this.reply.prepare();
				}
				catch (IOException x)
				{
					this.reply.release();
					this.reply = new Reply(Reply.Code.NOT_FOUND, new StringContent(x));
					this.reply.prepare();
				}

				if (send())
				{
					// More bytes remain to be written
					sk.interestOps(SelectionKey.OP_WRITE);
				}
				else
				{
					// Reply completely written; we're done
					if (this.cio.shutdown())
					{
						this.cio.close();
						this.reply.release();
					}
				}
			}
			else
			{
				if (!send()) // Should be rp.send()
				{
					if (this.cio.shutdown())
					{
						this.cio.close();
						this.reply.release();
					}
				}
			}
		}
		catch (IOException x)
		{
			String m = x.getMessage();

			if (!m.equals("Broken pipe") && !m.equals("Connection reset by peer"))
			{
				System.err.println("RequestHandler: " + x.toString());
			}

			try
			{
				/*
				 * We had a failure here, so we'll try to be nice before closing down and send off a
				 * close_notify, but if we can't get the message off with one try, we'll just
				 * shutdown.
				 */
				this.cio.shutdown();
			}
			catch (IOException e)
			{
				// ignore
			}

			this.cio.close();

			if (this.reply != null)
			{
				this.reply.release();
			}
		}
	}

	/**
	 * When parse is successfull, saves request and returns true
	 * 
	 * @return boolean
	 * @throws IOException Falls was schief geht
	 */
	private boolean parse() throws IOException
	{
		try
		{
			this.request = Request.parse(this.rbb);

			return true;
		}
		catch (MalformedRequestException x)
		{
			this.reply = new Reply(Reply.Code.BAD_REQUEST, new StringContent(x));
		}

		return false;
	}

	/**
	 * Returns true when request is complete May expand rbb if more room required
	 * 
	 * @param sk {@link SelectionKey}
	 * @return boolean
	 * @throws IOException Falls was schief geht
	 */
	private boolean receive(final SelectionKey sk) throws IOException
	{
		// ByteBuffer tmp = null;

		if (this.requestReceived)
		{
			return true;
		}

		if (!this.cio.doHandshake(sk))
		{
			return false;
		}

		if ((this.cio.read() < 0) || Request.isComplete(this.cio.getReadBuf()))
		{
			this.rbb = this.cio.getReadBuf();

			return (this.requestReceived = true);
		}

		return false;
	}

	/**
	 * @return boolean
	 * @throws IOException Falls was schief geht
	 */
	private boolean send() throws IOException
	{
		try
		{
			return this.reply.send(this.cio);
		}
		catch (IOException x)
		{
			if (x.getMessage().startsWith("Resource temporarily"))
			{
				System.err.println("## RTA");

				return true;
			}

			throw x;
		}
	}
}
