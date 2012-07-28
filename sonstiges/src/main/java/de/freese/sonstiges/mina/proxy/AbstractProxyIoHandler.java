/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */
package de.freese.sonstiges.mina.proxy;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class of {@link org.apache.mina.core.service.IoHandler} classes which handle proxied
 * connections.
 * 
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public abstract class AbstractProxyIoHandler extends IoHandlerAdapter
{
	/**
	 * 
	 */
	private static final Charset CHARSET = Charset.forName("iso8859-1");

	/**
	 * 
	 */
	public static final String OTHER_IO_SESSION = AbstractProxyIoHandler.class.getName()
			+ ".OtherIoSession";

	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Erstellt ein neues {@link AbstractProxyIoHandler} Object.
	 */
	public AbstractProxyIoHandler()
	{
		super();
	}

	/**
	 * @return {@link Logger}
	 */
	protected Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(org.apache.mina.core.session.IoSession,
	 *      java.lang.Object)
	 */
	@Override
	public void messageReceived(final IoSession session, final Object message) throws Exception
	{
		IoBuffer rb = (IoBuffer) message;
		IoBuffer wb = IoBuffer.allocate(rb.remaining());
		rb.mark();
		wb.put(rb);
		wb.flip();
		((IoSession) session.getAttribute(OTHER_IO_SESSION)).write(wb);
		rb.reset();

		getLogger().info(rb.getString(CHARSET.newDecoder()));
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionClosed(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionClosed(final IoSession session) throws Exception
	{
		if (session.getAttribute(OTHER_IO_SESSION) != null)
		{
			IoSession sess = (IoSession) session.getAttribute(OTHER_IO_SESSION);
			sess.setAttribute(OTHER_IO_SESSION, null);
			sess.close(false);
			session.setAttribute(OTHER_IO_SESSION, null);
		}
	}

	/**
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionCreated(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionCreated(final IoSession session) throws Exception
	{
		session.suspendRead();
		session.suspendWrite();
	}
}