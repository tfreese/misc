package bridge;

/**
 * @author Thomas Freese
 */
public class BaseList
{
	/**
     * 
     */
	protected ListImpl implementor;

	/**
	 * @param item String
	 */
	public void add(final String item)
	{
		this.implementor.addItem(item);
	}

	/**
	 * @param item String
	 * @param position int
	 */
	public void add(final String item, final int position)
	{
		if (this.implementor.supportsOrdering())
		{
			this.implementor.addItem(item, position);
		}
	}

	/**
	 * @return int
	 */
	public int count()
	{
		return this.implementor.getNumberOfItems();
	}

	/**
	 * @param index int
	 * @return int
	 */
	public String get(final int index)
	{
		return this.implementor.getItem(index);
	}

	/**
	 * @param item ListImpl
	 */
	public void remove(final String item)
	{
		this.implementor.removeItem(item);
	}

	/**
	 * @param impl ListImpl
	 */
	public void setImplementor(final ListImpl impl)
	{
		this.implementor = impl;
	}
}
