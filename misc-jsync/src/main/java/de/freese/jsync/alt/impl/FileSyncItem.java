/**
 * Created: 30.10.2016
 */

package de.freese.jsync.alt.impl;

/**
 * Object für die Informationen einer Datei.<br>
 * Der Pfad ist relativ zum Basis-Verzeichnis.
 *
 * @author Thomas Freese
 */
public class FileSyncItem extends AbstractSyncItem
{
    /**
     * @author Thomas Freese
     */
    public static class Builder extends AbstractBuilder<FileSyncItem>
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
         * @param checksum String
         */
        public void checksum(final String checksum)
        {
            getItem().checksum = checksum;
        }

        /**
         * @param size long
         */
        public void size(final long size)
        {
            getItem().size = size;
        }

        /**
         * @see de.freese.jsync.alt.impl.AbstractSyncItem.AbstractBuilder#createItem(java.lang.String)
         */
        @Override
        protected FileSyncItem createItem(final String path)
        {
            return new FileSyncItem(path);
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
    *
    */
    private String checksum = null;

    /**
    *
    */
    private long size = 0;

    /**
     * Erstellt ein neues {@link FileSyncItem} Object.
     *
     * @param path String
     */
    public FileSyncItem(final String path)
    {
        super(path);
    }

    /**
     * @return String
     */
    public String getChecksum()
    {
        return this.checksum;
    }

    /**
     * @return long
     */
    public long getSize()
    {
        return this.size;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder2 = new StringBuilder();
        builder2.append("FileSyncItem [getPath()=");
        builder2.append(getPath());
        builder2.append("]");

        return builder2.toString();
    }
}
