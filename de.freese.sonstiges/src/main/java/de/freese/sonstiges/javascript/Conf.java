/**
 * Created: 10.10.2012
 */

package de.freese.sonstiges.javascript;

/**
 * @author Thomas Freese
 */
public class Conf
{
	/**
	 * 
	 */
	private int blocksize = 0;

	/**
	 * 
	 */
	private int threads = 0;

	/**
	 * Erstellt ein neues {@link Conf} Object.
	 */
	public Conf()
	{
		super();
	}

	/**
	 * @return int
	 */
	public int getBlocksize()
	{
		return this.blocksize;
	}

	/**
	 * @return int
	 */
	public int getThreads()
	{
		return this.threads;
	}

	/**
	 * @param blocksize int
	 */
	public void setBlocksize(final int blocksize)
	{
		this.blocksize = blocksize;
	}

	/**
	 * @param threads int
	 */
	public void setThreads(final int threads)
	{
		this.threads = threads;
	}
}
