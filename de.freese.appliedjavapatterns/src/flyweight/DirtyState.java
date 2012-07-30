package flyweight;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Thomas Freese
 */
public class DirtyState implements State
{
	/**
     * 
     */
	private int stateFlags;

	/**
	 * Erstellt ein neues {@link DirtyState} Object.
	 */
	DirtyState()
	{
		super();
	}

	/**
	 * @param stateType int
	 */
	public void decrementStateValue(final int stateType)
	{
		if ((stateType > 0) && (stateType <= MAXIMUM_STATE_VALUE))
		{
			this.stateFlags = this.stateFlags ^ stateType;
		}
	}

	/**
	 * @see flyweight.State#edit(int)
	 */
	@Override
	public void edit(final int type)
	{
		incrementStateValue(type);
	}

	/**
	 * @param stateType int
	 */
	public void incrementStateValue(final int stateType)
	{
		if ((stateType > 0) && (stateType <= MAXIMUM_STATE_VALUE))
		{
			this.stateFlags = this.stateFlags | stateType;
		}
	}

	/**
	 * @see flyweight.State#save(java.io.File, java.io.Serializable, int)
	 */
	@Override
	public void save(final File file, final Serializable data, final int stateType)
		throws IOException
	{
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(fos);

		out.writeObject(data);
		decrementStateValue(stateType);

		if (this.stateFlags == 0)
		{
			StateFactory.setCurrentState(StateFactory.CLEAN);
		}
	}
}
