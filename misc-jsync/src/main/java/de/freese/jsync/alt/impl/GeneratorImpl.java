// Created: 28.10.2016
package de.freese.jsync.alt.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import de.freese.jsync.alt.api.Generator;
import de.freese.jsync.alt.api.Options;
import de.freese.jsync.alt.api.SyncItem;
import de.freese.jsync.api.Group;
import de.freese.jsync.api.User;

/**
 * Basis-Implementierung des {@link Generator}.
 *
 * @author Thomas Freese
 */
public class GeneratorImpl implements Generator
{
    /**
     *
     */
    private static final LinkOption[] EMPTY_LINKOPTIONS = new LinkOption[0];

    /**
     *
     */
    private static final LinkOption[] SYM_LINKOPTIONS = new LinkOption[]
    {
            LinkOption.NOFOLLOW_LINKS
    };

    /**
     *
     */
    private final Path base;

    /**
     *
     */
    private final Options options;

    /**
     * Erzeugt eine neue Instanz von {@link GeneratorImpl}
     *
     * @param options {@link Options}
     * @param base {@link Path}
     */
    public GeneratorImpl(final Options options, final Path base)
    {
        super();

        Objects.requireNonNull(options, "options required");
        Objects.requireNonNull(base, "base required");

        this.options = options;
        this.base = base;
    }

    /**
     * @see de.freese.jsync.alt.api.Generator#createFileList()
     */
    @Override
    public FutureTask<List<SyncItem>> createFileList() throws Exception
    {
        if (Files.notExists(getBase()))
        {
            // return CompletableFuture.completedFuture(Collections.emptyList());
            return new FutureTask<>(Collections::emptyList);
        }

        //@formatter:off
        Callable<List<SyncItem>> callable = () -> Files.walk(getBase())
                //.filter(p -> !Files.isDirectory(p))
                .sorted()
                .map(this::toItem)
                .collect(Collectors.toList());
        //@formatter:on

        FutureTask<List<SyncItem>> futureTask = new FutureTask<>(callable);
        // futureTask.run();
        // CompletableFuture.runAsync(futureTask);
        // getOptions().getExecutor().execute(futureTask);

        return futureTask;
    }

    /**
     * @see de.freese.jsync.alt.api.Generator#createFileMap()
     */
    @Override
    public FutureTask<Map<String, SyncItem>> createFileMap() throws Exception
    {
        if (Files.notExists(getBase()))
        {
            // return CompletableFuture.completedFuture(Collections.emptyMap());
            return new FutureTask<>(Collections::emptyMap);
        }

        //@formatter:off
        Callable<Map<String, SyncItem>> callable = () -> Files.walk(getBase())
                //.filter(p -> !Files.isDirectory(p))
                .map(this::toItem)
                .collect(Collectors.toMap(SyncItem::getPath, Function.identity()));
        //@formatter:on

        FutureTask<Map<String, SyncItem>> futureTask = new FutureTask<>(callable);
        // futureTask.run();
        // CompletableFuture.runAsync(futureTask);
        // getOptions().getExecutor().execute(futureTask);

        return futureTask;
    }

    /**
     * @see de.freese.jsync.alt.api.Generator#getBase()
     */
    @Override
    public Path getBase()
    {
        return this.base;
    }

