/**
 * Created: 30.09.2012
 */

package de.freese.cdi.weld.tellermachine;

import javax.enterprise.inject.Default;

/**
 * @author Thomas Freese
 */
@Default
public class StandardAtmTransport implements IATMTransport
{
	/**
	 * Erstellt ein neues {@link StandardAtmTransport} Object.
	 */
	public StandardAtmTransport()
	{
		super();
	}

	/**
	 * @see de.freese.cdi.weld.tellermachine.IATMTransport#communicateWithBank(byte[])
	 */
	@Override
	public void communicateWithBank(final byte[] packet)
	{
		System.out.println("communicating with bank via Standard transport");
	}
}
