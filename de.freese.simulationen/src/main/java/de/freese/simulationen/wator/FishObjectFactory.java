// Created: 14.10.2009
/**
 * 14.10.2009
 */
package de.freese.simulationen.wator;

/**
 * ObjectFactory fuer {@link FishCell}s.
 * 
 * @author Thomas Freese
 */
public class FishObjectFactory extends AbstractObjectFactory
{
	/**
	 * Erstellt ein neues {@link FishObjectFactory} Object.
	 */
	public FishObjectFactory()
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
		return new FishCell();
	}
}
