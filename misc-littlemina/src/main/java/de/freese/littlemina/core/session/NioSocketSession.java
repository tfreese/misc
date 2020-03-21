// Created: 10.01.2010
/**
 * 10.01.2010
 */
package de.freese.littlemina.core.session;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import de.freese.littlemina.core.IoHandler;
import de.freese.littlemina.core.buffer.AbstractIoBuffer;
import de.freese.littlemina.core.processor.IoProcessor;

/**
 * Basisimplementierung eines {@link IoSession}s.<br>
 *
 * @author Thomas Freese
 */
public class NioSocketSession implements IoSession
{
    /**
     *
     */
    private final ConcurrentMap<Object, Object> attributes = new ConcurrentHashMap<>();

    /**
     *
     */
    private AbstractIoBuffer buffer = null;

    /**
     *
     */
    private final SocketChannel channel;

    /**
     *
     */
    private IoHandler handler = null;

    /**
     *
     */
    private SelectionKey key;

    /**
     *
     */
    private boolean markedForClosing = false;

    /**
     *
     */
    private final IoProcessor<NioSocketSession> processor;

    /**
     * Erstellt ein neues {@link NioSocketSession} Object.
     *
     * @param processor {@link IoProcessor}
     * @param handler {@link IoHandler}
     * @param channel {@link SocketChannel}
     */
    public NioSocketSession(final IoProcessor<NioSocketSession> processor, final IoHandler handler, final SocketChannel channel)
    {
        super();

        this.processor = processor;
        this.handler = handler;
        this.channel = channel;
    }

    /**
     * @see de.freese.littlemina.core.session.IoSession#close()
     */
    @Override
    public void close()
    {
        this.markedForClosing = true;
        // getProcessor().scheduleRemove(this);
    }

    /**
     * @see de.freese.littlemina.core.session.IoSession#getAttribute(java.lang.Object)
     */
    @Override
    public Object getAttribute(final Object key)
    {
        return this.attributes.get(key);
    }

    /**
     * @see de.freese.littlemina.core.session.IoSession#getBuffer()
     */
    @Override
    public AbstractIoBuffer getBuffer()
    {
        // if (this.byteBuffer == null)
        // {
        // this.byteBuffer = ByteBuffer.allocate(1024);
        // }

        return this.buffer;
    }

    /**
     * @see de.freese.littlemina.core.session.IoSession#getChannel()
     */
    @Override
    public SocketChannel getChannel()
    {
        return this.channel;
    }

    /**
     * @see de.freese.littlemina.core.session.IoSession#getHandler()
     */
    @Override
    public IoHandler getHandler()
    {
        return this.handler;
    }

    /**
     * @return {@link IoProcessor}<NioSocketSession>
     */
    public IoProcessor<NioSocketSession> getProcessor()
    {
        return this.processor;
    }

    /**
     * @see de.freese.littlemina.core.session.IoSession#getSelectionKey()
     */
    @Override
    public SelectionKey getSelectionKey()
    {
        return this.key;
    }

    /**
     * @return boolean
     */
    public boolean isMarkedForClosing()
    {
        return this.markedForClosing;
    }

    /**
     * @see de.freese.littlemina.core.session.IoSession#setAttribute(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setAttribute(final Object key, final Object value)
    {
        this.attributes.put(key, value);
    }

    /**
     * @see de.freese.littlemina.core.session.IoSession#setBuffer(de.freese.littlemina.core.buffer.AbstractIoBuffer)
     */
    @Override
    public void setBuffer(final AbstractIoBuffer buffer)
    {
        this.buffer = buffer;
    }

    /**
     * @see de.freese.littlemina.core.session.IoSession#setSelectionKey(java.nio.channels.SelectionKey)
     */
    @Override
    public void setSelectionKey(final SelectionKey key)
    {
        this.key = key;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("resource")
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("@");
        sb.append(Integer.toHexString(hashCode()));
        sb.append(": ");
        sb.append(getChannel().socket().getLocalSocketAddress());
        sb.append(getChannel().socket().getRemoteSocketAddress());

        return sb.toString();
    }

    /**
     * @see de.freese.littlemina.core.session.IoSession#write(de.freese.littlemina.core.buffer.AbstractIoBuffer)
     */
    @Override
    public void write(final AbstractIoBuffer buffer) throws Exception
    {
        setBuffer(buffer);
        getProcessor().scheduleWrite(this);
    }
}
