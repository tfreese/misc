// Created: 05.04.2018
package de.freese.jsync.impl.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import de.freese.jsync.api.Generator;
import de.freese.jsync.api.Group;
import de.freese.jsync.api.Options;
import de.freese.jsync.api.SyncItem;
import de.freese.jsync.api.User;

/**
 * Default-Implementierung des {@link Generator}.
 *
 * @author Thomas Freese
 */
public class DefaultGenerator extends AbstractGenerator
{
    /**
     * Erzeugt eine neue Instanz von {@link DefaultGenerator}.
     *
     * @param options {@link Options}
     * @param base {@link Path}
     */
    public DefaultGenerator(final Options options, final Path base)
    {
        super(options, base);
    }

    /**
     * @see de.freese.jsync.api.Generator#createSyncItems()
     */
    @Override
    public RunnableFuture<Map<String, SyncItem>> createSyncItems()
    {
        if (Files.notExists(getBase()))
        {
            // return CompletableFuture.completedFuture(Collections.emptyMap());
            return new FutureTask<>(Collections::emptyMap);
        }

        //@formatter:off
        Callable<Map<String, SyncItem>> callable = () -> Files.walk(getBase())
                //.parallel()
                //.filter(p -> !Files.isDirectory(p))
                .map(this::toItem)
                //.collect(Collectors.toMap(SyncItem::getRelativePath, Function.identity()));
                .collect(Collectors.toMap(SyncItem::getRelativePath, Function.identity(),
                        (v1, v2) -> { throw new IllegalStateException(String.format("Duplicate key %s", v1)); },
                        ()-> Collections.synchronizedMap(new TreeMap<>()))); // TreeMap::new
        //@formatter:on

        FutureTask<Map<String, SyncItem>> futureTask = new FutureTask<>(callable);
        // futureTask.run();
        // CompletableFuture.runAsync(futureTask);
        // getOptions().getExecutor().execute(futureTask);

        return futureTask;
    }

    /**
     * @param directory {@link Path}
     * @param linkOption {@link LinkOption}; wenn {@value LinkOption#NOFOLLOW_LINKS} null dann Follow
     * @return {@link SyncItem}
     * @throws IOException Falls was schief geht.
     */
    protected SyncItem toDirectoryItem(final Path directory, final LinkOption[] linkOption) throws IOException
    {
        DirectorySyncItem syncItem = new DirectorySyncItem(getBase().relativize(directory).toString());

        if (Options.IS_WINDOWS)
        {
            long lastModifiedTime = Files.getLastModifiedTime(directory, linkOption).to(TimeUnit.SECONDS);

            syncItem.setLastModifiedTime(lastModifiedTime);
        }
        else if (Options.IS_LINUX)
        {
            // unix:mode
            Map<String, Object> attributes = Files.readAttributes(directory, "unix:lastModifiedTime,permissions,owner,group,uid,gid", linkOption);

            long lastModifiedTime = ((FileTime) attributes.get("lastModifiedTime")).to(TimeUnit.SECONDS);

            @SuppressWarnings("unchecked")
            Set<PosixFilePermission> filePermissions = (Set<PosixFilePermission>) attributes.get("permissions");

            String userName = ((UserPrincipal) attributes.get("owner")).getName();
            String groupName = ((GroupPrincipal) attributes.get("group")).getName();
            int uid = (int) attributes.get("uid");
            int gid = (int) attributes.get("gid");

            syncItem.setLastModifiedTime(lastModifiedTime);
            syncItem.setPermissions(filePermissions);
            syncItem.setUser(new User(userName, uid));
            syncItem.setGroup(new Group(groupName, gid));

            // UserPrincipalLookupService lookupService = provider(path).getUserPrincipalLookupService();
            // UserPrincipal joe = lookupService.lookupPrincipalByName("joe");
        }

        return syncItem;
    }

    /**
     * @param file {@link Path}
     * @param linkOption {@link LinkOption}; wenn {@value LinkOption#NOFOLLOW_LINKS} null dann Follow
     * @return {@link SyncItem}
     * @throws IOException Falls was schief geht.
     */
    protected SyncItem toFileItem(final Path file, final LinkOption[] linkOption) throws IOException
    {
        FileSyncItem syncItem = new FileSyncItem(getBase().relativize(file).toString());

        if (Options.IS_WINDOWS)
        {
            // PosixFileAttributes für Unix.
            BasicFileAttributes basicFileAttributes = Files.readAttributes(file, BasicFileAttributes.class, linkOption);

            long lastModifiedTime = basicFileAttributes.lastModifiedTime().to(TimeUnit.SECONDS);
            long size = basicFileAttributes.size();

            syncItem.setLastModifiedTime(lastModifiedTime);
            syncItem.setSize(size);
        }
        else if (Options.IS_LINUX)
        {
            // posix:*, basic:*, unix:*
            Map<String, Object> attributes = Files.readAttributes(file, "unix:lastModifiedTime,size,permissions,owner,group,uid,gid", linkOption);

            long lastModifiedTime = ((FileTime) attributes.get("lastModifiedTime")).to(TimeUnit.SECONDS);
            long size = (long) attributes.get("size");

            @SuppressWarnings("unchecked")
            Set<PosixFilePermission> filePermissions = (Set<PosixFilePermission>) attributes.get("permissions");

            String userName = ((UserPrincipal) attributes.get("owner")).getName();
            String groupName = ((GroupPrincipal) attributes.get("group")).getName();
            int uid = (int) attributes.get("uid");
            int gid = (int) attributes.get("gid");

            syncItem.setLastModifiedTime(lastModifiedTime);
            syncItem.setSize(size);
            syncItem.setPermissions(filePermissions);
            syncItem.setUser(new User(userName, uid));
            syncItem.setGroup(new Group(groupName, gid));

            // UserPrincipalLookupService lookupService = provider(path).getUserPrincipalLookupService();
            // UserPrincipal joe = lookupService.lookupPrincipalByName("joe");
        }

        if (getOptions().isChecksum())
        {
            String checksum = generateChecksum(file);
            syncItem.setChecksum(checksum);
        }

        return syncItem;
    }

    /**
     * Wenn die {@link Options#isFollowSymLinks} true ist, werden SymLinks verfolgt.
     *
     * @param path {@link Path}
     * @return {@link SyncItem}
     */
    protected SyncItem toItem(final Path path)
    {
        LinkOption[] linkOption = getOptions().isFollowSymLinks() ? LINKOPTION_EMPTY : LINKOPTION_NO_SYMLINKS;

        try
        {
            if (Files.isDirectory(path))
            {
                return toDirectoryItem(path, linkOption);
            }

            return toFileItem(path, linkOption);
        }
        catch (IOException ioex)
        {
            throw new RuntimeException(ioex);
        }
    }
}
