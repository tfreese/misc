/**
 * Created: 22.10.2016
 */
package de.freese.jsync.alt.impl;

import java.util.Objects;

import de.freese.jsync.alt.SyncStatus;
import de.freese.jsync.alt.api.SyncItem;

/**
 * Object für die Informationen der Source- und Destination.<br>
 * Der Pfad ist relativ zum Basis-Verzeichnis.
 *
 * @author Thomas Freese
 */
public class SyncPair
{
    /**
    *
    */
    private final SyncItem destination;

    /**
     *
     */
    private final SyncItem source;

    /**
     *
     */
    private SyncStatus status = null;

    /**
     * Erstellt ein neues {@link SyncPair} Object.
     *
     * @param source {@link SyncItem}
     * @param destination {@link SyncItem}
     */
    public SyncPair(final SyncItem source, final SyncItem destination)
    {
        super();

        this.source = source;
        this.destination = destination;
    }

    /**
     * @return {@link SyncItem}
     */
    public SyncItem getDestination()
    {
        return this.destination;
    }

    /**
     * @return String
     */
    public String getPath()
    {
        return getSource() != null ? getSource().getPath() : getDestination().getPath();
    }

    /**
     * @return {@link SyncItem}
     */
    public SyncItem getSource()
    {
        return this.source;
    }

    /**
     * Liefert den Status der Datei.
     *
     * @return {@link SyncStatus}
     */
    public SyncStatus getStatus()
    {
        return this.status;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SyncPair [path=");
        builder.append(getPath());
        builder.append(", status=");
        builder.append(getStatus());
        builder.append("]");

        return builder.toString();
    }

    /**
     * Vergleicht die Datei in der Quelle (Source) mit dem Ziel (Destination).
     *
     * @throws IllegalStateException Falls was schief geht.
     */
    public void validateStatus() throws IllegalStateException
    {
        if ((getSource() == null) && (getDestination() != null))
        {
            // Löschen: In Source nicht vorhanden, aber in Dest.
            this.status = SyncStatus.ONLY_IN_DEST;
        }
        else if ((getSource() != null) && (getDestination() == null))
        {
            // Kopieren: In Source vorhanden, aber nicht in Dest.
            this.status = SyncStatus.ONLY_IN_SOURCE;
        }
        else if ((getSource() != null) && (getDestination() != null))
        {
            // Kopieren: Attribute unterschiedlich.
            if (getSource().getLastModifiedTime() != getDestination().getLastModifiedTime())
            {
                this.status = SyncStatus.DIFFERENT_CONTENT;
            }
            else if (!Objects.equals(getSource().getPermissionsToString(), getDestination().getPermissionsToString()))
            {
                this.status = SyncStatus.DIFFERENT_PERMISSIONS;
            }
            else if (!Objects.equals(getSource().getUser(), getDestination().getUser()))
            {
                this.status = SyncStatus.DIFFERENT_PERMISSIONS;
            }
            else if (!Objects.equals(getSource().getGroup(), getDestination().getGroup()))
            {
                this.status = SyncStatus.DIFFERENT_PERMISSIONS;
            }

            if (getSource() instanceof FileSyncItem)
            {
                FileSyncItem src = (FileSyncItem) getSource();
                FileSyncItem dst = (FileSyncItem) getDestination();

                if (src.getSize() != dst.getSize())
                {
                    this.status = SyncStatus.DIFFERENT_CONTENT;
                }
                else if (!Objects.equals(src.getChecksum(), dst.getChecksum()))
                {
                    this.status = SyncStatus.DIFFERENT_CONTENT;
                }
            }

            // Alle Prüfungen ohne Unterschied.
            if (this.status == null)
            {
                this.status = SyncStatus.SYNCHRONIZED;
            }
        }
        else
        {
            throw new IllegalStateException("unknown SyncStatus");
        }
    }
}
