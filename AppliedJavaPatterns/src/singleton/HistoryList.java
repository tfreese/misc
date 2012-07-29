package singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class HistoryList
{
	/**
     * 
     */
	private static HistoryList instance = new HistoryList();

	/**
	 * @return {@link HistoryList}
	 */
	public static HistoryList getInstance()
	{
		return instance;
	}

	/**
     * 
     */
	private List<String> history = Collections.synchronizedList(new ArrayList<String>());

	/**
	 * Creates a new {@link HistoryList} object.
	 */
	private HistoryList()
	{
		super();
	}

	/**
	 * @param command String
	 */
	public void addCommand(final String command)
	{
		this.history.add(command);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < this.history.size(); i++)
		{
			result.append("  ");
			result.append(this.history.get(i));
			result.append("\n");
		}

		return result.toString();
	}

	/**
	 * @return Object
	 */
	public Object undoCommand()
	{
		return this.history.remove(this.history.size() - 1);
	}
}
