package bridge;

/**
 * @author Thomas Freese
 */
public class NumberedList extends BaseList
{
	/**
	 * Erstellt ein neues {@link NumberedList} Object.
	 */
	NumberedList()
	{
		super();
	}

	/**
	 * @see bridge.BaseList#get(int)
	 */
	@Override
	public String get(final int index)
	{
		return (index + 1) + ". " + super.get(index);
	}
}
