/**
 * Created: 30.09.2012
 */

package de.freese.cdi.weld.tellermachine;

/**
 * @author Thomas Freese
 */
public interface IAutomatedTellerMachine
{
	/**
	 * @param bd float
	 */
	public void deposit(float bd);

	/**
	 * @param bd float
	 */
	public abstract void withdraw(float bd);
}
