package de.freese.nio.sun;

/*
 * @(#)N2.java 1.3 05/11/17 Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
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

/**
 * A non-blocking/dual-threaded which performs accept()s in one thread, and services requests in a
 * second. Both threads use select().
 * 
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.3, 05/11/17
 */
public class N2 extends Server
{
	/**
	 * Creates a new {@link N2} object.
	 * 
	 * @param port int
	 * @param backlog int
	 * @param secure boolean
	 * @throws Exception Falls was schief geht.
	 */
	N2(final int port, final int backlog, final boolean secure) throws Exception
	{
		super(port, backlog, secure);
	}

	/**
	 * @see de.freese.nio.sun.Server#runServer()
	 */
	@Override
	void runServer() throws Exception
	{
		Dispatcher d = new DispatcherN();
		Acceptor a = new Acceptor(this.ssc, d, this.sslContext);

		new Thread(a, "Acceptor " + this.ssc.socket().getLocalPort()).start();
		// d.run();
		new Thread(d, "Dispatcher").start();
	}
}