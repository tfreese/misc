/**
 *
 */
package de.freese.queryengine;

import java.util.ArrayList;
import java.util.List;

import de.freese.queryengine.condition.ICondition;

/**
 * Queryobject der QueryEngine.
 * 
 * @author Thomas Freese
 */
public final class Query
{
	/**
     * 
     */
	private final List<ICondition> conditions = new ArrayList<>();

	/**
     * 
     */
	private final Object target;

	/**
     * 
     */
	private Object[] filter = null;

	/**
     * 
     */
	private Object[] orderBy = null;

	/**
	 * Creates a new {@link Query} object.
	 * 
	 * @param target Object; Ziel der Query
	 * @throws IllegalArgumentException Falls was schief geht.
	 */
	public Query(final Object target)
	{
		super();

		if (target == null)
		{
			throw new IllegalArgumentException("Query Target ist null !");
		}

		this.target = target;
	}

	/**
	 * Hinzufuegen einer Condition.
	 * 
	 * @param condition {@link ICondition}
	 */
	public void addCondition(final ICondition condition)
	{
		this.conditions.add(condition);
	}

	/**
	 * Liefert alle Conditions des Requests.
	 * 
	 * @return List aus {@link ICondition}s
	 */
	public List<ICondition> getConditions()
	{
		return this.conditions;
	}

	/**
	 * Liefert den Filter einer Query.
	 * 
	 * @return Object[]
	 */
	public Object[] getFilter()
	{
		return this.filter;
	}

	/**
	 * Liefert die Sortierung einer Query.
	 * 
	 * @return Object[]
	 */
	public Object[] getOrderBy()
	{
		return this.orderBy;
	}

	/**
	 * Liefert das Ziel der Query.
	 * 
	 * @return Object
	 */
	public Object getTarget()
	{
		return this.target;
	}

	/**
	 * Setzt den Filter einer Query.
	 * 
	 * @param filter Object[]
	 */
	public void setFilter(final Object...filter)
	{
		this.filter = filter;
	}

	/**
	 * Liefert die Sortierung einer Query.
	 * 
	 * @param orderBy Object[]
	 */
	public void setOrderBy(final Object...orderBy)
	{
		this.orderBy = orderBy;
	}
}
