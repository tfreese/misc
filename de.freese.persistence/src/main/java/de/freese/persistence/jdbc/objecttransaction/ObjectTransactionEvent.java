/*
 * Created on 12.08.2004
 */
package de.freese.persistence.jdbc.objecttransaction;

import java.util.EventListener;

import javax.transaction.Status;

import de.freese.base.utils.concurrent.event.ThreadQueueEvent;

/**
 * ObjectTransactionEvent
 * 
 * @author Thomas Freese
 */
public class ObjectTransactionEvent extends ThreadQueueEvent implements Status
{
	/**
	 *
	 */
	private static final long serialVersionUID = -3212990166024578268L;

	/**
	 * Erstellt ein neues {@link ObjectTransactionEvent} Object.
	 * 
	 * @param obj Object
	 * @param type int
	 */
	public ObjectTransactionEvent(final ObjectTransaction obj, final int type)
	{
		super(obj, type);
	}

	/**
	 * @see de.freese.base.utils.concurrent.event.ThreadQueueEvent#dispatch(java.util.EventListener)
	 */
	@Override
	protected void dispatch(final EventListener listener)
	{
		ObjectTransactionListener otl = (ObjectTransactionListener) listener;

		switch (getType())
		{
			case STATUS_ACTIVE:
				otl.transactionActive(this);

				return;

			case STATUS_COMMITTED:
				otl.transactionCommitted(this);

				return;

			case STATUS_COMMITTING:
				otl.transactionCommitting(this);

				return;

			case STATUS_MARKED_ROLLBACK:
				otl.transactionMarkedRollback(this);

				return;

			case STATUS_NO_TRANSACTION:
				otl.transactionNoTransaction(this);

				return;

			case STATUS_PREPARED:
				otl.transactionPrepared(this);

				return;

			case STATUS_PREPARING:
				otl.transactionPreparing(this);

				return;

			case STATUS_ROLLEDBACK:
				otl.transactionRolledBack(this);

				return;

			case STATUS_ROLLING_BACK:
				otl.transactionRollingBack(this);

				return;

			case STATUS_UNKNOWN:
				otl.transactionUnknown(this);

				return;

			default:
				return;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		switch (getType())
		{
			case STATUS_ACTIVE:
				return "Active";

			case STATUS_COMMITTED:
				return "Committed";

			case STATUS_COMMITTING:
				return "Committing";

			case STATUS_MARKED_ROLLBACK:
				return "Marked Rollback";

			case STATUS_NO_TRANSACTION:
				return "No Transaction";

			case STATUS_PREPARED:
				return "Prepared";

			case STATUS_PREPARING:
				return "Preparing";

			case STATUS_ROLLEDBACK:
				return "Rolled Back";

			case STATUS_ROLLING_BACK:
				return "Rolling Back";

			case STATUS_UNKNOWN:
				return "Unknown";

			default:
				return "";
		}
	}
}
