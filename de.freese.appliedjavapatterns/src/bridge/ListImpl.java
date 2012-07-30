package bridge;

/**
 * @author Thomas Freese
 */
public interface ListImpl
{
	/**
	 * @param item String
	 */
	public void addItem(String item);

	/**
	 * @param item String
	 * @param position int
	 */
	public void addItem(String item, int position);

	/**
	 * @param index int
	 * @return String
	 */
	public String getItem(int index);

	/**
	 * @return int
	 */
	public int getNumberOfItems();

	/**
	 * @param item String
	 */
	public void removeItem(String item);

	/**
	 * @return boolean
	 */
	public boolean supportsOrdering();
}
