/**
 * Created: 03.10.2011
 */

package de.freese.sonstiges.imap;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * @author Thomas Freese
 */
public class MailAuthenticator extends Authenticator
{
	/**
	 * 
	 */
	private final String userName;

	/**
	 * 
	 */
	private final String password;

	/**
	 * Erstellt ein neues {@link MailAuthenticator} Object.
	 * 
	 * @param userName String
	 * @param password String
	 */
	public MailAuthenticator(final String userName, final String password)
	{
		super();

		this.userName = userName;
		this.password = password;
	}

	/**
	 * @see javax.mail.Authenticator#getPasswordAuthentication()
	 */
	@Override
	protected PasswordAuthentication getPasswordAuthentication()
	{
		return new PasswordAuthentication(this.userName, this.password);
	}
}
