/**
 * Created: 30.09.2012
 */

package de.freese.cdi.weld.tellermachine;

import javax.enterprise.inject.Alternative;

/**
 * @author Thomas Freese
 */
@Alternative
public class SoapAtmTransport implements IATMTransport
{
	/**
	 * Erstellt ein neues {@link SoapAtmTransport} Object.
	 */
	public SoapAtmTransport()
	{
		super();
	}

	/**
	 * @see de.freese.cdi.weld.tellermachine.IATMTransport#communicateWithBank(byte[])
	 */
	@Override
	public void communicateWithBank(final byte[] packet)
	{
		System.out.println("communicating with bank via SOAP transport");
	}
}
