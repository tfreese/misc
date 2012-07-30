package de.freese.nio.sun;

/*
 * @(#)Dispatcher1.java 1.3 05/11/17 Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
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
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * A single-threaded dispatcher.
 * <P>
 * When a SelectionKey is ready, it dispatches the job in this thread.
 * 
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.3, 05/11/17
 */
class Dispatcher1 implements Dispatcher
{
	/**
	 * 
	 */
	private Selector sel;

	/**
	 * Creates a new {@link Dispatcher1} object.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	Dispatcher1() throws IOException
	{
		this.sel = Selector.open();
	}

	/**
	 * @throws IOException Falls was schief geht
	 */
	private void dispatch() throws IOException
	{
		this.sel.select();

		for (Iterator<SelectionKey> i = this.sel.selectedKeys().iterator(); i.hasNext();)
		{
			SelectionKey sk = i.next();

			i.remove();
			Handler h = (Handler) sk.attachment();

			h.handle(sk);
		}
	}

	/**
	 * @see de.freese.nio.sun.Dispatcher#register(java.nio.channels.SelectableChannel, int,
	 *      de.freese.nio.sun.Handler)
	 */
	@Override
	public void register(final SelectableChannel ch, final int ops, final Handler h)
		throws IOException
	{
		ch.register(this.sel, ops, h);
	}

	/**
	 * Doesn't really need to be runnable
	 */
	@Override
	public void run()
	{
		for (;;)
		{
			try
			{
				dispatch();
			}
			catch (IOException x)
			{
				x.printStackTrace();
			}
		}
	}
}
