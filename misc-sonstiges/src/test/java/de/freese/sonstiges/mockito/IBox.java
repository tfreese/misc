/**
 * 06.11.2013
 */
package de.freese.sonstiges.mockito;

/**
 * @author Thomas Freese
 */
public interface IBox
{
	/**
	 * @return int
	 */
	public int getPrice();

	/**
	 * @return boolean
	 */
	public boolean isEmpty();

	/**
	 * 
	 */
	public void releaseItem();
}
