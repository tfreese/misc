/**
 * Created: 22.10.2016
 */

package de.freese.jsync.alt;

/**
 * Merkmale von Unterschieden zwischen Quelle und Ziel.
 *
 * @author Thomas Freese
 */
public enum SyncStatus
{
    /**
     * Prüfsumme unterschiedlich.
     */
    DIFFERENT_CHECKSUM,

    /**
     * Inhalt unterschiedlich.
     */
    DIFFERENT_CONTENT,

    /**
     * Gruppe unterschiedlich.
     */
    DIFFERENT_GROUP,

    /**
     * Timstamp unterschiedlich.
     */
    DIFFERENT_LAST_MODIFIEDTIME,

    /**
     * Berechtigungen unterschiedlich.
     */
    DIFFERENT_PERMISSIONS,

    /**
     * Größe unterschiedlich.
     */
    DIFFERENT_SIZE,

    /**
     * Eigentümer unterschiedlich.
     */
    DIFFERENT_USER,

    /**
     * Ziel muss gelöscht werden.
     */
    ONLY_IN_DEST,

    /**
     * Quelle muss kopiert werden.
     */
    ONLY_IN_SOURCE,

    /**
     * Quelle und Ziel identisch.
     */
    SYNCHRONIZED;
}
