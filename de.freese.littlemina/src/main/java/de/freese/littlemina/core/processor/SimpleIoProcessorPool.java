// Created: 10.01.2010
/**
 * 10.01.2010
 */
package de.freese.littlemina.core.processor;

import java.lang.reflect.Constructor;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.core.pool.IDisposeableCallback;
import de.freese.base.core.pool.RoundRobinPool;
import de.freese.littlemina.core.session.IoSession;

/**
 * Ein {@link IoProcessor} Pool, welche {@link IoSession}s auf einen oder mehrere<br>
 * {@link IoProcessor}s verteilt und somit bessere Unterstuetzung fuer<br>
 * Multi-Prozessor Architekturen bietet.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter Typ der {@link IoSession}
 */
public final class SimpleIoProcessorPool<T extends IoSession> extends
		RoundRobinPool<IoProcessor<T>> implements IoProcessor<T>
{
	/**
	 * @author Thomas Freese
	 */
	private class DisposeableCallback implements IDisposeableCallback<IoProcessor<T>>
	{
		/**
		 * Erstellt ein neues {@link DisposeableCallback} Object.
		 */
		public DisposeableCallback()
		{
			super();
		}

		/**
		 * @see de.freese.base.core.pool.IDisposeableCallback#dispose(java.lang.Object)
		 */
		@Override
		public void dispose(final IoProcessor<T> object)
		{
			if (!object.isDisposing())
			{
				object.dispose();
			}
		}
	}

	/**
     *
     */
	private final static Logger LOGGER = LoggerFactory.getLogger(SimpleIoProcessorPool.class);

	/**
	 *
	 */
	private final boolean createdExecutor;

	/**
	 *
	 */
	private final Executor executor;

	/**
	 * Erstellt ein neues {@link SimpleIoProcessorPool} Object.
	 * 
	 * @param processorType Class
	 */
	public SimpleIoProcessorPool(final Class<IoProcessor<T>> processorType)
	{
		this(processorType, null, DEFAULT_SIZE);
	}

	/**
	 * Erstellt ein neues {@link SimpleIoProcessorPool} Object.
	 * 
	 * @param processorType Class
	 * @param executor {@link Executor}
	 */
	public SimpleIoProcessorPool(final Class<IoProcessor<T>> processorType, final Executor executor)
	{
		this(processorType, executor, DEFAULT_SIZE);
	}

	/**
	 * Erstellt ein neues {@link SimpleIoProcessorPool} Object.
	 * 
	 * @param processorType Class
	 * @param executor {@link Executor}
	 * @param size int
	 */
	public SimpleIoProcessorPool(final Class<IoProcessor<T>> processorType,
			final Executor executor, final int size)
	{
		super(processorType, size);

		setDisposeableCallback(new DisposeableCallback());

		if (executor == null)
		{
			this.executor = Executors.newCachedThreadPool();
			this.createdExecutor = true;
		}
		else
		{
			this.executor = executor;
			this.createdExecutor = false;
		}
	}

	/**
	 * Erstellt ein neues {@link SimpleIoProcessorPool} Object.
	 * 
	 * @param processorType Class
	 * @param size int
	 */
	public SimpleIoProcessorPool(final Class<IoProcessor<T>> processorType, final int size)
	{
		this(processorType, null, DEFAULT_SIZE);
	}

	/**
	 * @see de.freese.base.core.pool.RoundRobinPool#dispose()
	 */
	@Override
	public void dispose()
	{
		super.dispose();

		synchronized (getDisposalLock())
		{
			if (this.createdExecutor)
			{
				ExecutorService executorService = (ExecutorService) this.executor;
				executorService.shutdown();

				while (!executorService.isTerminated())
				{
					try
					{
						executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
					}
					catch (InterruptedException ex)
					{
						// Ignore
					}
				}
			}
		}
	}

	/**
	 * @see de.freese.base.core.pool.RoundRobinPool#fillPoolImpl()
	 */
	@Override
	protected void fillPoolImpl() throws Exception
	{
		Constructor<? extends IoProcessor<T>> processorConstructor = null;
		boolean usesExecutorArg = true;

		// We create at least one processor
		try
		{
			try
			{
				processorConstructor = getObjectType().getConstructor(ExecutorService.class);
				getPool().add(processorConstructor.newInstance(this.executor));
			}
			catch (NoSuchMethodException ex)
			{
				// To the next step...
			}

			try
			{
				processorConstructor = getObjectType().getConstructor(Executor.class);
				getPool().add(processorConstructor.newInstance(this.executor));
			}
			catch (NoSuchMethodException ex)
			{
				// To the next step...
			}

			try
			{
				processorConstructor = getObjectType().getConstructor();
				usesExecutorArg = false;
				getPool().add(processorConstructor.newInstance());
			}
			catch (NoSuchMethodException ex)
			{
				// To the next step...
			}
		}
		catch (RuntimeException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new RuntimeException("Failed to create a new instance of "
					+ getObjectType().getName(), ex);
		}

		if (processorConstructor == null)
		{
			// Raise an exception if no proper constructor is found.
			throw new IllegalArgumentException(String.valueOf(getObjectType())
					+ " must have a public constructor " + "with one "
					+ ExecutorService.class.getSimpleName() + " parameter, "
					+ "a public constructor with one " + Executor.class.getSimpleName()
					+ " parameter or a public default constructor.");
		}

		// Constructor found now use it for all subsequent instantiations
		for (int i = 1; i < getSize(); i++)
		{
			try
			{
				if (usesExecutorArg)
				{
					getPool().add(processorConstructor.newInstance(this.executor));
				}
				else
				{
					getPool().add(processorConstructor.newInstance());
				}
			}
			catch (Exception ex)
			{
				// Won't happen because it has been done previously
			}
		}
	}

	/**
	 * Liefert den {@link IoProcessor} einer {@link IoSession}.
	 * 
	 * @param session {@link IoSession}
	 * @return {@link IoProcessor}
	 */
	@SuppressWarnings("unchecked")
	private IoProcessor<T> getProcessor(final T session)
	{
		IoProcessor<T> p = (IoProcessor<T>) session.getAttribute("processor");

		if (p == null)
		{
			p = nextObject();
			IoProcessor<T> oldp = (IoProcessor<T>) session.setAttribute("processor", p);

			if (oldp != null)
			{
				p = oldp;
			}
		}

		return p;
	}

	/**
	 * @see de.freese.littlemina.core.processor.IoProcessor#scheduleAdd(de.freese.littlemina.core.session.IoSession)
	 */
	@Override
	public void scheduleAdd(final T session)
	{
		getProcessor(session).scheduleAdd(session);
	}

	/**
	 * @see de.freese.littlemina.core.processor.IoProcessor#scheduleRemove(de.freese.littlemina.core.session.IoSession)
	 */
	@Override
	public void scheduleRemove(final T session)
	{
		getProcessor(session).scheduleRemove(session);
	}

	/**
	 * @see de.freese.littlemina.core.processor.IoProcessor#scheduleWrite(de.freese.littlemina.core.session.IoSession)
	 */
	@Override
	public void scheduleWrite(final T session) throws Exception
	{
		getProcessor(session).scheduleWrite(session);
	}
}
