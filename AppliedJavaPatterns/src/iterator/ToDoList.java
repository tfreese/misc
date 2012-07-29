package iterator;

/**
 * @author Thomas Freese
 */
public interface ToDoList extends Iterating
{
	/**
	 * @param item String
	 */
	public void add(String item);

	/**
	 * @param item String
	 * @param position int
	 */
	public void add(String item, int position);

	/**
	 * @return String
	 */
	public String getListName();

	/**
	 * @return int
	 */
	public int getNumberOfItems();

	/**
	 * @param item String
	 */
	public void remove(String item);

	/**
	 * @param newListName String
	 */
	public void setListName(String newListName);
}
