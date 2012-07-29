package iterator;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author Thomas Freese
 */
public interface Iterating extends Serializable
{
	/**
	 * @return {@link Iterator}
	 */
	public Iterator<?> getIterator();
}
