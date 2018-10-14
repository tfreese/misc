/**
 * Created: 30.10.2016
 */

package de.freese.jsync.alt.impl;

import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Objects;
import java.util.Set;

import de.freese.jsync.alt.api.SyncItem;
import de.freese.jsync.api.Group;
import de.freese.jsync.api.User;

/**
 * Basis-Implementierung für ein Verzeichnis / Datei, welche es zu Synchronisieren gilt.<br>
 * Der Pfad ist relativ zum Basis-Verzeichnis.
 *
 * @author Thomas Freese
 */
abstract class AbstractSyncItem implements SyncItem
{
    /**
     * @author Thomas Freese
     * @param <T> Konkreter Typ
     */
    protected static abstract class AbstractBuilder<T extends AbstractSyncItem>
    {
        /**
        *
        */
        private final T item;

        /**
         * Erstellt ein neues {@link AbstractBuilder} Object.
         *
         * @param path String
         */
        protected AbstractBuilder(final String path)
        {
            super();

            this.item = createItem(path);
        }

        /**
         * @return {@link SyncItem}
         */
        public T build()
        {
            return this.item;
        }

        /**
         * @param group {@link Group}
         */
        public void group(final Group group)
        {
            getItem().group = group;
        }

        /**
         * @param lastModifiedTime long
         */
        public void lastModifiedTime(final long lastModifiedTime)
        {
            getItem().lastModifiedTime = lastModifiedTime;
        }

        /**
         * @param permissions {@link Set}<PosixFilePermission>
         */
        public void permissions(final Set<PosixFilePermission> permissions)
        {
            getItem().permissions = permissions;
        }

        /**
         * @param user {@link User}
         */
        public void user(final User user)
        {
            getItem().user = user;
        }

        /**
         * Erzeugt ein neues {@link SyncItem}.
         *
         * @param path String
         * @return {@link AbstractSyncItem}
         */
        protected abstract T createItem(String path);

        /**
         * @return {@link AbstractSyncItem}
         */
        protected T getItem()
        {
            return this.item;
        }
    }

    /**
    *
    */
    private final String path;

    /**
    *
    */
    protected Group group = null;

    /**
    *
    */
    protected long lastModifiedTime = 0;

    /**
    *
    */
    protected Set<PosixFilePermission> permissions = null;

    /**
    *
    */
    protected User user = null;

    /**
     * Erstellt ein neues {@link AbstractSyncItem} Object.
     *
     * @param path String
     */
    protected AbstractSyncItem(final String path)
    {
        super();

        Objects.requireNonNull(path, "path required");

        this.path = path;
    }

    /**
     * @return {@link Group}
     */
    @Override
    public Group getGroup()
    {
        return this.group;
    }

    /**
     * @see de.freese.jsync.api.SyncItem#getLastModifiedTime()
     */
    @Override
    public long getLastModifiedTime()
    {
        return this.lastModifiedTime;
    }

    /**
     * @see de.freese.jsync.alt.api.SyncItem#getPath()
     */
    @Override
    public String getPath()
    {
        return this.path;
    }

    /**
     * @see de.freese.jsync.api.SyncItem#getPermissions()
     */
    @Override
    public Set<PosixFilePermission> getPermissions()
    {
        return this.permissions;
    }

    /**
     * @see de.freese.jsync.api.SyncItem#getPermissionsToString()
     */
    @Override
    public String getPermissionsToString()
    {
        if ((getPermissions() == null) || (getPermissions().size() == 0))
        {
            return null;
        }

        return PosixFilePermissions.toString(getPermissions());
    }

    /**
     * @return {@link User}
     */
    @Override
    public User getUser()
    {
        return this.user;
    }
}
