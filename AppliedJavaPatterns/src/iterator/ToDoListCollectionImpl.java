package iterator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class ToDoListCollectionImpl implements ToDoListCollection
{
	/**
	 *
	 */
	private static final long serialVersionUID = -1744448286517033736L;

	/**
     * 
     */
	private Map<String, ToDoList> lists = new HashMap<>();

	/**
	 * Erstellt ein neues {@link ToDoListCollectionImpl} Object.
	 */
	ToDoListCollectionImpl()
	{
		super();
	}

	/**
	 * @see iterator.ToDoListCollection#add(iterator.ToDoList)
	 */
	@Override
	public void add(final ToDoList list)
	{
		if (!this.lists.containsKey(list.getListName()))
		{
			this.lists.put(list.getListName(), list);
		}
	}

	/**
	 * @see iterator.Iterating#getIterator()
	 */
	@Override
	public Iterator<?> getIterator()
	{
		return this.lists.values().iterator();
	}

	/**
	 * @see iterator.ToDoListCollection#getNumberOfItems()
	 */
	@Override
	public int getNumberOfItems()
	{
		return this.lists.size();
	}

	/**
	 * @see iterator.ToDoListCollection#remove(iterator.ToDoList)
	 */
	@Override
	public void remove(final ToDoList list)
	{
		if (this.lists.containsKey(list.getListName()))
		{
			this.lists.remove(list.getListName());
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getClass().toString();
	}
}
