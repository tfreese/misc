// Created: 12.01.2010
/**
 * 12.01.2010
 */
package de.freese.littlemina.imap;

import de.freese.littlemina.core.acceptor.NioSocketAcceptor;

/**
 * @author Thomas Freese
 */
public class TestIMAP
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		NioSocketAcceptor acceptor = new NioSocketAcceptor();

		try
		{
			acceptor.setHandler(new ImapHandler());

			acceptor.bind(143);
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
