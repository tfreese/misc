package flyweight;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Thomas Freese
 */
public interface State
{
	/**
     * 
     */
	public static final int CONTACTS = 1;

	/**
     * 
     */
	public static final int ADDRESSES = 2;

	/**
     * 
     */
	public static final int MAXIMUM_STATE_VALUE = 2;

	/**
	 * @param type int
	 */
	public void edit(int type);

	/**
	 * @param f {@link File}
	 * @param s {@link Serializable}
	 * @param type int
	 * @throws IOException Falls was schief geht
	 */
	public void save(File f, Serializable s, int type) throws IOException;
}
