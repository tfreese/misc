/*
 * Created on 29.08.2004
 */
package de.freese.persistence.jdbc.objecttransaction;

/**
 * ObjectTransactionAdapter
 * 
 * @author Thomas Freese
 */
public class ObjectTransactionAdapter implements ObjectTransactionListener
{
	/**
	 * Erstellt ein neues {@link ObjectTransactionAdapter} Object.
	 */
	public ObjectTransactionAdapter()
	{
		super();
	}

	/**
	 * @see de.freese.persistence.jdbc.objecttransaction.ObjectTransactionListener#transactionActive(de.freese.persistence.jdbc.objecttransaction.ObjectTransactionEvent)
	 */
	@Override
	public void transactionActive(final ObjectTransactionEvent event)
	{
		// Empty
	}

	/**
	 * @see de.freese.persistence.jdbc.objecttransaction.ObjectTransactionListener#transactionCommitted(de.freese.persistence.jdbc.objecttransaction.ObjectTransactionEvent)
	 */
	@Override
	public void transactionCommitted(final ObjectTransactionEvent event)
	{
		// Empty
	}

	/**
	 * @see de.freese.persistence.jdbc.objecttransaction.ObjectTransactionListener#transactionCommitting(de.freese.persistence.jdbc.objecttransaction.ObjectTransactionEvent)
	 */
	@Override
	public void transactionCommitting(final ObjectTransactionEvent event)
	{
		// Empty
	}

	/**
	 * @see de.freese.persistence.jdbc.objecttransaction.ObjectTransactionListener#transactionMarkedRollback(de.freese.persistence.jdbc.objecttransaction.ObjectTransactionEvent)
	 */
	@Override
	public void transactionMarkedRollback(final ObjectTransactionEvent event)
	{
		// Empty
	}

	/**
	 * @see de.freese.persistence.jdbc.objecttransaction.ObjectTransactionListener#transactionNoTransaction(de.freese.persistence.jdbc.objecttransaction.ObjectTransactionEvent)
	 */
	@Override
	public void transactionNoTransaction(final ObjectTransactionEvent event)
	{
		// Empty
	}

	/**
	 * @see de.freese.persistence.jdbc.objecttransaction.ObjectTransactionListener#transactionPrepared(de.freese.persistence.jdbc.objecttransaction.ObjectTransactionEvent)
	 */
	@Override
	public void transactionPrepared(final ObjectTransactionEvent event)
	{
		// Empty
	}

	/**
	 * @see de.freese.persistence.jdbc.objecttransaction.ObjectTransactionListener#transactionPreparing(de.freese.persistence.jdbc.objecttransaction.ObjectTransactionEvent)
	 */
	@Override
	public void transactionPreparing(final ObjectTransactionEvent event)
	{
		// Empty
	}

	/**
	 * @see de.freese.persistence.jdbc.objecttransaction.ObjectTransactionListener#transactionRolledBack(de.freese.persistence.jdbc.objecttransaction.ObjectTransactionEvent)
	 */
	@Override
	public void transactionRolledBack(final ObjectTransactionEvent event)
	{
		// Empty
	}

	/**
	 * @see de.freese.persistence.jdbc.objecttransaction.ObjectTransactionListener#transactionRollingBack(de.freese.persistence.jdbc.objecttransaction.ObjectTransactionEvent)
	 */
	@Override
	public void transactionRollingBack(final ObjectTransactionEvent event)
	{
		// Empty
	}

	/**
	 * @see de.freese.persistence.jdbc.objecttransaction.ObjectTransactionListener#transactionUnknown(de.freese.persistence.jdbc.objecttransaction.ObjectTransactionEvent)
	 */
	@Override
	public void transactionUnknown(final ObjectTransactionEvent event)
	{
		// Empty
	}
}
