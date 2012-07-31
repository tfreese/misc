/**
 * 
 */
package de.freese.jgoodies.binding.simple;

import com.jgoodies.binding.beans.Model;

/**
 * @author Thomas Freese
 */
public class MyBean extends Model
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7779158223966541164L;

	/**
	 * 
	 */
	private boolean booleanValue = false;

	/**
	 * 
	 */
	private String stringValue = null;

	/**
	 * Erstellt ein neues {@link MyBean} Objekt.
	 */
	public MyBean()
	{
		super();
	}

	/**
	 * @return String
	 */
	public String getStringValue()
	{
		return this.stringValue;
	}

	/**
	 * @return String
	 */
	public boolean isBooleanValue()
	{
		return this.booleanValue;
	}

	/**
	 * @param booleanValue boolean
	 */
	public void setBooleanValue(final boolean booleanValue)
	{
		System.out.println("Boolean value set: " + booleanValue);

		boolean oldValue = booleanValue;
		this.booleanValue = booleanValue;

		firePropertyChange("booleanValue", oldValue, booleanValue);
	}

	/**
	 * @param stringValue String
	 */
	public void setStringValue(final String stringValue)
	{
		System.out.println("String value set: " + stringValue);

		String oldValue = stringValue;
		this.stringValue = stringValue;

		firePropertyChange("stringValue", oldValue, stringValue);
	}
}
