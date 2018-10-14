// Created: 03.11.2016
package de.freese.sonstiges.server;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;

/**
 * Adapter für den {@link CharBuffer} mit AutoExpand-Funktion.<br>
 * Der carriage return line feed (crlf) wird automatisch bei jeder put-Methode angefügt.
 *
 * @author Thomas Freese
 */
public final class AutoExpandCharBuffer extends AbstractAutoExpandBuffer<CharBuffer>
{
    /**
     * @author Thomas Freese
     */
    public static class Builder extends AbstractBuilder<AutoExpandCharBuffer>
    {
        /**
         * Erzeugt eine neue Instanz von {@link Builder}
         *
         * @param capacity inr
         */
        private Builder(final int capacity)
        {
            super(capacity);
        }

        /**
         * @see de.freese.sonstiges.server.AbstractAutoExpandBuffer.AbstractBuilder#createAutoExpandBuffer(int)
         */
        @Override
        protected AutoExpandCharBuffer createAutoExpandBuffer(final int capacity)
        {
            CharBuffer charBuffer = CharBuffer.allocate(capacity);

            return new AutoExpandCharBuffer(charBuffer);
        }

        /**
         * carriage return line feed (NETASCII_EOL)<br>
         * Default: "\r\n"
         *
         * @param crlf String
         * @return {@link Builder}
         */
        public Builder crlf(final String crlf)
        {
            getAutoExpandBuffer().crlf = crlf;

            return this;
        }
    }

    /**
    *
    */
    private static final String CRLF = "\r\n";

    /**
     * Erzeugt einen neuen Builder.
     *
     * @param capacity int
     * @return {@link Builder}
     */
    public static Builder builder(final int capacity)
    {
        return new Builder(capacity);
    }

    /**
     *
     */
    private String crlf = CRLF;

    /**
     * Erzeugt eine neue Instanz von {@link AutoExpandCharBuffer}
     *
     * @param buffer {@link CharBuffer}
     */
    private AutoExpandCharBuffer(final CharBuffer buffer)
    {
        super(buffer);
    }

    /**
     * Fügt CRLF an, wenn dieser != null.
     */
    private void appendCRLF()
    {
        if (getCRLF() != null)
        {
            autoExpand(getCRLF().length());
            getBuffer().put(getCRLF());
        }
    }

    /**
     * @see de.freese.sonstiges.server.AbstractAutoExpandBuffer#createNewBuffer(java.nio.Buffer, int, int)
     */
    @Override
    protected CharBuffer createNewBuffer(final CharBuffer buffer, final int newCapacity, final int mark)
    {
        if (newCapacity > buffer.capacity())
        {
            // Alten Zustand speichern.
            int pos = buffer.position();

            // // Reallocate.
            CharBuffer newBuffer = CharBuffer.allocate(newCapacity);

            buffer.flip();
            newBuffer.put(buffer);

            // Alten Zustand wiederherstellen.
            newBuffer.limit(newCapacity);

            if (mark >= 0)
            {
                newBuffer.position(mark);
                newBuffer.mark();
            }

            newBuffer.position(pos);

            return newBuffer;
        }

        return buffer;
    }

    /**
     * @param encoder {@link CharsetEncoder}
     * @return {@link ByteBuffer}
     * @throws CharacterCodingException Falls was schief geht.
     */
    public ByteBuffer encode(final CharsetEncoder encoder) throws CharacterCodingException
    {
        return encoder.reset().encode(getBuffer());
    }

    /**
     * carriage return line feed (NETASCII_EOL)
     *
     * @return String
     */
    private String getCRLF()
    {
        return this.crlf;
    }

    /**
     * @see CharBuffer#put(String)
     * @param src {@link CharSequence}
     * @return {@link AutoExpandCharBuffer}
     */
    public AutoExpandCharBuffer put(final CharSequence src)
    {
        return put(src, 0, src.length());
    }

    /**
     * @see CharBuffer#put(String, int, int)
     * @param src {@link CharSequence}
     * @param start int
     * @param end int
     * @return {@link AutoExpandCharBuffer}
     */
    public AutoExpandCharBuffer put(final CharSequence src, final int start, final int end)
    {
        autoExpand(end - start);

        getBuffer().put(src.toString(), start, end);

        appendCRLF();

        return this;
    }

    /**
     * @see String#format(String, Object...)
     * @param format String
     * @param args Object[]
     * @return {@link AutoExpandCharBuffer}
     */
    public AutoExpandCharBuffer putf(final String format, final Object...args)
    {
        String s = String.format(format, args);

        return put(s, 0, s.length());
    }

    /**
     * Fügt eine Leerzeile hinzu.<br>
     * Default: "\r\n"
     *
     * @return {@link AutoExpandCharBuffer}
     */
    public AutoExpandCharBuffer putln()
    {
        appendCRLF();

        return this;
    }
}
