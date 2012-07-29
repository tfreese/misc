package interpreter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class Context
{
	/**
	 * 
	 */
	private Map<Object, Object> map = new HashMap<>();

	/**
	 * Erstellt ein neues {@link Context} Object.
	 */
	Context()
	{
		super();
	}

	/**
	 * @param name Object
	 * @param value Object
	 */
	public void addVariable(final Object name, final Object value)
	{
		this.map.put(name, value);
	}

	/**
	 * @param name Object
	 * @return Object
	 */
	public Object get(final Object name)
	{
		return this.map.get(name);
	}
}
