// Created: 14.10.2009
/**
 * 14.10.2009
 */
package de.freese.simulationen.wator;

/**
 * ObjectFactory fuer {@link SharkCell}s.
 * 
 * @author Thomas Freese
 */
public class SharkObjectFactory extends AbstractObjectFactory
{
	/**
	 * Erstellt ein neues {@link SharkObjectFactory} Object.
	 */
	public SharkObjectFactory()
	{
		super();
	}

	/**
	 * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
	 */
	@Override
	public AbstractWatorCell makeObject() throws Exception
	{
		// System.out.println("FishObjectFactory.makeObject()");
		return new SharkCell();
	}
}
