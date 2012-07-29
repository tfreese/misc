package bridge;

/**
 * @author Thomas Freese
 */
public class OrnamentedList extends BaseList
{
	/**
     * 
     */
	private char itemType;

	/**
	 * Erstellt ein neues {@link OrnamentedList} Object.
	 */
	OrnamentedList()
	{
		super();
	}

	/**
	 * @see bridge.BaseList#get(int)
	 */
	@Override
	public String get(final int index)
	{
		return this.itemType + " " + super.get(index);
	}

	/**
	 * @return char
	 */
	public char getItemType()
	{
		return this.itemType;
	}

	/**
	 * @param newItemType char
	 */
	public void setItemType(final char newItemType)
	{
		if (newItemType > ' ')
		{
			this.itemType = newItemType;
		}
	}
}
