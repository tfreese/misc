package de.freese.nio.sun;

/*
 * @(#)StringContent.java 1.3 05/11/17 Copyright (c) 2006 Sun Microsystems, Inc. All Rights
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * A Content type that provides for transferring Strings.
 * 
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.3, 05/11/17
 */
class StringContent implements Content
{
	/**
	 * 
	 */
	private static Charset ascii = Charset.forName("US-ASCII");

	/**
	 * 
	 */
	private ByteBuffer bb = null;

	/**
	 * 
	 */
	private String content;

	/**
	 * 
	 */
	private String type; // MIME type

	/**
	 * Creates a new {@link StringContent} object.
	 * 
	 * @param c {@link CharSequence}
	 */
	StringContent(final CharSequence c)
	{
		this(c, "text/plain");
	}

	/**
	 * Creates a new {@link StringContent} object.
	 * 
	 * @param c {@link CharSequence}
	 * @param t String
	 */
	StringContent(final CharSequence c, final String t)
	{
		super();

		this.content = c.toString();

		if (!this.content.endsWith("\n"))
		{
			this.content += "\n";
		}

		this.type = t + "; charset=iso-8859-1";
	}

	/**
	 * Creates a new {@link StringContent} object.
	 * 
	 * @param x {@link Exception}
	 */
	StringContent(final Exception x)
	{
		super();

		StringWriter sw = new StringWriter();

		x.printStackTrace(new PrintWriter(sw));
		this.type = "text/plain; charset=iso-8859-1";
		this.content = sw.toString();
	}

	/**
	 * 
	 */
	private void encode()
	{
		if (this.bb == null)
		{
			this.bb = ascii.encode(CharBuffer.wrap(this.content));
		}
	}

	/**
	 * @see de.freese.nio.sun.Content#length()
	 */
	@Override
	public long length()
	{
		encode();

		return this.bb.remaining();
	}

	/**
	 * @see de.freese.nio.sun.Sendable#prepare()
	 */
	@Override
	public void prepare()
	{
		encode();
		this.bb.rewind();
	}

	/**
	 * @see de.freese.nio.sun.Sendable#release()
	 */
	@Override
	public void release() throws IOException
	{
		// Empty
	}

	/**
	 * @see de.freese.nio.sun.Sendable#send(de.freese.nio.sun.ChannelIO)
	 */
	@Override
	public boolean send(final ChannelIO cio) throws IOException
	{
		if (this.bb == null)
		{
			throw new IllegalStateException();
		}

		cio.write(this.bb);

		return this.bb.hasRemaining();
	}

	/**
	 * @see de.freese.nio.sun.Content#type()
	 */
	@Override
	public String type()
	{
		return this.type;
	}
}
