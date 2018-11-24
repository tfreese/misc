/**
 * Created: 03.07.2011
 */
package de.freese.littlemina.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Ein stark vereinfachter ObjektPool.<br>
 * Die Objekte werden nicht wie bei einem herkömmlichen Pool (commons-pool) für die Verwendung gesperrt,<br>
 * sondern im Round-Robin Verfahren bereitgestellt und erst erzeugt, wenn diese benötigt werden..<br>
 * Die Default Grösse des Pools beträgt <code>Runtime.availableProcessors() + 1</code>.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Objekttyp
 */
public class RoundRobinPool<T>
{
    /**
     *
     */
    public static final int DEFAULT_SIZE = Runtime.getRuntime().availableProcessors() + 1;

    /**
     *
     */
    private Supplier<T> creator = null;

    /**
     *
     */
    private volatile Boolean disposed = null;

    /**
     *
     */
    private Consumer<T> disposer = null;

    /**
    *
    */
    private int index = 0;

    // /**
    // *
    // */
    // private final AtomicInteger poolID = new AtomicInteger(0);

    /**
     *
     */
    private final List<T> pool;

    /**
     *
     */
    private final int size;

    /**
     * Erstellt ein neues {@link RoundRobinPool} Object.
     *
     * @param creator {@link Supplier}
     * @param disposer {@link Consumer}; optional
     */
    public RoundRobinPool(final Supplier<T> creator, final Consumer<T> disposer)
    {
        this(creator, disposer, DEFAULT_SIZE);
    }

    /**
     * Erstellt ein neues {@link RoundRobinPool} Object.
     *
     * @param creator {@link Supplier}
     * @param disposer {@link Consumer}; optional
     * @param size int
     */
    public RoundRobinPool(final Supplier<T> creator, final Consumer<T> disposer, final int size)
    {
        super();

        Objects.requireNonNull(creator, "creator required");

        if (size <= 0)
        {
            throw new IllegalArgumentException("size: " + size + " (expected: positive integer)");
        }

        this.creator = creator;
        this.disposer = disposer;
        this.size = size;
        this.pool = new ArrayList<>(size);
    }

    /**
     * Freigeben aller Resourcen.
     */
    public void dispose()
    {
        if (isDisposing())
        {
            return;
        }

        this.disposed = Boolean.FALSE;

        for (Iterator<T> iterator = getPool().iterator(); iterator.hasNext();)
        {
            T poolObject = iterator.next();

            if (this.disposer != null)
            {
                this.disposer.accept(poolObject);
            }

            iterator.remove();
        }

        this.disposed = Boolean.TRUE;
    }

    /**
     * @return {@link List}
     */
    protected List<T> getPool()
    {
        return this.pool;
    }

    /**
     * @return int
     */
    protected int getSize()
    {
        return this.size;
    }

    /**
     * Liefert <tt>true</tt>, wenn das Disposing beendet ist.
     *
     * @return boolean
     */
    public final boolean isDisposed()
    {
        return Boolean.TRUE.equals(this.disposed);
    }

    /**
     * Liefert <tt>true</tt>, wenn dispose aufgerufen worden ist.
     *
     * @return boolean
     */
    public final boolean isDisposing()
    {
        return Boolean.FALSE.equals(this.disposed);
    }

    /**
     * Liefert ein Objekt aus dem Pool.
     *
     * @return Object
     */
    public final T nextObject()
    {
        if (isDisposed())
        {
            throw new IllegalStateException("A disposed ObjectPool can not be accessed.");
        }

        if (getPool().size() < getSize())
        {
            getPool().add(this.creator.get());
        }

        // int index = Math.abs(this.poolID.getAndIncrement()) % getSize();
        //
        // return getPool().get(index);

        T object = getPool().get(this.index++);

        if (this.index == getSize())
        {
            this.index = 0;
        }

        return object;
    }
}
