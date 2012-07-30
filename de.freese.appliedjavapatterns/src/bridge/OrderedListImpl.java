package bridge;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class OrderedListImpl implements ListImpl
{
	/**
     * 
     */
	private List<String> items = new ArrayList<>();

	/**
	 * Erstellt ein neues {@link OrderedListImpl} Object.
	 */
	OrderedListImpl()
	{
		super();
	}

	/**
	 * @see bridge.ListImpl#addItem(java.lang.String)
	 */
	@Override
	public void addItem(final String item)
	{
		if (!this.items.contains(item))
		{
			this.items.add(item);
		}
	}

	/**
	 * @see bridge.ListImpl#addItem(java.lang.String, int)
	 */
	@Override
	public void addItem(final String item, final int position)
	{
		if (!this.items.contains(item))
		{
			this.items.add(position, item);
		}
	}

	/**
	 * @see bridge.ListImpl#getItem(int)
	 */
	@Override
	public String getItem(final int index)
	{
		if (index < this.items.size())
		{
			return this.items.get(index);
		}

		return null;
	}

	/**
	 * @see bridge.ListImpl#getNumberOfItems()
	 */
	@Override
	public int getNumberOfItems()
	{
		return this.items.size();
	}

	/**
	 * @see bridge.ListImpl#removeItem(java.lang.String)
	 */
	@Override
	public void removeItem(final String item)
	{
		if (this.items.contains(item))
		{
			this.items.remove(this.items.indexOf(item));
		}
	}

	/**
	 * @see bridge.ListImpl#supportsOrdering()
	 */
	@Override
	public boolean supportsOrdering()
	{
		return true;
	}
}
