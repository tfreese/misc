package iterator;

/**
 * @author Thomas Freese
 */
public interface ToDoListCollection extends Iterating
{
	/**
	 * @param list {@link ToDoList}
	 */
	public void add(ToDoList list);

	/**
	 * @return int
	 */
	public int getNumberOfItems();

	/**
	 * @param list {@link ToDoList}
	 */
	public void remove(ToDoList list);
}
