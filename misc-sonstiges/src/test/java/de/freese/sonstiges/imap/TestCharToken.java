/**
 * Created: 12.01.2014
 */

package de.freese.sonstiges.imap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import de.freese.sonstiges.imap.analyze.CharToken;

/**
 * @author Thomas Freese
 */
class TestCharToken
{
    /**
     *
     */
    @Test
    void testAppendChar()
    {
        CharToken charToken = new CharToken().append(' ').append('a').append(' ');

        assertEquals(3, charToken.length());
        assertEquals(" a ", charToken.toString());
    }

    /**
     *
     */
    @Test
    void testAppendCharSequence()
    {
        CharSequence charToken = new CharToken().append(" a ");

        assertEquals(3, charToken.length());
        assertEquals(" a ", charToken.toString());
    }

    /**
     *
     */
    @Test
    void testAppendNull()
    {
        CharToken charToken = new CharToken().appendNull();

        assertEquals(4, charToken.length());
        assertEquals("null", charToken.toString());
    }

    /**
     *
     */
    @Test
    void testCompareTo()
    {
        CharToken charToken1 = new CharToken().appendNull();
        CharToken charToken2 = new CharToken().appendNull();
        assertEquals(0, charToken1.compareTo(charToken2));
        assertEquals(0, charToken1.compareTo("null"));
        assertEquals(0, charToken2.compareTo("null"));

        charToken1 = new CharToken().append('a');
        charToken2 = new CharToken().append('b');
        assertEquals(0, charToken1.compareTo("a"));
        assertEquals(0, charToken2.compareTo("b"));
        assertTrue(charToken1.compareTo(charToken2) < 0);
        assertTrue(charToken2.compareTo(charToken1) > 0);

        charToken1 = new CharToken().append(" a");
        charToken2 = new CharToken().append('b');
        assertEquals(0, charToken1.compareTo(" a"));
        assertEquals(0, charToken2.compareTo("b"));
        assertTrue(charToken1.compareTo(charToken2) < 0);
        assertTrue(charToken2.compareTo(charToken1) > 0);

        charToken1 = new CharToken().append("a");
        charToken2 = new CharToken().append("a ");
        assertEquals(0, charToken1.compareTo("a"));
        assertEquals(0, charToken2.compareTo("a "));
        assertTrue(charToken1.compareTo(charToken2) < 0);
        assertTrue(charToken2.compareTo(charToken1) > 0);
    }

    /**
     *
     */
    @Test
    void testForceResizeBuffer()
    {
        CharToken charToken = new CharToken();

        charToken.append("0123456789");
        assertEquals(10, charToken.length());

        charToken.append("0123456789");
        assertEquals(20, charToken.length());

        assertEquals("01234567890123456789", charToken.toString());
    }

    /**
    *
    */
    @Test
    void testResize()
    {
        CharToken charToken = new CharToken();

        charToken.append("01234     ");
        assertEquals(10, charToken.length());
        assertEquals("01234     ", charToken.toString());

        charToken.resize();
        assertEquals(5, charToken.length());
        assertEquals("01234", charToken.toString());
    }

    /**
     *
     */
    @Test
    void testToLowerCase()
    {
        CharToken charToken = new CharToken().append(" AbCdEf!§$% ");

        assertEquals(12, charToken.length());
        assertEquals(" AbCdEf!§$% ", charToken.toString());
        assertEquals(" abcdef!§$% ", charToken.toLowerCase().toString());
    }

    /**
     *
     */
    @Test
    void testToUpperCase()
    {
        CharToken charToken = new CharToken().append(" AbCdEf!§$% ");

        assertEquals(12, charToken.length());
        assertEquals(" AbCdEf!§$% ", charToken.toString());
        assertEquals(" ABCDEF!§$% ", charToken.toUpperCase().toString());
    }

    /**
     *
     */
    @Test
    void testTrim()
    {
        CharToken charToken = new CharToken().append(' ').append('a').append(' ').append(" a ");

        assertEquals(6, charToken.length());
        assertEquals(" a  a ", charToken.toString());
        assertEquals("a  a", charToken.toTrimmedString());

        charToken = new CharToken();

        assertEquals(0, charToken.length());
        assertEquals("", charToken.toString());
        assertEquals("", charToken.toTrimmedString());
    }
}
