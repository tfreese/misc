/*
 * Created on 21.10.2004
 */
package de.freese.persistence.jdbc.objecttransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.utils.concurrent.event.ThreadQueueEventHandler;
import de.freese.persistence.exception.PersistenceException;
import de.freese.persistence.jdbc.model.AbstractJDBCPersistenceObject;
import de.freese.persistence.jdbc.state.PersistenceStateEvent;
import de.freese.persistence.jdbc.state.PersistenceStateListener;

/**
 * ObjectTransaction
 * 
 * @author Thomas Freese
 */
public class ObjectTransaction implements Transaction, TransactionManager, Status,
		PersistenceStateListener
{
	/**
	 * 
	 */
	private static final ObjectTransaction INSTANCE = createNewInstance();

	/**
	 * 
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(ObjectTransaction.class);

	/**
	 * @return {@link ObjectTransaction}
	 */
	public static synchronized ObjectTransaction createNewInstance()
	{
		return new ObjectTransaction();
	}

	/**
	 * @return {@link ObjectTransaction}
	 */
	public static ObjectTransaction getInstance()
	{
		return INSTANCE;
	}

	/**
	 * 
	 */
	private List<Connection> connectionList = null;

	/**
	 * 
	 */
	protected ThreadQueueEventHandler eventQueueHandler = null;

	/**
	 * 
	 */
	private List<AbstractJDBCPersistenceObject> poList = null;

	/**
	 * 
	 */
	private int status = STATUS_NO_TRANSACTION;

	/**
	 * 
	 */
	private boolean isOpen = false;

	/**
	 * Creates a new {@link ObjectTransaction} object.
	 */
	private ObjectTransaction()
	{
		super();
	}

	/**
	 * @param listener connectionListener {@link ObjectTransactionListener}
	 */
	public synchronized void addObjectTransactionListener(final ObjectTransactionListener listener)
	{
		getEventQueueHandler().addEventListener(ObjectTransactionListener.class, listener);
	}

	/**
	 * @param poCol {@link Collection}
	 * @throws IllegalStateException Falls was schief geht.
	 */
	public void addPersistenceCollection(final Collection<AbstractJDBCPersistenceObject> poCol)
		throws IllegalStateException
	{
		if (poCol == null)
		{
			return;
		}

		try
		{
			if (getStatus() != STATUS_ACTIVE)
			{
				throw new IllegalStateException("Transaction is not avtive !");
			}
		}
		catch (SystemException ex)
		{
			LOGGER.error("", ex);
		}

		for (AbstractJDBCPersistenceObject po : poCol)
		{
			addPersistenceObject(po);
		}
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 * @throws IllegalStateException Falls was schief geht.
	 */
	public void addPersistenceObject(final AbstractJDBCPersistenceObject po)
		throws IllegalStateException
	{
		if (po == null)
		{
			return;
		}

		try
		{
			if (getStatus() != STATUS_ACTIVE)
			{
				throw new IllegalStateException("Transaction is not avtive !");
			}
		}
		catch (SystemException ex)
		{
			LOGGER.error("", ex);
		}

		setOpen(true);

		if (!getPoList().contains(po))
		{
			getPoList().add(po);
		}

		// Nur eindeutige Connections fuer commit und rollback zulassen !!!
		if (!getConnectionList().contains(po.getPersistenceManager().getConnection()))
		{
			getConnectionList().add(po.getPersistenceManager().getConnection());
		}
	}

	/**
	 * @see javax.transaction.TransactionManager#begin()
	 */
	@Override
	public void begin() throws NotSupportedException, SystemException
	{
		setStatus(STATUS_ACTIVE);
	}

	/**
	 * @see de.freese.persistence.jdbc.state.PersistenceStateListener#changed(de.freese.persistence.jdbc.state.PersistenceStateEvent)
	 */
	@Override
	public void changed(final PersistenceStateEvent event)
	{
		addPersistenceObject((AbstractJDBCPersistenceObject) event.getSource());
	}

	/**
	 * 
	 */
	public void clearTransaction()
	{
		getPoList().clear();
		getConnectionList().clear();

		setStatus(STATUS_NO_TRANSACTION);

		setOpen(false);
	}

	/**
	 * @see javax.transaction.TransactionManager#commit()
	 */
	@Override
	public void commit()
		throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
		SecurityException, IllegalStateException, SystemException
	{
		try
		{
			if (getStatus() != STATUS_ACTIVE)
			{
				throw new IllegalStateException("Transaction is not avtive !");
			}
		}
		catch (SystemException ex)
		{
			LOGGER.error("", ex);
		}

		try
		{
			prepare();
		}
		catch (PersistenceException ex)
		{
			LOGGER.error("", ex);

			throw new SystemException(ex.getMessage());
		}

		LOGGER.info("Commit Transaction");

		setStatus(STATUS_COMMITTING);

		// Die Connections committen
		for (Connection connection : getConnectionList())
		{
			try
			{
				connection.commit();
			}
			catch (SQLException ex)
			{
				LOGGER.error("", ex);

				throw new SystemException(ex.getMessage());
			}
		}

		// Das ganze committen, d.h. ObjectIDs und Timestamps festschreiben, incl. der Children.
		for (AbstractJDBCPersistenceObject po : getPoList())
		{
			try
			{
				po.commitObject();
			}
			catch (PersistenceException ex)
			{
				LOGGER.error("", ex);

				throw new SystemException(ex.getMessage());
			}
		}

		setStatus(STATUS_COMMITTED);

		// Wieder aufräumen
		clearTransaction();
	}

	/**
	 * @see de.freese.persistence.jdbc.state.PersistenceStateListener#complexChanged(de.freese.persistence.jdbc.state.PersistenceStateEvent)
	 */
	@Override
	public void complexChanged(final PersistenceStateEvent event)
	{
		// Empty
	}

	/**
	 * @see de.freese.persistence.jdbc.state.PersistenceStateListener#deleted(de.freese.persistence.jdbc.state.PersistenceStateEvent)
	 */
	@Override
	public void deleted(final PersistenceStateEvent event)
	{
		removePersistenceObject((AbstractJDBCPersistenceObject) event.getSource());
	}

	/**
	 * @see javax.transaction.Transaction#delistResource(javax.transaction.xa.XAResource, int)
	 */
	@Override
	public boolean delistResource(final XAResource arg0, final int arg1)
		throws IllegalStateException, SystemException
	{
		throw new SystemException("Not implemented");
	}

	/**
	 * @see javax.transaction.Transaction#enlistResource(javax.transaction.xa.XAResource)
	 */
	@Override
	public boolean enlistResource(final XAResource arg0)
		throws RollbackException, IllegalStateException, SystemException
	{
		throw new SystemException("Not implemented");
	}

	/**
	 * @param type int
	 */
	protected void fireObjectTransactionEvent(final int type)
	{
		ObjectTransactionEvent event = new ObjectTransactionEvent(this, type);

		getEventQueueHandler().fireThreadQueueEvent(ObjectTransactionListener.class, event);
	}

	/**
	 * @return {@link List}
	 */
	private List<Connection> getConnectionList()
	{
		if (this.connectionList == null)
		{
			this.connectionList = Collections.synchronizedList(new ArrayList<Connection>());
		}

		return this.connectionList;
	}

	/**
	 * @return {@link ThreadQueueEventHandler}
	 */
	protected synchronized ThreadQueueEventHandler getEventQueueHandler()
	{
		if (this.eventQueueHandler == null)
		{
			this.eventQueueHandler = new ThreadQueueEventHandler(Executors.newCachedThreadPool());
		}

		return this.eventQueueHandler;
	}

	/**
	 * @return {@link List}
	 */
	private List<AbstractJDBCPersistenceObject> getPoList()
	{
		if (this.poList == null)
		{
			this.poList =
					Collections.synchronizedList(new ArrayList<AbstractJDBCPersistenceObject>());
		}

		return this.poList;
	}

	/**
	 * @see javax.transaction.TransactionManager#getStatus()
	 */
	@Override
	public int getStatus() throws SystemException
	{
		return this.status;
	}

	/**
	 * @see javax.transaction.TransactionManager#getTransaction()
	 */
	@Override
	public Transaction getTransaction() throws SystemException
	{
		return this;
	}

	/**
	 * @return boolean
	 */
	public boolean isOpen()
	{
		return this.isOpen;
	}

	/**
	 * @throws PersistenceException Falls was schief geht.
	 */
	protected void prepare() throws PersistenceException
	{
		try
		{
			if (getStatus() != STATUS_ACTIVE)
			{
				throw new IllegalStateException("Transaction is not avtive !");
			}
		}
		catch (SystemException ex)
		{
			LOGGER.error("", ex);
		}

		LOGGER.info("Prepare Transaction");

		setStatus(STATUS_PREPARING);

		// Die Transactionen der Connections oeffnen
		for (Connection connection : getConnectionList())
		{
			try
			{
				connection.setAutoCommit(false);
			}
			catch (SQLException ex)
			{
				LOGGER.error("", ex);

				throw new PersistenceException(ex);
			}
		}

		// Je nach Status insert, update oder delete, incl. der Children.
		for (AbstractJDBCPersistenceObject po : getPoList())
		{
			po.executeState();
		}

		setStatus(STATUS_PREPARED);
	}

	/**
	 * @see javax.transaction.Transaction#registerSynchronization(javax.transaction.Synchronization)
	 */
	@Override
	public void registerSynchronization(final Synchronization arg0)
		throws RollbackException, IllegalStateException, SystemException
	{
		throw new SystemException("Not implemented");
	}

	/**
	 * Freigeben aller Resourcen.
	 */
	public void release()
	{
		getPoList().clear();
		this.poList = null;
		getConnectionList().clear();
		this.connectionList = null;

		// getEventQueueHandler().closeEventQueue();
	}

	/**
	 * @param listener connectionListener {@link ObjectTransactionListener}
	 */
	public synchronized void removeObjectTransactionListener(	final ObjectTransactionListener listener)
	{
		getEventQueueHandler().removeEventListener(ObjectTransactionListener.class, listener);
	}

	/**
	 * @param po {@link AbstractJDBCPersistenceObject}
	 */
	public void removePersistenceObject(final AbstractJDBCPersistenceObject po)
	{
		if (po == null)
		{
			return;
		}

		getPoList().remove(po);
	}

	/**
	 * @see javax.transaction.TransactionManager#resume(javax.transaction.Transaction)
	 */
	@Override
	public void resume(final Transaction arg0)
		throws InvalidTransactionException, IllegalStateException, SystemException
	{
		throw new SystemException("Not implemented");
	}

	/**
	 * @see javax.transaction.TransactionManager#rollback()
	 */
	@Override
	public void rollback() throws IllegalStateException, SecurityException, SystemException
	{
		try
		{
			if (getStatus() != STATUS_ACTIVE)
			{
				throw new IllegalStateException("Transaction is not avtive !");
			}
		}
		catch (SystemException ex)
		{
			LOGGER.error("", ex);
		}

		LOGGER.info("Rollback Transaction");

		setStatus(STATUS_ROLLING_BACK);

		// Die Rollback der Transactionen
		for (Connection connection : getConnectionList())
		{
			try
			{
				if (!connection.getAutoCommit())
				{
					connection.rollback();
				}
			}
			catch (SQLException ex)
			{
				LOGGER.error("", ex);

				throw new SystemException(ex.getMessage());
			}
		}

		setStatus(STATUS_ROLLEDBACK);
	}

	/**
	 * @see de.freese.persistence.jdbc.state.PersistenceStateListener#saved(de.freese.persistence.jdbc.state.PersistenceStateEvent)
	 */
	@Override
	public void saved(final PersistenceStateEvent event)
	{
		// Empty
	}

	/**
	 * @param isOpen boolean
	 */
	private void setOpen(final boolean isOpen)
	{
		this.isOpen = isOpen;
	}

	/**
	 * @see javax.transaction.TransactionManager#setRollbackOnly()
	 */
	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException
	{
		throw new SystemException("Not implemented");
	}

	/**
	 * @param state int
	 */
	private void setStatus(final int state)
	{
		this.status = state;
		fireObjectTransactionEvent(state);
	}

	/**
	 * @see javax.transaction.TransactionManager#setTransactionTimeout(int)
	 */
	@Override
	public void setTransactionTimeout(final int arg0) throws SystemException
	{
		throw new SystemException("Not implemented");
	}

	/**
	 * @see javax.transaction.TransactionManager#suspend()
	 */
	@Override
	public Transaction suspend() throws SystemException
	{
		throw new SystemException("Not implemented");
	}

	/**
	 * @see de.freese.persistence.jdbc.state.PersistenceStateListener#toDelete(de.freese.persistence.jdbc.state.PersistenceStateEvent)
	 */
	@Override
	public void toDelete(final PersistenceStateEvent event)
	{
		addPersistenceObject((AbstractJDBCPersistenceObject) event.getSource());
	}
}
