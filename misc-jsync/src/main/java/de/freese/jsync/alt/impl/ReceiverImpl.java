/**
 * Created: 22.10.2016
 */

package de.freese.jsync.alt.impl;

import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import de.freese.jsync.alt.api.Generator;
import de.freese.jsync.alt.api.Options;
import de.freese.jsync.alt.api.Receiver;
import de.freese.jsync.alt.api.SyncItem;

/**
 * Basis-Implementierung des {@link Receiver}.
 *
 * @author Thomas Freese
 */
public class ReceiverImpl implements Receiver
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
     * Erstellt ein neues {@link ReceiverImpl} Object.
     *
     * @param options {@link Options}
     * @param base {@link Path}
     */
    public ReceiverImpl(final Options options, final Path base)
    {
        super();

        Objects.requireNonNull(options, "options required");
        Objects.requireNonNull(base, "base required");

        this.options = options;
        this.base = base;
    }

    /**
     * @see de.freese.jsync.alt.api.Receiver#createDirectory(java.lang.String, java.lang.String, long, java.lang.String, java.lang.String)
     */
    @Override
    public void createDirectory(final String directory, final String permissions, final long lastModifiedTime, final String group,
            final String user) throws Exception
    {
        Path path = getBase().resolve(directory);

        Files.createDirectory(path);

        updateDirectory(directory, permissions, lastModifiedTime, group, user);
    }

    /**
     * @see de.freese.jsync.alt.api.Receiver#createFile(java.lang.String, java.lang.String, long, java.lang.String, java.lang.String)
     */
    @Override
    public void createFile(final String file, final String permissions, final long lastModifiedTime, final String group, final String user)
            throws Exception
    {
        Path path = getBase().resolve(file);

        Files.createFile(path);

        updateFile(file, permissions, lastModifiedTime, group, user);
    }

    /**
     * @see de.freese.jsync.alt.api.Receiver#createFileMap()
     */
    @Override
    public Future<Map<String, SyncItem>> createFileMap() throws Exception
    {
        Generator generator = new GeneratorImpl(getOptions(), getBase());
        FutureTask<Map<String, SyncItem>> futureTask = generator.createFileMap();

        getOptions().getExecutor().execute(futureTask);

        return futureTask;
    }

    /**
     * @see de.freese.jsync.api.Receiver#delete(java.lang.String)
     */
    @Override
    public void delete(final String fileDir) throws Exception
    {
        Path path = getBase().resolve(fileDir);

        Files.delete(path);
    }

    // /**
    // * @see de.freese.jsync.api.Receiver#deleteDirectory(java.lang.String)
    // */
    // @Override
    // public void deleteDirectory(final String directory) throws Exception
    // {
    // Path path = getBase().resolve(directory);
    //
    // // Sollte hier schon bereits leer sein.
    // Files.delete(path);
    //
    // // Verzeichnis löschen, wenn es leer ist.
    // // if (Files.list(path).count() == 0)
    // // {
    // // Files.delete(path);
    // // }
    // }
    //
    // /**
    // * @see de.freese.jsync.api.Receiver#deleteFile(java.lang.String)
    // */
    // @Override
    // public void deleteFile(final String file) throws Exception
    // {
    // Path path = getBase().resolve(file);
    //
    // Files.delete(path);
    // }

    /**
     * Liefert das Basis-Verzeichnis.
     *
     * @return base {@link Path}
     */
    public Path getBase()
    {
        return this.base;
    }

    /**
     * @see de.freese.jsync.api.Receiver#getChannel(java.lang.String)
     */
    @Override
    public WritableByteChannel getChannel(final String file) throws Exception
    {
        Path path = getBase().resolve(file);

        return Files.newByteChannel(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }

    /**
     * @see de.freese.jsync.alt.api.Receiver#updateDirectory(java.lang.String, java.lang.String, long, java.lang.String, java.lang.String)
     */
    @Override
    public void updateDirectory(final String directory, final String permissions, final long lastModifiedTime, final String group,
            final String user) throws Exception
    {
        update(getBase().resolve(directory), permissions, lastModifiedTime, group, user);
    }

    /**
     * @see de.freese.jsync.alt.api.Receiver#updateFile(java.lang.String, java.lang.String, long, java.lang.String, java.lang.String)
     */
    @Override
    public void updateFile(final String file, final String permissions, final long lastModifiedTime, final String group, final String user)
            throws Exception
    {
        update(getBase().resolve(file), permissions, lastModifiedTime, group, user);
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
     * @param group String
     * @param user String
     * @throws Exception Falls was schief geht.
     */
    protected void update(final Path path, final String permissions, final long lastModifiedTime, final String group, final String user)
            throws Exception
    {
        Set<PosixFilePermission> filePermissions = PosixFilePermissions.fromString(permissions);
        // FileAttribute<Set<PosixFilePermission>> fileAttributePermissions = PosixFilePermissions.asFileAttribute(filePermissions);

        Files.setPosixFilePermissions(path, filePermissions);
        Files.setLastModifiedTime(path, FileTime.from(lastModifiedTime, TimeUnit.SECONDS));

        UserPrincipalLookupService lookupService = path.getFileSystem().getUserPrincipalLookupService();

        PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
        GroupPrincipal groupPrincipal = lookupService.lookupPrincipalByGroupName(group);
        fileAttributeView.setGroup(groupPrincipal);

        UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(user);
        fileAttributeView.setOwner(userPrincipal);
    }
}
