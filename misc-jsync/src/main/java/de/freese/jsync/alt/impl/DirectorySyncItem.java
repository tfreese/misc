/**
 * Created: 30.10.2016
 */

package de.freese.jsync.alt.impl;

/**
 * Object für die Informationen eines Verzeichnisses.<br>
 * Der Pfad ist relativ zum Basis-Verzeichnis.
 *
 * @author Thomas Freese
 */
public class DirectorySyncItem extends AbstractSyncItem
{
    /**
     * @author Thomas Freese
     */
    public static class Builder extends AbstractBuilder<DirectorySyncItem>
    {
        /**
         * Erstellt ein neues {@link Builder} Object.
         *
         * @param path String
         */
        private Builder(final String path)
        {
            super(path);
        }

        /**
         * @see de.freese.jsync.alt.impl.AbstractSyncItem.AbstractBuilder#createItem(java.lang.String)
         */
        @Override
        protected DirectorySyncItem createItem(final String path)
        {
            return new DirectorySyncItem(path);
        }
    }

    /**
     * Erzeugt einen neuen Builder.
     *
     * @param path String
     * @return {@link Builder}
     */
    public static Builder builder(final String path)
    {
        return new Builder(path);
    }

    /**
     * Erstellt ein neues {@link DirectorySyncItem} Object.
     *
     * @param path String
     */
    public DirectorySyncItem(final String path)
    {
        super(path);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DirectorySyncItem [getPath()=");
        builder.append(getPath());
        builder.append("]");

        return builder.toString();
    }
}
