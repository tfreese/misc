package de.freese.nio.sun;

/*
 * @(#)FileContent.java 1.3 05/11/17 Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.channels.FileChannel;

/**
 * A Content type that provides for transferring files.
 * 
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.3, 05/11/17
 */
class FileContent implements Content
{
	/**
	 * 
	 */
	private static File ROOT = new File("root");

	/**
	 * 
	 */
	private FileChannel fc = null;

	/**
	 * 
	 */
	private File fn;

	/**
	 * 
	 */
	private long length = -1;

	/**
	 * 
	 */
	private long position = -1; // NB only; >= 0 if transferring

	/**
	 * 
	 */
	private String type = null;

	/**
	 * Creates a new {@link FileContent} object.
	 * 
	 * @param uri {@link URI}
	 */
	FileContent(final URI uri)
	{
		this.fn = new File(ROOT, uri.getPath().replace('/', File.separatorChar));
	}

	/**
	 * @see de.freese.nio.sun.Content#length()
	 */
	@Override
	public long length()
	{
		return this.length;
	}

	/**
	 * @see de.freese.nio.sun.Sendable#prepare()
	 */
	@Override
	public void prepare() throws IOException
	{
		if (this.fc == null)
		{
			this.fc = new RandomAccessFile(this.fn, "r").getChannel();
		}

		this.length = this.fc.size();
		this.position = 0; // NB only
	}

	/**
	 * @see de.freese.nio.sun.Sendable#release()
	 */
	@Override
	public void release() throws IOException
	{
		if (this.fc != null)
		{
			this.fc.close();
			this.fc = null;
		}
	}

	/**
	 * @see de.freese.nio.sun.Sendable#send(de.freese.nio.sun.ChannelIO)
	 */
	@Override
	public boolean send(final ChannelIO cio) throws IOException
	{
		if (this.fc == null)
		{
			throw new IllegalStateException();
		}

		if (this.position < 0)
		{
			throw new IllegalStateException();
		}

		/*
		 * Short-circuit if we're already done.
		 */
		if (this.position >= this.length)
		{
			return false;
		}

		this.position += cio.transferTo(this.fc, this.position, this.length - this.position);

		return (this.position < this.length);
	}

	/**
	 * @see de.freese.nio.sun.Content#type()
	 */
	@Override
	public String type()
	{
		if (this.type != null)
		{
			return this.type;
		}

		String nm = this.fn.getName();

		if (nm.endsWith(".html"))
		{
			this.type = "text/html; charset=iso-8859-1";
		}
		else if ((nm.indexOf('.') < 0) || nm.endsWith(".txt"))
		{
			this.type = "text/plain; charset=iso-8859-1";
		}
		else
		{
			this.type = "application/octet-stream";
		}

		return this.type;
	}
}
