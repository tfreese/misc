/**
 * Created: 30.09.2012
 */

package de.freese.cdi.weld.tellermachine;

import javax.enterprise.inject.Produces;

/**
 * @author Thomas Freese
 */
public class TransportFactory
{
	/**
	 * Erstellt ein neues {@link TransportFactory} Object.
	 */
	public TransportFactory()
	{
		super();
	}

	/**
	 * @return {@link IATMTransport}
	 */
	@Produces
	public IATMTransport createTransport()
	{
		System.out.println("ATMTransport created with producer");

		return new StandardAtmTransport();
	}
}
