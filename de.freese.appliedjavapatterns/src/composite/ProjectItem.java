package composite;

import java.io.Serializable;

/**
 * @author Thomas Freese
 */
public interface ProjectItem extends Serializable
{
	/**
	 * @return double
	 */
	public double getTimeRequired();
}
