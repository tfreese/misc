/**
 *
 */
package de.freese.queryengine.condition;

import de.freese.queryengine.Query;

/**
 * Bedingung fuer eine {@link Query}.
 * 
 * @author Thomas Freese
 */
public interface ICondition
{
	/**
	 * Liefert den Feldnamen der Condition.
	 * 
	 * @return String
	 */
	public String getFieldName();

	/**
	 * Liefert den Operator der Condition.
	 * 
	 * @return Operator
	 */
	public Operator getOperator();
}
