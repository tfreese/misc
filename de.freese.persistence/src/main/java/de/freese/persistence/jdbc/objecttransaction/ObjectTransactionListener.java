/*
 * Created on 12.08.2004
 */
package de.freese.persistence.jdbc.objecttransaction;

import java.util.EventListener;

/**
 * {@link ObjectTransactionListener}
 * 
 * @author Thomas Freese
 */
public interface ObjectTransactionListener extends EventListener
{
	/**
	 * @param event {@link ObjectTransactionEvent}
	 */
	public void transactionActive(ObjectTransactionEvent event);

	/**
	 * @param event {@link ObjectTransactionEvent}
	 */
	public void transactionCommitted(ObjectTransactionEvent event);

	/**
	 * @param event {@link ObjectTransactionEvent}
	 */
	public void transactionCommitting(ObjectTransactionEvent event);

	/**
	 * @param event {@link ObjectTransactionEvent}
	 */
	public void transactionMarkedRollback(ObjectTransactionEvent event);

	/**
	 * @param event {@link ObjectTransactionEvent}
	 */
	public void transactionNoTransaction(ObjectTransactionEvent event);

	/**
	 * @param event {@link ObjectTransactionEvent}
	 */
	public void transactionPrepared(ObjectTransactionEvent event);

	/**
	 * @param event {@link ObjectTransactionEvent}
	 */
	public void transactionPreparing(ObjectTransactionEvent event);

	/**
	 * @param event {@link ObjectTransactionEvent}
	 */
	public void transactionRolledBack(ObjectTransactionEvent event);

	/**
	 * @param event {@link ObjectTransactionEvent}
	 */
	public void transactionRollingBack(ObjectTransactionEvent event);

	/**
	 * @param event {@link ObjectTransactionEvent}
	 */
	public void transactionUnknown(ObjectTransactionEvent event);
}
