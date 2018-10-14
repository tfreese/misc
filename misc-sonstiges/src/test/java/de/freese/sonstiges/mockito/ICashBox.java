/**
 * 06.11.2013
 */
package de.freese.sonstiges.mockito;

/**
 * @author Thomas Freese
 */
public interface ICashBox
{
	/**
	 * @return int
	 */
	public int getCurrentAmount();

	/**
	 * @param amountRequired int
	 */
	public void withdraw(int amountRequired);
}
