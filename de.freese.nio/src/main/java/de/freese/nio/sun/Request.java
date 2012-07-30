package de.freese.nio.sun;

/*
 * @(#)Request.java 1.3 05/11/17 Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An encapsulation of the request received.
 * <P>
 * The static method parse() is responsible for creating this object.
 * 
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 * @version 1.3, 05/11/17
 */
class Request
{
	/**
	 * A helper class for parsing HTTP command actions.
	 * 
	 * @author Thomas Freese
	 */
	static class Action
	{
		/**
		 * 
		 */
		static Action GET = new Action("GET");

		/**
		 * 
		 */
		static Action PUT = new Action("PUT");

		/**
		 * 
		 */
		static Action POST = new Action("POST");

		/**
		 * 
		 */
		static Action HEAD = new Action("HEAD");

		/**
		 * @param s String
		 * @return {@link Action}
		 * @throws IllegalArgumentException Falls was schief geht
		 */
		static Action parse(final String s)
		{
			if (s.equals("GET"))
			{
				return GET;
			}

			if (s.equals("PUT"))
			{
				return PUT;
			}

			if (s.equals("POST"))
			{
				return POST;
			}

			if (s.equals("HEAD"))
			{
				return HEAD;
			}

			throw new IllegalArgumentException(s);
		}

		/**
		 * 
		 */
		private String name;

		/**
		 * Creates a new {@link Action} object.
		 * 
		 * @param name String
		 */
		private Action(final String name)
		{
			this.name = name;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return this.name;
		}
	}

	/**
	 * 
	 */
	private static Charset ascii = Charset.forName("US-ASCII");

	/**
	 * The expected message format is first compiled into a pattern, and is then compared against
	 * the inbound character buffer to determine if there is a match. This convienently tokenizes
	 * our request into usable pieces. This uses Matcher "expression capture groups" to tokenize
	 * requests like: GET /dir/file HTTP/1.1 Host: hostname into: group[1] = "GET" group[2] =
	 * "/dir/file" group[3] = "1.1" group[4] = "hostname" The text in between the parens are used to
	 * captured the regexp text.
	 */
	private static Pattern requestPattern = Pattern.compile(
			"\\A([A-Z]+) +([^ ]+) +HTTP/([0-9\\.]+)$" + ".*^Host: ([^ ]+)$.*\r\n\r\n\\z",
			Pattern.MULTILINE | Pattern.DOTALL);

	/**
	 * @param bb {@link ByteBuffer}
	 * @return boolean
	 */
	static boolean isComplete(final ByteBuffer bb)
	{
		int p = bb.position() - 4;

		if (p < 0)
		{
			return false;
		}

		return (((bb.get(p + 0) == '\r') && (bb.get(p + 1) == '\n') && (bb.get(p + 2) == '\r') && (bb
				.get(p + 3) == '\n')));
	}

	/**
	 * @param bb {@link ByteBuffer}
	 * @return {@link Request}
	 * @throws MalformedRequestException Falls was schief geht
	 */
	static Request parse(final ByteBuffer bb) throws MalformedRequestException
	{
		CharBuffer cb = ascii.decode(bb);
		Matcher m = requestPattern.matcher(cb);

		if (!m.matches())
		{
			throw new MalformedRequestException();
		}

		Action a;

		try
		{
			a = Action.parse(m.group(1));
		}
		catch (IllegalArgumentException x)
		{
			throw new MalformedRequestException();
		}

		URI u;

		try
		{
			u = new URI("http://" + m.group(4) + m.group(2));
		}
		catch (URISyntaxException x)
		{
			throw new MalformedRequestException();
		}

		return new Request(a, m.group(3), u);
	}

	/**
	 * 
	 */
	private Action action;

	/**
	 * 
	 */
	private URI uri;

	/**
	 * 
	 */
	private String version;

	/**
	 * Creates a new {@link Request} object.
	 * 
	 * @param a {@link Action}
	 * @param v String
	 * @param u {@link URI}
	 */
	private Request(final Action a, final String v, final URI u)
	{
		this.action = a;
		this.version = v;
		this.uri = u;
	}

	/**
	 * @return {@link Action}
	 */
	Action action()
	{
		return this.action;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return (this.action + " " + this.version + " " + this.uri);
	}

	/**
	 * @return {@link URI}
	 */
	URI uri()
	{
		return this.uri;
	}

	/**
	 * @return String
	 */
	String version()
	{
		return this.version;
	}
}
