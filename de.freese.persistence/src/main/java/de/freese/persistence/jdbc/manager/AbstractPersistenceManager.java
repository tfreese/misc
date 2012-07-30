package de.freese.persistence.jdbc.manager;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.persistence.exception.PersistenceException;
import de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject;
import de.freese.persistence.jdbc.objecttransaction.ObjectTransaction;
import de.freese.persistence.jdbc.statement.AbstractStatement;
import de.freese.persistence.jdbc.statement.DeleteByOIDSTMT;

/**
 * PersistenceManager eines Objektes.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter Typ des persistenten Objektes.
 */
public abstract class AbstractPersistenceManager<T extends AbstractJDBCPersistenceObject>
		implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 7002770518239701869L;

	/**
	 * 
	 */
	protected AbstractStatement deleteSTMT = null;

	/**
	 * 
	 */
	protected AbstractStatement insertSTMT = null;

	/**
	 * 
	 */
	protected AbstractStatement selectAllSTMT = null;

	/**
	 * 
	 */
	protected AbstractStatement updateSTMT = null;

	/**
	 * 
	 */
	private Connection connection = null;

	/**
	 *
	 */
	private final Logger logger;

	/**
	 * Creates a new {@link AbstractPersistenceManager} object.
	 * 
	 * @param connection {@link Connection}
	 */
	public AbstractPersistenceManager(final Connection connection)
	{
		super();

		setConnection(connection);
		this.logger = LoggerFactory.getLogger(getClass());

		try
		{
			ObjectTransaction.getInstance().begin();
		}
		catch (NotSupportedException ex)
		{
			getLogger().error("", ex);
		}
		catch (SystemException ex)
		{
			getLogger().error("", ex);
		}
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void delete(final AbstractJDBCPersistenceObject po) throws PersistenceException
	{
		if (getDeleteSTMT() == null)
		{
			return;
		}

		po.preDelete();

		// return(T)
		getDeleteSTMT().execute(po);
	}

	/**
	 * @return {@link Connection}
	 */
	public Connection getConnection()
	{
		return this.connection;
	}

	/**
	 * @return {@link AbstractStatement}
	 */
	protected AbstractStatement getDeleteSTMT()
	{
		if (this.deleteSTMT == null)
		{
			this.deleteSTMT = new DeleteByOIDSTMT(this);
		}

		return this.deleteSTMT;
	}

	/**
	 * @return {@link AbstractStatement}
	 */
	protected abstract AbstractStatement getInsertSTMT();

	// /**
	// *
	// *
	// * @return
	// */
	// public AbstractJDBCPersistenceObject createNewObject()
	// {
	// AbstractJDBCPersistenceObject po = null;
	//
	// try
	// {
	// po = (AbstractJDBCPersistenceObject) getPOClass().newInstance();
	//
	// po.setPersistenceManager(this);
	//
	// }
	// catch (InstantiationException e)
	// {
	// log.error(e);
	// }
	// catch (IllegalAccessException e)
	// {
	// log.error(e);
	// }
	//
	// return po;
	// }

	/**
	 * @return {@link Logger}
	 */
	protected Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * @return {@link AbstractStatement}
	 */
	protected abstract AbstractStatement getSelectAllSTMT();

	/**
	 * @return String
	 */
	public abstract String getTable();

	/**
	 * @return {@link AbstractStatement}
	 */
	protected abstract AbstractStatement getUpdateSTMT();

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void insert(final AbstractJDBCPersistenceObject po) throws PersistenceException
	{
		if (getInsertSTMT() == null)
		{
			return;
		}

		po.preInsert();

		// return (T)
		getInsertSTMT().execute(po);
	}

	/**
	 * Freigeben aller Resourcen.
	 */
	public void release()
	{
		try
		{
			if (this.selectAllSTMT != null)
			{
				this.selectAllSTMT.close();
			}
		}
		catch (Throwable th)
		{
			getLogger().error("", th);
		}

		try
		{
			if (this.deleteSTMT != null)
			{
				this.deleteSTMT.close();
			}
		}
		catch (Throwable th)
		{
			getLogger().error("", th);
		}

		try
		{
			if (this.insertSTMT != null)
			{
				this.insertSTMT.close();
			}
		}
		catch (Throwable th)
		{
			getLogger().error("", th);
		}

		try
		{
			if (this.updateSTMT != null)
			{
				this.updateSTMT.close();
			}
		}
		catch (Throwable th)
		{
			getLogger().error("", th);
		}
	}

	/**
	 * Liefert alle Objekte.
	 * 
	 * @return Object
	 * @throws PersistenceException Falls was schief geht.
	 */
	@SuppressWarnings("unchecked")
	public List<T> selectALL() throws PersistenceException
	{
		if (getSelectAllSTMT() == null)
		{
			return null;
		}

		return (List<T>) getSelectAllSTMT().execute();
	}

	/**
	 * @param parentID long
	 * @return {@link List}
	 * @throws PersistenceException Falls was schief geht.
	 */
	public abstract List<T> selectByParentID(long parentID) throws PersistenceException;

	/**
	 * @param connection {@link Connection}
	 */
	public void setConnection(final Connection connection)
	{
		this.connection = connection;
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws PersistenceException Falls was schief geht.
	 */
	public void update(final AbstractJDBCPersistenceObject po) throws PersistenceException
	{
		if (getUpdateSTMT() == null)
		{
			return;
		}

		po.preUpdate();

		// return
		getUpdateSTMT().execute(po);
	}
}
