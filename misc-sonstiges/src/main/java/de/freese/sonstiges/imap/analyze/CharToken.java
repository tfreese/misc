/**
 * Created: 14.01.2014
 */

package de.freese.sonstiges.imap.analyze;

import java.nio.CharBuffer;
import java.util.Arrays;

/**
 * Enthält Methoden für die Verarbeitunge eines char[].<br>
 * Geklaut von <code>org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl</code>.
 *
 * @author Thomas Freese
 */
public class CharToken implements Appendable, CharSequence, Comparable<CharSequence>
{
    /**
     * 
     */
    private char[] buffer;

    /**
     * Cursor auf das letzte Zeichen.
     */
    private int length = 0;

    /**
     * Erstellt ein neues {@link CharToken} Object.
     */
    public CharToken()
    {
        this(10);
    }

    /**
     * Erstellt ein neues {@link CharToken} Object.
     * 
     * @param buffer char[]
     */
    public CharToken(final char[] buffer)
    {
        super();

        this.buffer = buffer;
    }

    /**
     * Erstellt ein neues {@link CharToken} Object.
     * 
     * @param lenght int
     */
    public CharToken(final int lenght)
    {
        this(new char[lenght]);
    }

    /**
     * @see java.lang.Appendable#append(char)
     */
    @Override
    public CharToken append(final char c)
    {
        resizeBuffer(this.length + 1)[this.length++] = c;

        return this;
    }

    /**
     * @see java.lang.Appendable#append(java.lang.CharSequence)
     */
    @Override
    public CharToken append(final CharSequence csq)
    {
        if (csq == null)
        {
            return appendNull();
        }

        return append(csq, 0, csq.length());
    }

    /**
     * @see java.lang.Appendable#append(java.lang.CharSequence, int, int)
     */
    @Override
    public CharToken append(final CharSequence csq, final int start, final int end)
    {
        CharSequence cs = csq;

        if (cs == null)
        {
            cs = "null";
        }

        final int len = end - start;
        final int csqlen = cs.length();

        if ((len < 0) || (start > csqlen) || (end > csqlen))
        {
            throw new IndexOutOfBoundsException();
        }

        if (len == 0)
        {
            return this;
        }

        int begin = start;
        resizeBuffer(this.length + len);

        if (len > 4) // only use instanceof check series for longer CSQs, else simply iterate
        {
            if (cs instanceof String)
            {
                ((String) cs).getChars(start, end, this.buffer, this.length);
            }
            else if (cs instanceof StringBuilder)
            {
                ((StringBuilder) cs).getChars(start, end, this.buffer, this.length);
            }
            else if (cs instanceof CharToken)
            {
                System.arraycopy(((CharToken) cs).buffer, start, this.buffer, this.length, len);
            }
            else if ((cs instanceof CharBuffer) && ((CharBuffer) cs).hasArray())
            {
                final CharBuffer cb = (CharBuffer) cs;
                System.arraycopy(cb.array(), cb.arrayOffset() + cb.position() + start, this.buffer, this.length, len);
            }
            else if (cs instanceof StringBuffer)
            {
                ((StringBuffer) cs).getChars(start, end, this.buffer, this.length);
            }
            else
            {
                while (begin < end)
                {
                    this.buffer[this.length++] = cs.charAt(begin++);
                }

                return this;
            }

            this.length += len;

            return this;
        }

        while (begin < end)
        {
            this.buffer[this.length++] = cs.charAt(begin++);
        }

        return this;
    }

    /**
     * Fügt <code>['n','u','l','l']</code> dem Buffer hinzu.
     * 
     * @return {@link CharToken}
     */
    public CharToken appendNull()
    {
        resizeBuffer(this.length + 4);

        this.buffer[this.length++] = 'n';
        this.buffer[this.length++] = 'u';
        this.buffer[this.length++] = 'l';
        this.buffer[this.length++] = 'l';

        return this;
    }

    /**
     * @return char[]
     */
    public char[] buffer()
    {
        return this.buffer;
    }

    /**
     * @see java.lang.CharSequence#charAt(int)
     */
    @Override
    public char charAt(final int index)
    {
        if (index >= this.length)
        {
            throw new IndexOutOfBoundsException();
        }

        return this.buffer[index];
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final CharSequence o)
    {
        int size = Math.min(length(), o.length());
        int comp = 0;

        for (int i = 0; i < size; i++)
        {
            comp = Character.compare(charAt(i), o.charAt(i));

            if (comp != 0)
            {
                break;
            }
        }

        if (comp == 0)
        {
            comp = length() - o.length();
        }

        return comp;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        CharToken other = (CharToken) obj;

        if (this.length != other.length)
        {
            return false;
        }

        if (!Arrays.equals(this.buffer, other.buffer))
        {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + this.length;
        result = (prime * result) + Arrays.hashCode(this.buffer);

        return result;
    }

    /**
     * @see java.lang.CharSequence#length()
     */
    @Override
    public int length()
    {
        return this.length;
    }

    /**
     * Setzt den Cursor auf das letzte Zeichen, welches kein Leerzeichen ist.
     * 
     * @return {@link CharToken}
     */
    public CharToken resize()
    {
        while ((this.length > 0) && Character.isWhitespace(charAt(this.length - 1)))
        {
            this.length--;
        }

        return this;
    }

    /**
     * @param newSize int
     * @return char[]
     */
    private char[] resizeBuffer(final int newSize)
    {
        if (this.buffer.length < newSize)
        {
            int newLenght = newSize;

            // +50% der alten Länge um unnötiges Kopieren zu vermeiden.
            int oldLength = this.buffer.length;
            newLenght = newSize + (oldLength >> 1);

            final char[] newBuffer = new char[newLenght];
            System.arraycopy(this.buffer, 0, newBuffer, 0, this.buffer.length);

            this.buffer = newBuffer;
        }

        return this.buffer;
    }

    /**
     * @see java.lang.CharSequence#subSequence(int, int)
     */
    @Override
    public CharSequence subSequence(final int start, final int end)
    {
        if ((start > this.length) || (end > this.length))
        {
            throw new IndexOutOfBoundsException();
        }

        return new String(this.buffer, start, end - start);
    }

    /**
     * Wandelt alle Zeichen in Kleinbuchstaben um.
     * 
     * @return {@link CharToken}
     */
    public CharToken toLowerCase()
    {
        for (int i = 0; i < this.length;)
        {
            int codePoint = Character.codePointAt(this.buffer, i, this.length);
            i += Character.toChars(Character.toLowerCase(codePoint), this.buffer, i);
        }

        return this;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new String(this.buffer, 0, this.length);
        // return "CharToken [buffer=" + Arrays.toString(this.buffer) + "]";
    }

    /**
     * Liefert einen String ohne führende und abschliesende Leerzeichen.
     * 
     * @return {@link CharSequence}
     */
    public CharSequence toTrimmedString()
    {
        int start = 0;

        for (start = 0; start < this.length; start++)
        {
            char c = this.buffer[start];

            if (!Character.isWhitespace(c))
            {
                break;
            }
        }

        int end = resize().length();

        if (start > end)
        {
            return "";
        }

        return subSequence(start, end);
    }

    /**
     * Wandelt alle Zeichen in Grossbuchstaben um.
     * 
     * @return {@link CharToken}
     */
    public CharToken toUpperCase()
    {
        for (int i = 0; i < length();)
        {
            int codePoint = Character.codePointAt(this.buffer, i, this.length);
            i += Character.toChars(Character.toUpperCase(codePoint), this.buffer, i);
        }

        return this;
    }
}
