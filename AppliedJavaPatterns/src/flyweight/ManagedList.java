package flyweight;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class ManagedList
{
	/**
     * 
     */
	private Class<?> classType;

	/**
     * 
     */
	private List<Object> elements = new ArrayList<>();

	/**
	 * Creates a new {@link ManagedList} object.
	 */
	public ManagedList()
	{
		super();
	}

	/**
	 * Creates a new {@link ManagedList} object.
	 * 
	 * @param newClassType {@link Class}
	 */
	public ManagedList(final Class<?> newClassType)
	{
		super();

		this.classType = newClassType;
	}

	/**
	 * @param item Object
	 */
	public void addItem(final Object item)
	{
		if ((item != null) && (this.classType.isInstance(item)))
		{
			this.elements.add(item);
		}
		else
		{
			this.elements.add(item);
		}
	}

	/**
	 * @return {@link List}
	 */
	public List<Object> getItems()
	{
		return this.elements;
	}

	/**
	 * @param item Object
	 */
	public void removeItem(final Object item)
	{
		this.elements.remove(item);
	}

	/**
	 * @param newClassType Class
	 */
	public void setClassType(final Class<?> newClassType)
	{
		this.classType = newClassType;
	}
}
