// Created: 05.04.2018
package de.freese.jsync.impl.receiver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import de.freese.jsync.api.Options;
import de.freese.jsync.api.Receiver;
import de.freese.jsync.impl.generator.DirectorySyncItem;
import de.freese.jsync.impl.generator.FileSyncItem;

/**
 * Basis-Implementierung des {@link Receiver}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractReceiver implements Receiver
{
    /**
    *
    */
    private final Path base;

    /**
    *
    */
    private final Options options;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractReceiver}.
     *
     * @param options {@link Options}
     * @param base {@link Path}
     */
    public AbstractReceiver(final Options options, final Path base)
    {
        super();

        this.options = Objects.requireNonNull(options, "options required");
        this.base = Objects.requireNonNull(base, "base required");
    }

    /**
     * @see de.freese.jsync.api.Receiver#delete(java.lang.String)
     */
    @Override
    public void delete(final String fileDir) throws Exception
    {
        Path path = getBase().resolve(fileDir);

        Files.delete(path);

        // Da die Verzeichnisse immer am Ende nach den Dateien gelöscht werden, dürfte dies hier nicht notwendig sein.
        //
        // Path parent = path.getParent();
        //
        // try (Stream<Path> stream = Files.list(parent))
        // {
        // long fileCount = stream.count();
        //
        // if (fileCount == 0)
        // {
        // Files.delete(parent);
        // }
        // }
    }

    /**
     * Liefert das Basis-Verzeichnis.
     *
     * @return base {@link Path}
     */
    protected Path getBase()
    {
        return this.base;
    }

    /**
     * @return {@link Options}
     */
    protected Options getOptions()
    {
        return this.options;
    }

    /**
     * Aktualisiert ein Verzeichnis oder Datei.
     *
     * @param path {@link Path}
     * @param permissions String; In der Form "rwxr-xr-x"
     * @param lastModifiedTime long; TimeUnit = SECONDS
     * @param groupName String
     * @param userName String
     * @throws Exception Falls was schief geht.
     */
    protected void update(final Path path, final String permissions, final long lastModifiedTime, final String groupName, final String userName)
        throws Exception
    {
        Set<PosixFilePermission> filePermissions = PosixFilePermissions.fromString(permissions);
        // FileAttribute<Set<PosixFilePermission>> fileAttributePermissions = PosixFilePermissions.asFileAttribute(filePermissions);

        Files.setPosixFilePermissions(path, filePermissions);
        Files.setLastModifiedTime(path, FileTime.from(lastModifiedTime, TimeUnit.SECONDS));

        UserPrincipalLookupService lookupService = path.getFileSystem().getUserPrincipalLookupService();

        PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
        GroupPrincipal groupPrincipal = lookupService.lookupPrincipalByGroupName(groupName);
        fileAttributeView.setGroup(groupPrincipal);

        UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(userName);
        fileAttributeView.setOwner(userPrincipal);
    }

    /**
     * @see de.freese.jsync.api.Receiver#updateDirectory(de.freese.jsync.impl.generator.DirectorySyncItem)
     */
    @Override
    public void updateDirectory(final DirectorySyncItem syncItem) throws Exception
    {
        Path path = getBase().resolve(syncItem.getRelativePath());

        if (!Files.exists(path))
        {
            Files.createDirectories(path);
        }

        String permissions = syncItem.getPermissionsToString();
        long lastModifiedTime = syncItem.getLastModifiedTime();
        String groupName = syncItem.getGroup().getName();
        String userName = syncItem.getUser().getName();

        update(path, permissions, lastModifiedTime, groupName, userName);
    }

    /**
     * @see de.freese.jsync.api.Receiver#updateFile(de.freese.jsync.impl.generator.FileSyncItem)
     */
    @Override
    public void updateFile(final FileSyncItem syncItem) throws Exception
    {
        Path path = getBase().resolve(syncItem.getRelativePath());
        String permissions = syncItem.getPermissionsToString();
        long lastModifiedTime = syncItem.getLastModifiedTime();
        String groupName = syncItem.getGroup().getName();
        String userName = syncItem.getUser().getName();

        update(path, permissions, lastModifiedTime, groupName, userName);
    }
}
