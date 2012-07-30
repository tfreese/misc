package de.freese.nio.sun;

/*
 * @(#)Reply.java 1.3 05/11/17 Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: -Redistribution of source code must retain the
 * above copyright notice, this list of conditions and the following disclaimer. -Redistribution in
 * binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution. Neither
 * the name of Sun Microsystems, Inc. or the names of contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission. This software is
 * provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS
 * BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING
 * OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * An object used for sending Content to the requestor.
 * 
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.3, 05/11/17
 */
class Reply implements Sendable
{
	/**
	 * A helper class which define the HTTP response codes
	 * 
	 * @author Thomas Freese
	 */
	static class Code
	{
		/**
		 * 
		 */
		static Code OK = new Code(200, "OK");

		/**
		 * 
		 */
		static Code BAD_REQUEST = new Code(400, "Bad Request");

		/**
		 * 
		 */
		static Code NOT_FOUND = new Code(404, "Not Found");

		/**
		 * 
		 */
		static Code METHOD_NOT_ALLOWED = new Code(405, "Method Not Allowed");

		/**
		 * 
		 */
		private int number;

		/**
		 * 
		 */
		private String reason;

		/**
		 * Creates a new {@link Code} object.
		 * 
		 * @param i int
		 * @param r String
		 */
		private Code(final int i, final String r)
		{
			this.number = i;
			this.reason = r;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return this.number + " " + this.reason;
		}
	}

	/**
	 * 
	 */
	private static String CRLF = "\r\n";

	/**
	 * 
	 */
	private static Charset ascii = Charset.forName("US-ASCII");

	/**
	 * 
	 */
	private Code code;

	/**
	 * 
	 */
	private Content content;

	/**
	 * 
	 */
	private ByteBuffer hbb = null;

	/**
	 * 
	 */
	private boolean headersOnly;

	/**
	 * Creates a new {@link Reply} object.
	 * 
	 * @param rc {@link Code}
	 * @param c {@link Content}
	 */
	Reply(final Code rc, final Content c)
	{
		this(rc, c, null);
	}

	/**
	 * Creates a new {@link Reply} object.
	 * 
	 * @param rc {@link Code}
	 * @param c {@link Content}
	 * @param head {@link Request}
	 */
	Reply(final Code rc, final Content c, final Request.Action head)
	{
		this.code = rc;
		this.content = c;
		this.headersOnly = (head == Request.Action.HEAD);
	}

	/**
	 * @return {@link ByteBuffer}
	 */
	private ByteBuffer headers()
	{
		CharBuffer cb = CharBuffer.allocate(1024);

		for (;;)
		{
			try
			{
				cb.put("HTTP/1.0 ").put(this.code.toString()).put(CRLF);
				cb.put("Server: niossl/0.1").put(CRLF);
				cb.put("Content-type: ").put(this.content.type()).put(CRLF);
				cb.put("Content-length: ").put(Long.toString(this.content.length())).put(CRLF);
				cb.put(CRLF);

				break;
			}
			catch (BufferOverflowException x)
			{
				assert (cb.capacity() < (1 << 16));
				cb = CharBuffer.allocate(cb.capacity() * 2);

				continue;
			}
		}

		cb.flip();

		return ascii.encode(cb);
	}

	/**
	 * @see de.freese.nio.sun.Sendable#prepare()
	 */
	@Override
	public void prepare() throws IOException
	{
		this.content.prepare();
		this.hbb = headers();
	}

	/**
	 * @see de.freese.nio.sun.Sendable#release()
	 */
	@Override
	public void release() throws IOException
	{
		this.content.release();
	}

	/**
	 * @see de.freese.nio.sun.Sendable#send(de.freese.nio.sun.ChannelIO)
	 */
	@Override
	public boolean send(final ChannelIO cio) throws IOException
	{
		if (this.hbb == null)
		{
			throw new IllegalStateException();
		}

		if (this.hbb.hasRemaining())
		{
			if (cio.write(this.hbb) <= 0)
			{
				return true;
			}
		}

		if (!this.headersOnly)
		{
			if (this.content.send(cio))
			{
				return true;
			}
		}

		if (!cio.dataFlush())
		{
			return true;
		}

		return false;
	}
}
