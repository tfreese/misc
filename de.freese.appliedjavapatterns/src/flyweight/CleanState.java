package flyweight;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Thomas Freese
 */
public class CleanState implements State
{
	/**
	 * Erstellt ein neues {@link CleanState} Object.
	 */
	CleanState()
	{
		super();
	}

	/**
	 * @see flyweight.State#edit(int)
	 */
	@Override
	public void edit(final int type)
	{
		StateFactory.setCurrentState(StateFactory.DIRTY);
		((DirtyState) StateFactory.DIRTY).incrementStateValue(type);
	}

	/**
	 * @see flyweight.State#save(java.io.File, java.io.Serializable, int)
	 */
	@Override
	public void save(final File file, final Serializable s, final int type) throws IOException
	{
		// Empty
	}
}
