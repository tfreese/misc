package iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class ToDoListImpl implements ToDoList
{
	/**
	 *
	 */
	private static final long serialVersionUID = -9163422028207769302L;

	/**
     * 
     */
	private List<String> items = new ArrayList<>();

	/**
     * 
     */
	private String listName;

	/**
	 * Erstellt ein neues {@link ToDoListImpl} Object.
	 */
	ToDoListImpl()
	{
		super();
	}

	/**
	 * @see iterator.ToDoList#add(java.lang.String)
	 */
	@Override
	public void add(final String item)
	{
		if (!this.items.contains(item))
		{
			this.items.add(item);
		}
	}

	/**
	 * @see iterator.ToDoList#add(java.lang.String, int)
	 */
	@Override
	public void add(final String item, final int position)
	{
		if (!this.items.contains(item))
		{
			this.items.add(position, item);
		}
	}

	/**
	 * @see iterator.Iterating#getIterator()
	 */
	@Override
	public Iterator<?> getIterator()
	{
		return this.items.iterator();
	}

	/**
	 * @see iterator.ToDoList#getListName()
	 */
	@Override
	public String getListName()
	{
		return this.listName;
	}

	/**
	 * @see iterator.ToDoList#getNumberOfItems()
	 */
	@Override
	public int getNumberOfItems()
	{
		return this.items.size();
	}

	/**
	 * @see iterator.ToDoList#remove(java.lang.String)
	 */
	@Override
	public void remove(final String item)
	{
		if (this.items.contains(item))
		{
			this.items.remove(this.items.indexOf(item));
		}
	}

	/**
	 * @see iterator.ToDoList#setListName(java.lang.String)
	 */
	@Override
	public void setListName(final String newListName)
	{
		this.listName = newListName;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.listName;
	}
}
