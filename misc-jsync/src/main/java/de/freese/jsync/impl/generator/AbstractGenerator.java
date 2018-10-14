// Created: 05.04.2018
package de.freese.jsync.impl.generator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.xml.bind.DatatypeConverter;
import de.freese.jsync.api.Generator;
import de.freese.jsync.api.Options;

/**
 * Basis-Implementierung des {@link Generator}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractGenerator implements Generator
{
    /**
    *
    */
    protected static final LinkOption[] LINKOPTION_EMPTY = new LinkOption[0];

    /**
    *
    */
    protected static final LinkOption[] LINKOPTION_NO_SYMLINKS = new LinkOption[]
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
     * Erzeugt eine neue Instanz von {@link AbstractGenerator}.
     *
     * @param options {@link Options}
     * @param base {@link Path}
     */
    public AbstractGenerator(final Options options, final Path base)
    {
        super();

        this.options = Objects.requireNonNull(options, "options required");
        this.base = Objects.requireNonNull(base, "base required");
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

                buffer.clear();
            }
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
}
