/**
 * Created: 30.09.2012
 */

package de.freese.cdi.weld.tellermachine;

/**
 * @author Thomas Freese
 */
public interface IATMTransport
{
	/**
	 * @param packet byte[]
	 */
	public void communicateWithBank(byte[] packet);
}
