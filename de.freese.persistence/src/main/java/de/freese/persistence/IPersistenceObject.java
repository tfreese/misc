/*
 * Created on 16.07.2004
 */
package de.freese.persistence;

import java.io.Serializable;

/**
 * Interface eines persistenten Objektes.
 * 
 * @author Thomas Freese
 */
public interface IPersistenceObject extends Serializable
{
	/**
	 * Timestamp der letzten Aenderung.
	 * 
	 * @return String
	 */
	public String getLastModifiedTimeStamp();

	/**
	 * PrimaryKey des Objectes.
	 * 
	 * @return long
	 */
	public long getObjectID();
}