    /**
     * Erzeugt den {@link MessageDigest} für die Generierung der Prüfsumme.<br>
     * <p>
     * Every implementation of the Java platform is required to support the following standard MessageDigest algorithms:<br>
     * MD5<br>
     * SHA-1<br>
     * SHA-256<br>
     *
     * @return {@link MessageDigest}
     * @throws RuntimeException Falls was schief geht.
     */
    protected MessageDigest createMessageDigest() throws RuntimeException
    {
        try
        {
            return MessageDigest.getInstance("SHA-256");
        }
        catch (final NoSuchAlgorithmException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param file {@link Path}
     * @return String
     * @throws IOException Falls was schief geht.
     */
    protected String generateChecksum(final Path file) throws IOException
    {
        MessageDigest messageDigest = createMessageDigest();

        // try (DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest)) {
        // while (digestInputStream.read(buffer) > -1) {
        // }}
        // MessageDigest digest = digestInputStream.getMessageDigest();
        try (ReadableByteChannel srcChannel = Files.newByteChannel(file, StandardOpenOption.READ))
        {
            ByteBuffer buffer = ByteBuffer.allocateDirect(Options.BUFFER_SIZE);

            while (srcChannel.read(buffer) != -1)
            {
                // prepare the buffer to be drained
                buffer.flip();

                messageDigest.update(buffer);

                // If partial transfer, shift remainder down
                // If buffer is empty, same as doing clear()
                // buffer.compact();
                buffer.clear();
            }

            // EOF will leave buffer in fill state
            buffer.flip();

            // make sure the buffer is fully drained.
            while (buffer.hasRemaining())
            {
                messageDigest.update(buffer);
            }

            buffer.clear();
        }

        byte[] checksum = messageDigest.digest();
        String hex = DatatypeConverter.printHexBinary(checksum);

        // String hex = org.apache.commons.codec.binary.Hex.encodeHexString(messageDigest);
        // StringBuilder sb = new StringBuilder(checksum.length * 2);
        //
        // for (byte element : checksum)
        // {
        // sb.append(Integer.toString((element & 0xff) + 0x100, 16).substring(1));
        //
        // String t2 = Integer.toHexString(element); Integer.toHexString(0xFF & digestBuf[i]) // Ignore leading zeros
        // if (t2.length() == 1) {
        // sb.append('0');
        // }
        // String t3 = String.format("%02x", element);
        //
        // getOptions();
        // }
        return hex;
    }

    /**
     * @return {@link Options}
     */
    protected Options getOptions()
    {
        return this.options;
    }

    /**
     * @param directory {@link Path}
     * @param linkOption {@link LinkOption}; wenn {@value LinkOption#NOFOLLOW_LINKS} null dann Follow
     * @return {@link SyncItem}
     */
    protected SyncItem toDirectoryItem(final Path directory, final LinkOption[] linkOption)
    {
        try
        {
            DirectorySyncItem.Builder builder = DirectorySyncItem.builder(getBase().relativize(directory).toString());

            if (Options.IS_WINDOWS)
            {
                long lastModifiedTime = Files.getLastModifiedTime(directory, linkOption).to(TimeUnit.SECONDS);

                builder.lastModifiedTime(lastModifiedTime);
            }
            else if (Options.IS_LINUX)
            {
                // unix:mode
                Map<String, Object> attributes = Files.readAttributes(directory, "unix:lastModifiedTime,permissions,owner,group,uid,gid",
                        linkOption);

                long lastModifiedTime = ((FileTime) attributes.get("lastModifiedTime")).to(TimeUnit.SECONDS);

                @SuppressWarnings("unchecked")
                Set<PosixFilePermission> filePermissions = (Set<PosixFilePermission>) attributes.get("permissions");

                String userName = ((UserPrincipal) attributes.get("owner")).getName();
                String groupName = ((GroupPrincipal) attributes.get("group")).getName();
                int uid = (int) attributes.get("uid");
                int gid = (int) attributes.get("gid");

                builder.lastModifiedTime(lastModifiedTime);
                builder.permissions(filePermissions);
                builder.user(new User(userName, uid));
                builder.group(new Group(groupName, gid));

                // UserPrincipalLookupService lookupService = provider(path).getUserPrincipalLookupService();
                // UserPrincipal joe = lookupService.lookupPrincipalByName("joe");
            }

            return builder.build();
        }
        catch (IOException ioex)
        {
            throw new RuntimeException(ioex);
        }
    }

    /**
     * @param file {@link Path}
     * @param linkOption {@link LinkOption}; wenn {@value LinkOption#NOFOLLOW_LINKS} null dann Follow
     * @return {@link SyncItem}
     */
    protected SyncItem toFileItem(final Path file, final LinkOption[] linkOption)
    {
        try
        {
            FileSyncItem.Builder builder = FileSyncItem.builder(getBase().relativize(file).toString());

            if (Options.IS_WINDOWS)
            {
                BasicFileAttributes basicFileAttributes = Files.readAttributes(file, BasicFileAttributes.class, linkOption);

                long lastModifiedTime = basicFileAttributes.lastModifiedTime().to(TimeUnit.SECONDS);
                long size = basicFileAttributes.size();

                builder.lastModifiedTime(lastModifiedTime);
                builder.size(size);
            }
            else if (Options.IS_LINUX)
            {
                // unix:mode
                Map<String, Object> attributes = Files.readAttributes(file, "unix:lastModifiedTime,size,permissions,owner,group,uid,gid",
                        linkOption);

                long lastModifiedTime = ((FileTime) attributes.get("lastModifiedTime")).to(TimeUnit.SECONDS);
                long size = (long) attributes.get("size");

                @SuppressWarnings("unchecked")
                Set<PosixFilePermission> filePermissions = (Set<PosixFilePermission>) attributes.get("permissions");

                String userName = ((UserPrincipal) attributes.get("owner")).getName();
                String groupName = ((GroupPrincipal) attributes.get("group")).getName();
                int uid = (int) attributes.get("uid");
                int gid = (int) attributes.get("gid");

                builder.lastModifiedTime(lastModifiedTime);
                builder.size(size);
                builder.permissions(filePermissions);
                builder.user(new User(userName, uid));
                builder.group(new Group(groupName, gid));

                // UserPrincipalLookupService lookupService = provider(path).getUserPrincipalLookupService();
                // UserPrincipal joe = lookupService.lookupPrincipalByName("joe");
            }

            if (getOptions().isChecksum())
            {
                String checksum = generateChecksum(file);
                builder.checksum(checksum);
            }

            return builder.build();
        }
        catch (IOException ioex)
        {
            throw new RuntimeException(ioex);
        }
    }

    /**
     * Wenn die {@link LinkOption#NOFOLLOW_LINKS} NICHT null ist, werden SymLinks verfolgt.
     *
     * @param path {@link Path}
     * @return {@link SyncItem}
     */
    protected SyncItem toItem(final Path path)
    {
        LinkOption[] linkOption = getOptions().isFollowSymLinks() ? EMPTY_LINKOPTIONS : SYM_LINKOPTIONS;

        if (Files.isDirectory(path))
        {
            return toDirectoryItem(path, linkOption);
        }

        return toFileItem(path, linkOption);
    }
}
