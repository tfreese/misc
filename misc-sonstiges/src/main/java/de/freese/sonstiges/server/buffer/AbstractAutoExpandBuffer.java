// Created: 03.11.2016
package de.freese.sonstiges.server.buffer;

import java.nio.Buffer;
import java.util.Objects;

/**
 * Adapter für den {@link Buffer} mit AutoExpand-Funktion.
 *
 * @author Thomas Freese
 * @param <B> Konkreter Buffer
 */
public abstract class AbstractAutoExpandBuffer<B extends Buffer>
{
    /**
     * @author Thomas Freese
     * @param <T> Konkreter Typ
     */
    public static abstract class AbstractBuilder<T extends AbstractAutoExpandBuffer<?>>
    {
        /**
        *
        */
        private final T autoExpandBuffer;

        /**
         * Erzeugt eine neue Instanz von AbstractBuilder.
         *
         * @param capacity int
         */
        protected AbstractBuilder(final int capacity)
        {
            super();

            this.autoExpandBuffer = createAutoExpandBuffer(capacity);
        }

        /**
         * @return {@link AbstractAutoExpandBuffer}
         */
        public T build()
        {
            return this.autoExpandBuffer;
        }

        /**
         * Erzeugt einen neue {@link AbstractAutoExpandBuffer}.
         *
         * @param capacity int
         * @return {@link AbstractAutoExpandBuffer}
         */
        protected abstract T createAutoExpandBuffer(int capacity);

        /**
         * @return {@link AbstractAutoExpandBuffer}
         */
        protected T getAutoExpandBuffer()
        {
            return this.autoExpandBuffer;
        }
    }

    /**
     * Liefert den nächst größen Wert, der ein Vielfaches von "power of 2" ist.<br>
     * Ist der neue Wert = 0, wird 1024 geliefert.
     *
     * @param requestedCapacity int
     * @return int
     */
    public static int normalizeCapacity(final int requestedCapacity)
    {
        if (requestedCapacity <= 0)
        {
            return 1024;
        }

        int newCapacity = Integer.highestOneBit(requestedCapacity);
        newCapacity <<= (newCapacity < requestedCapacity ? 1 : 0);

        // return newCapacity < 0 ? Integer.MAX_VALUE : newCapacity;
        return newCapacity;
    }

    /**
    *
    */
    private B buffer = null;

    /**
     * Eigene Variable, da kein direkter Zugriff auf Buffer.markValue().
     */
    private int mark = -1;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractAutoExpandBuffer}
     *
     * @param buffer {@link Buffer}
     */
    protected AbstractAutoExpandBuffer(final B buffer)
    {
        super();

        setBuffer(buffer);
    }

    /**
     * Erweitert den Buffer soweit, wenn nötig, um die angegebene Größe aufnehmen zu können.
     *
     * @param expectedRemaining int
     */
    protected void autoExpand(final int expectedRemaining)
    {
        autoExpand(position(), expectedRemaining);
    }

    /**
     * Erweitert den Buffer soweit, wenn nötig, um die angegebene Größe aufnehmen zu können.
     *
     * @param pos int
     * @param expectedRemaining int
     */
    protected void autoExpand(final int pos, final int expectedRemaining)
    {
        // TODO Optimierung
        int end = pos + expectedRemaining;
        int newCapacity = normalizeCapacity(end);

        if (newCapacity > capacity())
        {
            // Buffer muss erweitert werden.
            setBuffer(createNewBuffer(getBuffer(), newCapacity, this.mark));
        }

        if (end > limit())
        {
            // Limit setzen, um StackOverflowError zu vermeiden.
            getBuffer().limit(end);
        }
    }

    /**
     * @see Buffer#capacity()
     * @return int
     */
    public final int capacity()
    {
        return getBuffer().capacity();
    }

    /**
     * @see Buffer#clear()
     * @return {@link AbstractAutoExpandBuffer}
     */
    public final AbstractAutoExpandBuffer<B> clear()
    {
        getBuffer().clear();

        this.mark = -1;

        return this;
    }

    /**
     * @param buffer {@link Buffer}
     * @param newCapacity int
     * @param mark int
     * @return {@link Buffer}
     */
    protected abstract B createNewBuffer(final B buffer, final int newCapacity, final int mark);

    /**
     * @see Buffer#flip()
     * @return {@link AbstractAutoExpandBuffer}
     */
    public final AbstractAutoExpandBuffer<B> flip()
    {
        getBuffer().flip();

        this.mark = -1;

        return this;
    }

    /**
     * @return {@link Buffer}
     */
    public final B getBuffer()
    {
        return this.buffer;
    }

    /**
     * @see Buffer#hasRemaining()
     * @return boolean
     */
    public final boolean hasRemaining()
    {
        return getBuffer().hasRemaining();
    }

    /**
     * @see Buffer#isDirect()
     * @return boolean
     */
    public final boolean isDirect()
    {
        return getBuffer().isDirect();
    }

    /**
     * @see Buffer#isReadOnly()
     * @return boolean
     */
    public final boolean isReadOnly()
    {
        return getBuffer().isReadOnly();
    }

    /**
     * @see Buffer#limit()
     * @return int
     */
    public final int limit()
    {
        return getBuffer().limit();
    }

    /**
     * @see Buffer#limit(int)
     * @param newLimit int
     * @return {@link AbstractAutoExpandBuffer}
     */
    public final AbstractAutoExpandBuffer<B> limit(final int newLimit)
    {
        autoExpand(newLimit, 0);
        getBuffer().limit(newLimit);

        if (this.mark > newLimit)
        {
            this.mark = -1;
        }

        return this;
    }

    /**
     * @see Buffer#mark()
     * @return {@link AbstractAutoExpandBuffer}
     */
    public final AbstractAutoExpandBuffer<B> mark()
    {
        getBuffer().mark();
        this.mark = position();

        return this;
    }

    /**
     * @see Buffer#position()
     * @return int
     */
    public final int position()
    {
        return getBuffer().position();
    }

    /**
     * @see Buffer#position(int)
     * @param newPosition int
     * @return {@link AbstractAutoExpandBuffer}
     */
    public final AbstractAutoExpandBuffer<B> position(final int newPosition)
    {
        autoExpand(newPosition, 0);
        getBuffer().position(newPosition);

        if (this.mark > newPosition)
        {
            this.mark = -1;
        }

        return this;
    }

    /**
     * @see Buffer#remaining()
     * @return int
     */
    public final int remaining()
    {
        return limit() - position();
    }

    /**
     * @see Buffer#reset()
     * @return {@link AbstractAutoExpandBuffer}
     */
    public final AbstractAutoExpandBuffer<B> reset()
    {
        getBuffer().reset();

        return this;
    }

    /**
     * @param buffer {@link Buffer}
     */
    protected void setBuffer(final B buffer)
    {
        this.buffer = Objects.requireNonNull(buffer, "buffer required");
    }

    /**
     * Forwards the position of this buffer as the specified <code>size</code> bytes.
     *
     * @param size int
     * @return {@link AbstractAutoExpandBuffer}
     */
    public final AbstractAutoExpandBuffer<B> skip(final int size)
    {
        autoExpand(size);

        return position(position() + size);
    }
}
