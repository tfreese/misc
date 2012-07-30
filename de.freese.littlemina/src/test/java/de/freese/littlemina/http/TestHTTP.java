// Created: 12.01.2010
/**
 * 12.01.2010
 */
package de.freese.littlemina.http;

import de.freese.littlemina.core.acceptor.NioSocketAcceptor;

/**
 * @author Thomas Freese
 */
public class TestHTTP
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		NioSocketAcceptor acceptor = new NioSocketAcceptor();

		try
		{
			System.out.println("Start server");

			acceptor.setHandler(new HttpHandler());

			acceptor.bind(8081);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			// acceptor.dispose();
		}
	}
}
