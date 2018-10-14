/**
 * Created: 12.01.2014
 */

package de.freese.sonstiges.imap;

import de.freese.sonstiges.imap.analyze.CharToken;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Freese
 */
public class TestCharToken
{
	/**
	 * Erstellt ein neues {@link TestCharToken} Object.
	 */
	public TestCharToken()
	{
		super();
	}

	/**
	 * 
	 */
	@Test
	public void testAppendChar()
	{
		CharToken charToken = new CharToken().append(' ').append('a').append(' ');

		Assert.assertEquals(3, charToken.length());
		Assert.assertEquals(" a ", charToken.toString());
	}

	/**
	 * 
	 */
	@Test
	public void testAppendCharSequence()
	{
		CharSequence charToken = new CharToken().append(" a ");

		Assert.assertEquals(3, charToken.length());
		Assert.assertEquals(" a ", charToken.toString());
	}

	/**
	 * 
	 */
	@Test
	public void testappendNull()
	{
		CharToken charToken = new CharToken().appendNull();

		Assert.assertEquals(4, charToken.length());
		Assert.assertEquals("null", charToken.toString());
	}

	/**
	 * 
	 */
	@Test
	public void testCompareTo()
	{
		CharToken charToken1 = new CharToken().appendNull();
		CharToken charToken2 = new CharToken().appendNull();
		Assert.assertEquals(0, charToken1.compareTo(charToken2));
		Assert.assertEquals(0, charToken1.compareTo("null"));
		Assert.assertEquals(0, charToken2.compareTo("null"));

		charToken1 = new CharToken().append('a');
		charToken2 = new CharToken().append('b');
		Assert.assertEquals(0, charToken1.compareTo("a"));
		Assert.assertEquals(0, charToken2.compareTo("b"));
		Assert.assertTrue(charToken1.compareTo(charToken2) < 0);
		Assert.assertTrue(charToken2.compareTo(charToken1) > 0);

		charToken1 = new CharToken().append(" a");
		charToken2 = new CharToken().append('b');
		Assert.assertEquals(0, charToken1.compareTo(" a"));
		Assert.assertEquals(0, charToken2.compareTo("b"));
		Assert.assertTrue(charToken1.compareTo(charToken2) < 0);
		Assert.assertTrue(charToken2.compareTo(charToken1) > 0);

		charToken1 = new CharToken().append("a");
		charToken2 = new CharToken().append("a ");
		Assert.assertEquals(0, charToken1.compareTo("a"));
		Assert.assertEquals(0, charToken2.compareTo("a "));
		Assert.assertTrue(charToken1.compareTo(charToken2) < 0);
		Assert.assertTrue(charToken2.compareTo(charToken1) > 0);
	}

	/**
	 * 
	 */
	@Test
	public void testForceResizeBuffer()
	{
		CharToken charToken = new CharToken();

		charToken.append("0123456789");
		Assert.assertEquals(10, charToken.length());

		charToken.append("0123456789");
		Assert.assertEquals(20, charToken.length());

		Assert.assertEquals("01234567890123456789", charToken.toString());
	}

	/**
    *
    */
	@Test
	public void testResize()
	{
		CharToken charToken = new CharToken();

		charToken.append("01234     ");
		Assert.assertEquals(10, charToken.length());
		Assert.assertEquals("01234     ", charToken.toString());

		charToken.resize();
		Assert.assertEquals(5, charToken.length());
		Assert.assertEquals("01234", charToken.toString());
	}

	/**
	 * 
	 */
	@Test
	public void testToLowerCase()
	{
		CharToken charToken = new CharToken().append(" AbCdEf!§$% ");

		Assert.assertEquals(12, charToken.length());
		Assert.assertEquals(" AbCdEf!§$% ", charToken.toString());
		Assert.assertEquals(" abcdef!§$% ", charToken.toLowerCase().toString());
	}

	/**
	 * 
	 */
	@Test
	public void testToUpperCase()
	{
		CharToken charToken = new CharToken().append(" AbCdEf!§$% ");

		Assert.assertEquals(12, charToken.length());
		Assert.assertEquals(" AbCdEf!§$% ", charToken.toString());
		Assert.assertEquals(" ABCDEF!§$% ", charToken.toUpperCase().toString());
	}

	/**
	 * 
	 */
	@Test
	public void testTrim()
	{
		CharToken charToken = new CharToken().append(' ').append('a').append(' ').append(" a ");

		Assert.assertEquals(6, charToken.length());
		Assert.assertEquals(" a  a ", charToken.toString());
		Assert.assertEquals("a  a", charToken.toTrimmedString());

		charToken = new CharToken();

		Assert.assertEquals(0, charToken.length());
		Assert.assertEquals("", charToken.toString());
		Assert.assertEquals("", charToken.toTrimmedString());
	}
}
