// Created: 14.10.2009
/**
 * 14.10.2009
 */
package de.freese.simulationen.wator;

import org.apache.commons.pool.PoolableObjectFactory;

/**
 * ObjectFactory fuer WaTor-Zellen.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractObjectFactory implements PoolableObjectFactory<AbstractWatorCell>
{
	/**
	 * Erstellt ein neues {@link AbstractObjectFactory} Object.
	 */
	public AbstractObjectFactory()
	{
		super();
	}

	/**
	 * @see org.apache.commons.pool.PoolableObjectFactory#activateObject(java.lang.Object)
	 */
	@Override
	public void activateObject(final AbstractWatorCell obj) throws Exception
	{
		// System.out.println("AbstractObjectFactory.activateObject()");
	}

	/**
	 * @see org.apache.commons.pool.PoolableObjectFactory#destroyObject(java.lang.Object)
	 */
	@Override
	public void destroyObject(final AbstractWatorCell obj) throws Exception
	{
		// System.out.println("AbstractObjectFactory.destroyObject()");
		passivateObject(obj);
	}

	/**
	 * @see org.apache.commons.pool.PoolableObjectFactory#passivateObject(java.lang.Object)
	 */
	@Override
	public void passivateObject(final AbstractWatorCell obj) throws Exception
	{
		// System.out.println("AbstractObjectFactory.passivateObject()");
		AbstractWatorCell cell = obj;
		cell.setXY(-1, -1);
		cell.setEnergy(0);
		cell.setWorld(null);
	}

	/**
	 * @param obj {@link AbstractWatorCell}
	 * @return boolean
	 * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(java.lang.Object)
	 */
	@Override
	public boolean validateObject(final AbstractWatorCell obj)
	{
		return true;
	}
}
